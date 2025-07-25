package com.justintime.jit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.justintime.jit.entity.Enums.OrderItemStatus;
import com.justintime.jit.service.OrderItemService;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDTO {
    @JsonProperty("itemName")
    private String orderItemName; // This is the name of the item in the order eg ("Vada#1, Vada#2")
    @JsonProperty("menuItemName")
    private String itemName; // This is the name of the item in the menu eg ("Vada")
    private String foodType;
    private BigDecimal price; // sum price of single menu item and single add-on
    private BigDecimal totalPrice; // price * quantity
    private Integer quantity;
    private OrderItemStatus orderItemStatus;
    private Set<AddOnDTO> addOns; // List of add-ons for the item
    private String customNotes;
    private String orderNumber;
    private Set<String> batchNumber;
}
