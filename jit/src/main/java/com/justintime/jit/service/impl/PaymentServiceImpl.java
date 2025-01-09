package com.justintime.jit.service.impl;

import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.repository.PaymentRepo.PaymentRepository;
import com.justintime.jit.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;

    @Autowired
    public void paymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment updatePayment(Long id, Payment updatedPayment) {
        return paymentRepository.findById(id)
                .map(existingPayment -> {
                    existingPayment.setOrder(updatedPayment.getOrder());
                    existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
                    existingPayment.setAmount(updatedPayment.getAmount());
                    existingPayment.setCurrency(updatedPayment.getCurrency());
                    existingPayment.setPaymentStatus(updatedPayment.getPaymentStatus());
                    existingPayment.setUpdatedBy(updatedPayment.getUpdatedBy());
                    existingPayment.setUpdatedDttm(updatedPayment.getUpdatedDttm());
                    return paymentRepository.save(existingPayment);
                }).orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}

