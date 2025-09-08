package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
        private Long id;
        private String itemName;
        private Integer stock;
        private Integer quantity;
        private float expense;
        private String item;
        private String unit;
        private Long supplierId; // ID of the supplier (User entity)
    }

