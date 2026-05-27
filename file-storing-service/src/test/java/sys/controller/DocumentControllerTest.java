package sys.controller;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import sys.model.Document;
import sys.model.requests.DocumentUpdateRequest;
import sys.service.DocumentService;
import sys.service.StorageService;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
@DisplayName("Мок-тесты контроллера документов")
class DocumentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private StorageService storageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Получение документа по ID")
        void testGetDocumentById() throws Exception {
            Document document = Document.builder().id(1L).fileName("test.txt").build();
            when(documentService.getDocumentById(1L)).thenReturn(document);

            mockMvc.perform(get("/api/storage/documents/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fileName").value("test.txt"));
        }

        @Test
        @DisplayName("Скачивание документа")
        void testDownloadDocument() throws Exception {
            Document document = Document.builder().id(1L).fileName("test.txt").filePath("/path/test.txt").build();
            Resource resource = new ByteArrayResource("content".getBytes());

            when(documentService.getDocumentById(1L)).thenReturn(document);
            when(storageService.getDocument(document.getFilePath())).thenReturn(resource);

            mockMvc.perform(get("/api/storage/documents/1/download"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\""))
                    .andExpect(content().string("content"));
        }

        @Test
        @DisplayName("Загрузка документа")
        void testAddDocument() throws Exception {
            MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                    MediaType.TEXT_PLAIN_VALUE, "текст".getBytes());

            Document doc = Document.builder().id(1L).fileName("test.txt").build();

            when(storageService.saveDocument(any())).thenReturn("/saved/path/test.txt");
            when(documentService.addDocument(anyString(), anyString(), anyString(), anyString(), any(), anyString()))
                    .thenReturn(doc);

            mockMvc.perform(multipart("/api/storage/documents/upload")
                            .file(file)
                            .param("lastName", "Иванов")
                            .param("firstName", "Иван")
                            .param("patronymic", "Иванович")
                            .param("groupName", "ГР-26"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        @DisplayName("Редактирование документа")
        void tetsUpdateDocument() throws Exception {
            DocumentUpdateRequest request = new DocumentUpdateRequest(
                    "/path/test.txt", "test.txt", 1L, LocalDateTime.now());

            Document updatedDoc = Document.builder()
                    .id(1L)
                    .filePath("/path/updated_name.txt")
                    .fileName("updated_name.txt")
                    .build();

            when(documentService.updateDocument(eq(1L), any(DocumentUpdateRequest.class)))
                    .thenReturn(updatedDoc);

            mockMvc.perform(put("/api/storage/documents/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.filePath").value("/path/updated_name.txt"))
                    .andExpect(jsonPath("$.fileName").value("updated_name.txt"));
        }


        @Test
        @DisplayName("Удаление документа")
        void testDeleteDocument() throws Exception {
            mockMvc.perform(delete("/api/storage/documents/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Просмотр всех документов")
        void testGetAllDocuments() throws Exception {
            when(documentService.getAllDocuments()).thenReturn(List.of(new Document()));

            mockMvc.perform(get("/api/storage/documents"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }
}
