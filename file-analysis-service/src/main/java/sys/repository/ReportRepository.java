package sys.repository;

import java.util.Optional;
import sys.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByDocumentId(Long documentId);
}
