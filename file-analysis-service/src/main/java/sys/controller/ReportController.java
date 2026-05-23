package sys.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sys.model.Report;
import sys.model.requests.AnalysisRequest;
import sys.service.ReportService;
import sys.utils.exception.EntityNotFoundException;

@Slf4j
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/overview/{id}")
    public Report getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @GetMapping("/overview/{documentId}/document")
    public Report getReportByDocumentId(@PathVariable Long documentId) {
        return reportService.getReportByDocumentId(documentId);
    }

    @GetMapping("/overview")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping(value = "/overview/{id}/cloud", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getWordCloudImage(@PathVariable Long id) {
        Report report = reportService.getReportById(id);
        if (report.getWordCloud() == null) {
            throw new EntityNotFoundException("Не найдено облако слов");
        }
        return report.getWordCloud();
    }

    @PostMapping(path = "/launch")
    @ResponseStatus(HttpStatus.CREATED)
    public Report launchAnalysis(@RequestBody AnalysisRequest request) {
        return reportService.addReport(request.documentId(), request.filePath());
    }
}
