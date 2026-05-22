package sys.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sys.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllDocumentsByStudentId(Long studentId);
}
