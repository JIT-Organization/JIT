package com.justintime.jit.util.mapper;

public interface GenericMapper<E, D> {
    D toDTO(E entity, Class<D> dtoClass);
    E toEntity(D dto, Class<E> entityClass);
}
