package com.justintime.jit.util;


import com.justintime.jit.dto.RazorpayOrderRequest;
import com.justintime.jit.dto.RazorpayOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "razorpay-client", url = "${razorpay.url}")
public interface RazorpayClient {
    @PostMapping("/orders")
    RazorpayOrderResponse createOrder(@RequestBody RazorpayOrderRequest request,
                                      @RequestHeader("Authorization") String authHeader);
}
