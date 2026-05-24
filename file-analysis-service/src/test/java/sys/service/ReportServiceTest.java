package sys.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sys.model.AnalysisStatus;
import sys.model.Report;
import sys.repository.ReportRepository;
import sys.utils.exception.EntityNotFoundException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private ReportRepository reportRepository;

    @Mock
    private WordCloudService worldCloudService;

    @InjectMocks
    private ReportService reportService;

    @TempDir
    Path tempDir;

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Добавление отчета о файле, который соответствует критериям")
        void testAddReportFile() throws Exception {
            var resource = getClass().getClassLoader().getResource("samples/sample_ok.txt");
            assertNotNull(resource);

            Path path = Path.of(resource.toURI());
            String absolutePath = path.toAbsolutePath().toString();
            when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

            Report result = reportService.addReport(1L, absolutePath);

            verify(worldCloudService, times(1)).generate(absolutePath);
            assertTrue(result.getFileSize() > 0);
            assertEquals(AnalysisStatus.SUCCESS, result.getStatus());
        }
    }

    @Test
    @DisplayName("Получение отчета по id")
    void testGetReportById() {
        Report report = Report.builder().id(1L).documentId(1L).build();

        when(reportRepository.findByDocumentId(1L)).thenReturn(Optional.of(report));
        Report result = reportService.getReportByDocumentId(1L);
        assertEquals(1L, result.getDocumentId());
    }

    @Test
    @DisplayName("Получение всех отчетов")
    void testGetAllReports() {
        Report report1 = Report.builder().id(1L).documentId(1L).build();
        Report report2 = Report.builder().id(2L).documentId(2L).build();
        List<Report> mockReports = List.of(report1, report2);
        when(reportRepository.findAll()).thenReturn(mockReports);

        List<Report> result = reportService.getAllReports();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(reportRepository, times(1)).findAll();
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("Добавление отчета о файле, размер которого больше или равен 1 Мб")
        void testAddLargeReportFile() throws Exception {
            var resource = getClass().getClassLoader().getResource("samples/sample_large.pdf");
            assertNotNull(resource);

            Path path = Path.of(resource.toURI());
            String absolutePath = path.toAbsolutePath().toString();
            when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

            Report result = reportService.addReport(1L, absolutePath);

            assertTrue(result.getFileSize() > 0);
            assertEquals(AnalysisStatus.FAILED, result.getStatus());
            assertTrue(result.getErrorMessage().contains("превышает 1 МБ"));
        }

        @Test
        @DisplayName("Добавление отчета о файле, расширение которого не разрешено")
        void testAddZipReportFile() throws Exception {
            var resource = getClass().getClassLoader().getResource("samples/sample_zip.zip");
            assertNotNull(resource);

            Path path = Path.of(resource.toURI());
            String absolutePath = path.toAbsolutePath().toString();
            when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

            Report result = reportService.addReport(1L, absolutePath);

            assertTrue(result.getFileSize() > 0);
            assertEquals(AnalysisStatus.FAILED, result.getStatus());
            assertTrue(result.getErrorMessage().contains("запрещен"));
        }

        @Test
        @DisplayName("Добавление отчета о файле, который не имеет расширения")
        void testAddExtensionlessReportFile() throws Exception {
            Path file = Files.createFile(tempDir.resolve("file"));
            when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

            Report result = reportService.addReport(1L, file.toString());

            assertEquals(AnalysisStatus.FAILED, result.getStatus());
            assertTrue(result.getErrorMessage().contains("не имеет расширения"));
        }

        @Test
        @DisplayName("Добавление отчета о файле с несуществующим путем")
        void addReport_Exception_PathNotFound() {
            when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

            Report result = reportService.addReport(1L, "non/existent/path");

            assertEquals(AnalysisStatus.FAILED, result.getStatus());
            assertTrue(result.getErrorMessage().contains("Ошибка чтения"));
        }

        @Test
        void getReportById_ThrowsException() {
            when(reportRepository.findById(1L)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> reportService.getReportById(1L));
        }
    }
}
