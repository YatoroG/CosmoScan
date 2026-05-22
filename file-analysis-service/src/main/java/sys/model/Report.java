package sys.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reports")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_format", nullable = false)
    private String fileFormat;

    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    @Column(name = "error_message", nullable = true)
    private String errorMessage;

    @Lob
    @Column(name = "word_cloud", columnDefinition = "bytea")
    private byte[] wordCloud;
}
