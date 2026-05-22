package sys.repository;

import java.util.List;
import sys.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findAllDocumentsByStudentId(Long studentId);
}
