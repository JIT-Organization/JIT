package com.justintime.jit.dto;

import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.Enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private String userName;
    private String reservationNumber;
    private List<String> diningTables;
    private OrderStatus status;
    private LocalDateTime serveTime;
    private String notes;
    private List<String> paymentNumber;
    private PaymentStatus paymentStatus;
}
