package sys.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import sys.utils.exception.EntityNotFoundException;


import static org.junit.jupiter.api.Assertions.*;

class StorageServiceTest {
    private StorageService storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageService = new StorageService();
        ReflectionTestUtils.setField(storageService, "rootLocation", tempDir);
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("Сохранение файла")
        void testSaveDocument() throws IOException {
            MultipartFile multipartFile = new MockMultipartFile("file", "test.pdf",
                    "application/pdf", "text".getBytes());

            String savedPathString = storageService.saveDocument(multipartFile);

            assertNotNull(savedPathString);
            Path savedPath = Path.of(savedPathString);
            assertTrue(Files.exists(savedPath));
            assertTrue(savedPathString.contains("test.pdf"));
            assertEquals("text", Files.readString(savedPath));
        }

        @Test
        @DisplayName("Скачивание файла")
        void testGetDocument() throws IOException {
            Path file = Files.createFile(tempDir.resolve("test.txt"));
            Files.writeString(file, "text");

            Resource resource = storageService.getDocument(file.toString());

            assertNotNull(resource);
            assertTrue(resource.exists());
            assertTrue(resource.isReadable());
        }
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("Получение файла")
        void testGetDocument() {
            String nonExistentPath = tempDir.resolve("non_existing.txt").toString();
            assertThrows(EntityNotFoundException.class, () -> storageService.getDocument(nonExistentPath));
        }
    }
}
