package com.justintime.jit.dto;


import lombok.Data;

@Data
public class RazorpayPaymentResponse {
    private String id;
    private String entity;
    private Integer amount;
    private String currency;
    private String status;
    private String order_id;
    private Boolean captured;
    private String method;
    private Integer amount_refunded;
    private String description;
    private String email;
    private String contact;
    private Long created_at;
}
