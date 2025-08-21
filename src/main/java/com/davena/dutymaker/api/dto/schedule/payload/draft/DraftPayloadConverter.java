package com.davena.dutymaker.api.dto.schedule.payload.draft;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DraftPayloadConverter implements AttributeConverter<DraftPayload, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(DraftPayload attribute) {
        if (attribute == null) return null;
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("DraftPayload -> JSON 변환 실패", e);
        }
    }

    @Override
    public DraftPayload convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return objectMapper.readValue(dbData, DraftPayload.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON -> DraftPayload 변환 실패", e);
        }
    }
}