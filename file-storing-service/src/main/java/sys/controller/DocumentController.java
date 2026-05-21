package sys.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import sys.model.Document;
import sys.model.requests.DocumentUpdateRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sys.service.DocumentService;
import sys.service.StorageService;

@RestController
@RequestMapping("/api/storage/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final StorageService storageService;

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
        Document document = documentService.getDocumentById(id)
                .orElseThrow(() -> new RuntimeException("Документ не найден"));
        Resource file = storageService.getDocument(document.getFilePath());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(file);
    }

    @PostMapping(path = "/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Document> addDocument(@RequestParam("file") MultipartFile file,
                                                @RequestParam String lastName,
                                                @RequestParam String firstName,
                                                @RequestParam(required = false) String patronymic,
                                                @RequestParam String groupName) {
        String path = storageService.saveDocument(file);
        Document newDocument = documentService.addDocument(path, file.getOriginalFilename(), lastName,
                firstName, patronymic, groupName);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDocument);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id,
                                                 @RequestBody DocumentUpdateRequest request) {
        Document updatedDocument = documentService.updateDocument(id, request);
        return ResponseEntity.ok(updatedDocument);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
