package sys.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import sys.utils.AnalysisStatusConverter;

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

    @Convert(converter = AnalysisStatusConverter.class)
    @Column(name = "status_id", nullable = false)
    private AnalysisStatus status;

    @Column(name = "error_message", nullable = true)
    private String errorMessage;

    @Lob
    @Column(name = "word_cloud")
    @JdbcTypeCode(SqlTypes.BINARY)
    private byte[] wordCloud;
}
