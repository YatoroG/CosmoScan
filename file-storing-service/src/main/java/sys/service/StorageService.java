package sys.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sys.utils.exception.EntityNotFoundException;
import sys.utils.exception.StorageException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {
    private final Path rootLocation = Paths.get("/app/uploads");

    public Resource getDocument(String path) {
        log.info("Запрос на скачивание документа по пути = {}", path);
        try {
            Path file = Paths.get(path);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new EntityNotFoundException("Документ не найден или недоступен для чтения");
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Не удалось скачать документ", e);
        }
    }

    public String saveDocument(MultipartFile file) {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetPath = rootLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath.toString();
        } catch (IOException e) {
            throw new StorageException("Не удалось сохранить документ", e);
        }
    }
}