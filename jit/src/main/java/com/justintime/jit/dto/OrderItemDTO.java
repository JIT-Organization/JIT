package com.justintime.jit.dto;

import lombok.*;

import java.math.BigDecimal;

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
}
