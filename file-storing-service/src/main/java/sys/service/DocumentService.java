package sys.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import sys.model.Document;
import sys.model.Student;
import sys.model.requests.AnalysisLaunchRequest;
import sys.model.requests.DocumentUpdateRequest;
import sys.repository.DocumentRepository;
import sys.repository.StudentRepository;
import sys.utils.exception.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final StudentRepository studentRepository;
    private final RestClient analysisRestClient;

    @Transactional(readOnly = true)
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Документ с id = " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<Document> getDocumentsByStudentId(Long studentId) {
        return documentRepository.findAllDocumentsByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public String getFilePathById(Long id) {
        return documentRepository.findById(id)
                .map(Document::getFilePath)
                .orElseThrow(() -> new EntityNotFoundException("Документ с id = " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document addDocument(String filePath, String fileName, String lastName,
                                String firstName, String patronymic, String groupName) {
        Student student = studentRepository.findByLastNameAndFirstNameAndPatronymicAndGroupName(
                        lastName, firstName, patronymic, groupName)
                .orElseGet(() -> {Student newStudent = Student.builder()
                        .lastName(lastName).firstName(firstName).patronymic(patronymic)
                        .groupName(groupName).build();
                    return studentRepository.save(newStudent);}
                );

        Document document = Document.builder()
                .filePath(filePath).fileName(fileName).student(student)
                .uploadDate(LocalDateTime.now()).build();
        Document savedDocument = documentRepository.save(document);

        try {
            log.info("Запрос на анализ документа c id = {}", savedDocument.getId());
            analysisRestClient.post()
                    .uri("/api/analysis/launch")
                    .body(new AnalysisLaunchRequest(savedDocument.getId(), savedDocument.getFilePath()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("file-analysis-service недоступен, ошибка: {}", e.getMessage());
        }

        return savedDocument;
    }

    public Document updateDocument(Long id, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Документ с id = " + id + " не найден"));
        if (request.filePath() != null)  {
            document.setFilePath(request.filePath());
        }
        if (request.fileName() != null)  {
            document.setFileName(request.fileName());
        }
        if (request.studentId() != null)  {
            Optional<Student> student = studentRepository.findById(request.studentId());
            student.ifPresent(document::setStudent);
        }
        if (request.uploadDate() != null)  {
            document.setUploadDate(request.uploadDate());
        }
        return documentRepository.save(document);
    }

    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new EntityNotFoundException("Документ с id = " + id + " не найден");
        }
        documentRepository.deleteById(id);
    }
}
