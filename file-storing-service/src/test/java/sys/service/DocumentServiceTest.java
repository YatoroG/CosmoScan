package sys.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import sys.model.Document;
import sys.model.Student;
import sys.model.requests.DocumentUpdateRequest;
import sys.repository.DocumentRepository;
import sys.repository.StudentRepository;
import sys.utils.exception.EntityNotFoundException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Мок-тесты сервиса документов")
class DocumentServiceTest {
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private StudentRepository studentRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient analysisRestClient;

    @InjectMocks
    private DocumentService documentService;

    private Student mockStudent;
    private Document mockDocument;

    @BeforeEach
    void setUp() {
        mockStudent = Student.builder().id(1L).lastName("Иванов").firstName("Иван").build();
        mockDocument = Document.builder().id(10L).filePath("/path/file.txt").fileName("file.txt").student(mockStudent).build();
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Добавление документа")
        void testAddDocument() {
            when(studentRepository.findByLastNameAndFirstNameAndPatronymicAndGroupName(any(), any(), any(), any()))
                    .thenReturn(Optional.of(mockStudent));
            when(documentRepository.save(any(Document.class))).thenReturn(mockDocument);

            Document result = documentService.addDocument("/path/file.txt",
                    "file.txt", "Иванов", "Иван",
                    "Иванович", "ГР-26");

            assertNotNull(result);
            assertEquals(10L, result.getId());
        }

        @Test
        @DisplayName("Редактирование документа")
        void testUpdateDocument() {
            DocumentUpdateRequest request = new DocumentUpdateRequest("/new/path", "new.txt", 1L, LocalDateTime.now());

            when(documentRepository.findById(10L)).thenReturn(Optional.of(mockDocument));
            when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
            when(documentRepository.save(any(Document.class))).thenAnswer(i -> i.getArgument(0));

            Document updated = documentService.updateDocument(10L, request);

            assertEquals("/new/path", updated.getFilePath());
            assertEquals("new.txt", updated.getFileName());
        }

        @Test
        @DisplayName("Получение пути к файлу по id")
        void testGetFilePathById() {
            when(documentRepository.findById(10L)).thenReturn(Optional.of(mockDocument));
            String path = documentService.getFilePathById(10L);
            assertEquals("/path/file.txt", path);
        }

        @Test
        @DisplayName("Удаление документа")
        void testDeleteDocument() {
            when(documentRepository.existsById(10L)).thenReturn(true);
            assertDoesNotThrow(() -> documentService.deleteDocument(10L));
            verify(documentRepository, times(1)).deleteById(10L);
        }
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("Поиск по несуществующему id")
        void testGetDocumentById() {
            when(documentRepository.findById(999L)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> documentService.getDocumentById(999L));
        }

        @Test
        @DisplayName("Получение пути для несуществующего документа")
        void testGetFilePathById() {
            when(documentRepository.findById(999L)).thenReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class, () -> documentService.getFilePathById(999L));
        }

        @Test
        @DisplayName("Удаление несуществующего документа")
        void testDeleteDocument() {
            when(documentRepository.existsById(999L)).thenReturn(false);
            assertThrows(EntityNotFoundException.class, () -> documentService.deleteDocument(999L));
        }
    }
}
