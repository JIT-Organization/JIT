package com.justintime.jit.util;


import com.justintime.jit.dto.RazorpayOrderRequest;
import com.justintime.jit.dto.RazorpayOrderResponse;
import com.justintime.jit.dto.RazorpayPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "razorpay-client", url = "${razorpay.url}")
public interface RazorpayClient {

    @PostMapping("/orders")
    RazorpayOrderResponse createOrder(@RequestBody RazorpayOrderRequest request,
                                      @RequestHeader("Authorization") String authHeader);

    @GetMapping("/payments/{paymentId}")
    RazorpayPaymentResponse getPayment(
            @PathVariable("paymentId") String paymentId,
            @RequestHeader("Authorization") String authHeader
    );
}
