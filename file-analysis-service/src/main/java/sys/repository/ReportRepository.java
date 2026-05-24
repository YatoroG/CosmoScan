package sys.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sys.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByDocumentId(Long documentId);
}
