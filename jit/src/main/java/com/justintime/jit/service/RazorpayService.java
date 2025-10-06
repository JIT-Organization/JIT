package com.justintime.jit.service;

import com.justintime.jit.dto.RazorpayOrderRequest;
import com.justintime.jit.dto.RazorpayOrderResponse;
import com.justintime.jit.dto.RazorpayPaymentResponse;
import com.justintime.jit.util.RazorpayClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Getter
@Setter
@Service
public class RazorpayService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    public RazorpayService(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    private String buildAuthHeader() {
        String auth = key + ":" + secret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        return "Basic " + encodedAuth;
    }

    public RazorpayOrderResponse createOrder(RazorpayOrderRequest request) {
        return razorpayClient.createOrder(request, buildAuthHeader());
    }

    public RazorpayPaymentResponse getPayment(String paymentId) {
        return razorpayClient.getPayment(paymentId, buildAuthHeader());
    }
}
