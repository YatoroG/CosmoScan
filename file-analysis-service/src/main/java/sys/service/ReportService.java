package sys.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.model.AnalysisStatus;
import sys.model.Report;
import sys.repository.ReportRepository;
import sys.utils.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "txt", "docx");
    private static final long MAX_FILE_SIZE = 1_048_576;

    private final ReportRepository reportRepository;
    private final WordCloudService worldCloudService;

    @Transactional(readOnly = true)
    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Отчет с id = " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public Report getReportByDocumentId(Long documentId) {
        return reportRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Отчет для документа с id = " +
                        documentId + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report addReport(Long documentId, String path) {
        Path filePath = Paths.get(path);
        long fileSize = 0;
        String fileFormat = "none";
        AnalysisStatus status = AnalysisStatus.SUCCESS;
        StringBuilder message = new StringBuilder();

        try {
            fileSize = Files.size(filePath);
            if (fileSize >= MAX_FILE_SIZE) {
                status = AnalysisStatus.FAILED;
                message.append("Размер документа превышает 1 МБ.");
            }

            String fileName = filePath.getFileName().toString();
            if (fileName.contains(".")) {
                fileFormat = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

                if (!ALLOWED_EXTENSIONS.contains(fileFormat)) {
                    status = AnalysisStatus.FAILED;
                    message.append("Формат \"").append(fileFormat).append("\" запрещен. ");
                }
            } else {
                status = AnalysisStatus.FAILED;
                message.append("Документ не имеет расширения. ");
            }

        } catch (IOException e) {
            log.error("Ошибка чтения документа: {}", e.getMessage());
            status = AnalysisStatus.FAILED;
            message.append("Ошибка чтения документа: ").append(e.getMessage());
        }

        byte[] cloud = null;
        if (status == AnalysisStatus.SUCCESS && "txt".equalsIgnoreCase(fileFormat)) {
            cloud = worldCloudService.generate(path);
        }

        Report report = Report.builder().documentId(documentId)
                .fileFormat(fileFormat).fileSize(fileSize).status(status)
                .errorMessage(message.toString()).wordCloud(cloud).build();
        return reportRepository.save(report);
    }
}
