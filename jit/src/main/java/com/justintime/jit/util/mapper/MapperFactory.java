package com.justintime.jit.util.mapper;

import org.modelmapper.ModelMapper;

public class MapperFactory {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.getConfiguration().setSkipNullEnabled(false);
    }

    public static <T, D> GenericMapper<T, D> getMapper(Class<T> source, Class<D> target) {
        return new GenericMapper<>(modelMapper, source, target);
    }
}
