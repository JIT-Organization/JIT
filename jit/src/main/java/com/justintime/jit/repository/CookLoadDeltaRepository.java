package com.justintime.jit.repository;

import com.justintime.jit.entity.CookLoadDelta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CookLoadDeltaRepository extends JpaRepository<CookLoadDelta, Long> {
    List<CookLoadDelta> findByProcessedFalseOrderByCreatedAtAsc();
}
