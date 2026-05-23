package sys.utils;

import jakarta.persistence.AttributeConverter;
import sys.model.AnalysisStatus;

public class AnalysisStatusConverter implements AttributeConverter<AnalysisStatus, Long> {
    @Override
    public Long convertToDatabaseColumn(AnalysisStatus attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case SUCCESS -> 1L;
            case FAILED -> 2L;
        };
    }

    @Override
    public AnalysisStatus convertToEntityAttribute(Long dbData) {
        if (dbData == null) return null;
        if (dbData == 1L) return AnalysisStatus.SUCCESS;
        if (dbData == 2L) return AnalysisStatus.FAILED;
        throw new IllegalArgumentException("Неизвестный статус ID: " + dbData);
    }
}
