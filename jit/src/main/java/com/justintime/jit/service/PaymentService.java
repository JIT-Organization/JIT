package com.justintime.jit.service;

import com.justintime.jit.entity.PaymentEntities.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long id);
    Payment createPayment(Payment payment);
    Payment updatePayment(Long id, Payment updatedPayment);
    void deletePayment(Long id);
    List<Payment> getPaymentsByOrderId(Long orderId);
}
