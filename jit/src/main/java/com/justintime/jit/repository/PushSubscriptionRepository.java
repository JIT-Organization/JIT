package com.justintime.jit.repository;

import com.justintime.jit.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    @Query("SELECT ps FROM PushSubscription ps WHERE ps.user.id = :userId")
    List<PushSubscription> findByUserId(@Param("userId") Long userId);
}
