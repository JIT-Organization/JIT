package com.justintime.jit.service.impl;

import com.justintime.jit.entity.CookLoadDelta;
import com.justintime.jit.repository.CookLoadDeltaRepository;
import com.justintime.jit.service.CookLoadTrackerService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CookLoadRecoveryService {
    private final CookLoadTrackerService tracker;
    private final CookLoadDeltaRepository repo;

    public CookLoadRecoveryService(CookLoadTrackerService tracker, CookLoadDeltaRepository repo) {
        this.tracker = tracker;
        this.repo = repo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void recover() {
        // 1. Load snapshot if exists
        // 2. Replay unprocessed deltas
        List<CookLoadDelta> deltas = repo.findByProcessedFalseOrderByCreatedAtAsc();
        for (CookLoadDelta d : deltas) {
            tracker.addLoad(d.getCookId(), d.getDeltaMinutes(), d.getOrderItemId());
            d.setProcessed(true);
        }
        repo.saveAll(deltas);
    }
}