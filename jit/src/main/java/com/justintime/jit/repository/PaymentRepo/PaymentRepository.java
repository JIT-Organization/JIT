package com.justintime.jit.repository.PaymentRepo;

import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends BaseRepository<Payment ,Long> {
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId ORDER BY p.updatedDttm")
    List<Payment> findByOrderId(@Param("orderId") Long orderId);
}
