package sys.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import sys.model.AnalysisStatus;
import sys.model.Report;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sys.repository.ReportRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "txt", "docs");
    private static final long MAX_FILE_SIZE = 1_048_576;

    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public Optional<Report> getReportById(Long id) {
        return reportRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Report> getReportByDocumentId(Long documentId) {
        return reportRepository.findByDocumentId(documentId);
    }

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
            if (fileSize > MAX_FILE_SIZE) {
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
                message.append("Файл не имеет расширения. ");
            }

        } catch (IOException e) {
            status = AnalysisStatus.FAILED;
            message.append("Ошибка чтения файла: ").append(e.getMessage());
        }
        Report report = Report.builder().documentId(documentId)
                .fileFormat(fileFormat).fileSize(fileSize).status(status)
                .errorMessage(message.toString()).build();
        return reportRepository.save(report);
    }
}
