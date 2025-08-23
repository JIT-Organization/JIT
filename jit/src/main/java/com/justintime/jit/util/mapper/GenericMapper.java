package com.justintime.jit.util.mapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.modelmapper.ModelMapper;

public class GenericMapper<T, D> {
    private final ModelMapper modelMapper;
    private final Class<T> entityClass;
    private final Class<D> dtoClass;

    /**
     * @param modelMapper shared, preconfigured instance. Do not mutate after passing.
     */
    @SuppressFBWarnings(value = "EI2", justification = "ModelMapper is shared as a singleton and is not modified externally")
    public GenericMapper(ModelMapper modelMapper, Class<T> entityClass, Class<D> dtoClass) {
        this.modelMapper = modelMapper;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public GenericMapper<T, D> setSkipNullEnabled(boolean enabled) {
        modelMapper.getConfiguration().setSkipNullEnabled(enabled);
        return this;
    }

    public D toDto(T entity) {
        return modelMapper.map(entity, dtoClass);
    }

    public T toEntity(D dto) {
        return modelMapper.map(dto, entityClass);
    }
}
