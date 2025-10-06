package com.justintime.jit.service.impl;

import com.justintime.jit.dto.RazorpayOrderRequest;
import com.justintime.jit.dto.RazorpayOrderResponse;
import com.justintime.jit.dto.RazorpayPaymentResponse;
import com.justintime.jit.entity.Enums.PaymentStatus;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.repository.PaymentRepo.PaymentRepository;
import com.justintime.jit.service.PaymentService;
import com.justintime.jit.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.justintime.jit.util.ShaHashUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentRepository paymentRepository;
    private RazorpayService razorpayService;

    @Autowired
    private ShaHashUtil shaHashUtil;

    @Autowired
    public void paymentService(PaymentRepository paymentRepository, RazorpayService razorpayService) {
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Payment createPayment(Payment payment) {

        if (payment.getRazorpayOrderId() == null || payment.getRazorpayOrderId().isEmpty()) {

            RazorpayOrderRequest request = new RazorpayOrderRequest(
                    payment.getAmount().multiply(BigDecimal.valueOf(100)).intValueExact(),
                    payment.getCurrency(),
                    "receipt_" + payment.getOrder().getId()
            );

            RazorpayOrderResponse response = razorpayService.createOrder(request);
            payment.setRazorpayOrderId(response.getId());
        }

        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        return paymentRepository.save(payment);
    }

    public Payment verifyAndUpdatePayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) {
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        String payload = razorpayOrderId + "|" + razorpayPaymentId;
        String expectedSignature = shaHashUtil.hmacSHA256(payload, razorpayService.getSecret());

        if (!expectedSignature.equals(razorpaySignature)) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment signature verification failed");
        }

        RazorpayPaymentResponse paymentResponse = razorpayService.getPayment(razorpayPaymentId);
        if ("captured".equalsIgnoreCase(paymentResponse.getStatus())) {
            payment.setPaymentStatus(PaymentStatus.PAID);
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
        }
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
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

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

}

