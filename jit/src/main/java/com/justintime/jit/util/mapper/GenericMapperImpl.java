package com.justintime.jit.util.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class GenericMapperImpl<T, D> implements GenericMapper<T, D>{

    private final ModelMapper modelMapper;

    public GenericMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public D toDTO(T entity, Class<D> dtoClass) {
        //populateIdSets(entity, dto);
        return modelMapper.map(entity, dtoClass);
    }

    @Override
    public T toEntity(D dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

//    private void populateIdSets(T entity, D dto) {
//        try {
//            Field[] entityFields = entity.getClass().getDeclaredFields();
//            Field[] dtoFields = dto.getClass().getDeclaredFields();
//
//            for (Field entityField : entityFields) {
//                if (Collection.class.isAssignableFrom(entityField.getType())) {
//                    entityField.setAccessible(true);
//                    Object fieldValue = entityField.get(entity);
//
//                    if (fieldValue instanceof Collection<?> entities && !entities.isEmpty()) {
//                        for (Field dtoField : dtoFields) {
//                            if (dtoField.getName().equals(entityField.getName() + "Ids") &&
//                                    Collection.class.isAssignableFrom(dtoField.getType())) {
//
//                                dtoField.setAccessible(true);
//
//                                // Determine the target collection type
//                                Class<?> collectionType = dtoField.getType();
//                                Collection<Long> ids;
//
//                                if (Set.class.isAssignableFrom(collectionType)) {
//                                    ids = entities.stream()
//                                            .map(this::getIdFromEntity)
//                                            .filter(Objects::nonNull)
//                                            .collect(Collectors.toCollection(HashSet::new));
//                                } else if (List.class.isAssignableFrom(collectionType)) {
//                                    ids = entities.stream()
//                                            .map(this::getIdFromEntity)
//                                            .filter(Objects::nonNull)
//                                            .collect(Collectors.toCollection(ArrayList::new));
//                                } else {
//                                    throw new IllegalArgumentException("Unsupported collection type: " + collectionType);
//                                }
//
//                                System.out.println("Populating " + dtoField.getName() + " with IDs: " + ids); // Debugging
//                                dtoField.set(dto, ids);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException("Error accessing fields during mapping", e);
//        }
//    }
//
//    private Long getIdFromEntity(Object entity) {
//        try {
//            Field idField = entity.getClass().getDeclaredField("id");
//            idField.setAccessible(true);
//            return (Long) idField.get(entity);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            return null; // Instead of throwing an error, return null
//        }
//    }
}
