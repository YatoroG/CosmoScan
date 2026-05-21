package sys.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import sys.model.Report;
import sys.model.requests.AnalysisRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sys.service.ReportService;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/overview/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/overview/{documentId}")
    public ResponseEntity<Report> getReportByDocumentId(@PathVariable Long documentId) {
        return reportService.getReportByDocumentId(documentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/overview")
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @PostMapping(path = "/launch")
    public ResponseEntity<Report> launchAnalysis(@RequestBody AnalysisRequest request) {
        Report newReport = reportService.addReport(request.documentId(), request.filePath());
        return ResponseEntity.status(HttpStatus.CREATED).body(newReport);
    }

}
