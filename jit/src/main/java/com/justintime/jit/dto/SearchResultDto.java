package com.justintime.jit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SearchResultDto {
    private String type; // "Restaurant" or "Food"
    private String name;
    private List<String> associatedNames; // e.g., foods in a restaurant or restaurants serving a food

    public List<String> getAssociatedNames() {
        return associatedNames != null ? new ArrayList<>(associatedNames) : null; // Defensive copy
    }

    public void setAssociatedNames(List<String> associatedNames) {
        this.associatedNames = associatedNames != null ? new ArrayList<>(associatedNames) : null; // Defensive copy
    }
}