package com.justintime.jit.util.mapper;

import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Converter(autoApply = false)
public class AddOnOptionsConverter extends JsonAttributeConverter<List<AddOnOption>> {
    public AddOnOptionsConverter() {
        super(new TypeReference<>() {});
    }
}
