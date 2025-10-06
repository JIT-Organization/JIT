package com.justintime.jit.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RazorpayOrderRequest {
    private int amount;
    private String currency;
    private String receipt;
}
