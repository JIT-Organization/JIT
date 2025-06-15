package com.justintime.jit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
public class PatchRequest<T> {
    private T dto;
    private HashSet<String> propertiesToBeUpdated;
}
