package com.justintime.jit.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PatchMenuItemRequest {
    private MenuItemDTO menuItemDTO;
    private List<String> propertiesToBeUpdated;
}
