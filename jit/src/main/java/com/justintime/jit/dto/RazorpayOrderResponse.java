package com.justintime.jit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RazorpayOrderResponse {

    private String id;
    private String entity;
    private int amount;
    private String currency;
    private String receipt;
    private String status;
}
