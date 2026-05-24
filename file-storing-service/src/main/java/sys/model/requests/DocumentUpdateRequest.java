package sys.model.requests;

import java.time.LocalDateTime;

public record DocumentUpdateRequest(String filePath, String fileName,
                                    Long studentId, LocalDateTime uploadDate) {
}
