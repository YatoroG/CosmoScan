package sys.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sys.model.Document;
import sys.model.requests.DocumentUpdateRequest;
import sys.service.DocumentService;
import sys.service.StorageService;

@Slf4j
@RestController
@RequestMapping("/api/storage/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final StorageService storageService;

    @GetMapping("/{id}")
    public Document getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id);
    }

    @GetMapping("/student/{id}")
    public List<Document> getDocumentsByStudentId(@PathVariable Long id) {
        return documentService.getDocumentsByStudentId(id);
    }

    @GetMapping("/{id}/path")
    public String getFilePathById(@PathVariable Long id) {
        return documentService.getFilePathById(id);
    }

    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        Document document = documentService.getDocumentById(id);
        Resource file = storageService.getDocument(document.getFilePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(file);
    }

    @PostMapping(path = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public Document addDocument(@RequestParam("file") MultipartFile file,
                                                @RequestParam String lastName,
                                                @RequestParam String firstName,
                                                @RequestParam(required = false) String patronymic,
                                                @RequestParam String groupName) {
        String path = storageService.saveDocument(file);
        return documentService.addDocument(path, file.getOriginalFilename(), lastName,
                firstName, patronymic, groupName);
    }

    @PutMapping("/{id}")
    public Document updateDocument(@PathVariable Long id,
                                                 @RequestBody DocumentUpdateRequest request) {
        return documentService.updateDocument(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
    }
}
