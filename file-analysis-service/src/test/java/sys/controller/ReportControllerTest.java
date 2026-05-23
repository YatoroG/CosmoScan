package sys.controller;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sys.model.AnalysisStatus;
import sys.model.Report;
import sys.model.requests.AnalysisRequest;
import sys.service.ReportService;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@DisplayName("Мок-тесты контроллера отчетов")
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Report report = new Report(
            1L,
            1L,
            2L,
            "txt",
            AnalysisStatus.SUCCESS,
            null,
            new byte[]{23, 76, 98}
    );

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Просмотр отчета после анализа конкретного документа")
        void testShowOverviewByReportId() throws Exception {
            when(reportService.getReportById(1L)).thenReturn(report);

            mockMvc.perform(get("/api/analysis/overview/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Просмотр отчета о документе")
        void testShowOverviewByDocumentId() throws Exception {
            when(reportService.getReportByDocumentId(1L)).thenReturn(report);

            mockMvc.perform(get("/api/analysis/overview/1/document"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Просмотр всех отчетов")
        void testShowAllReports() throws Exception {
            when(reportService.getAllReports()).thenReturn(List.of(report));

            mockMvc.perform(get("/api/analysis/overview"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("Просмотр облака слов")
        void testShowWordCloud() throws Exception {
            when(reportService.getReportById(1L)).thenReturn(report);

            mockMvc.perform(get("/api/analysis/overview/1/cloud"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                    .andExpect(content().bytes(report.getWordCloud()));
        }

        @Test
        void testLaunchAnalysis() throws Exception {
            AnalysisRequest request = new AnalysisRequest(2L, "path/to/file.pdf");
            when(reportService.addReport(anyLong(), anyString())).thenReturn(report);

            mockMvc.perform(post("/api/analysis/launch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }
    }
}
