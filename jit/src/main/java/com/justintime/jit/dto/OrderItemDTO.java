package com.justintime.jit.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDTO {
    private String itemName;
    private BigDecimal totalPrice;
    private Boolean isCombo;
    private Integer quantity;
    private String orderItemStatus;
    private String orderNumber;
    private List<String> batchNumber;
    private String menuItemName;
}
