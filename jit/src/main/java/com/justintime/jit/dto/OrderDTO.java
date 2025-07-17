package com.justintime.jit.dto;

import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.Enums.OrderType;
import com.justintime.jit.entity.Enums.PaymentStatus;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private String orderedBy;
    private String mobileNumber;
    private OrderType orderType;
    private String reservationNumber;
    private List<String> diningTables;
    private BigDecimal amount;
    private OrderStatus status;
    private LocalDateTime serveTime;
    private String notes;
    private List<String> paymentNumber;
    private PaymentStatus paymentStatus;
    private List<OrderItemDTO> orderItems;
}
