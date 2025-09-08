package com.justintime.jit.service;

import com.justintime.jit.util.RazorpayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import  com.justintime.jit.dto.RazorpayOrderRequest;
import com.justintime.jit.dto.RazorpayOrderResponse;

import java.util.Base64;

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

    public RazorpayOrderResponse createOrder(RazorpayOrderRequest request) {
        String auth = key + ":" + secret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;

        return razorpayClient.createOrder(request, authHeader);
    }
}
