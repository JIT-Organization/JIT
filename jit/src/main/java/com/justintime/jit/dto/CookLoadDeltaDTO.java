package com.justintime.jit.dto;

import lombok.Builder;

import java.sql.Timestamp;
import java.time.Instant;

@Builder
public record CookLoadDeltaDTO(Long cookId, int deltaMinutes, Long orderItemId, Instant createdAt, boolean processed) {
    public CookLoadDeltaDTO(Long cookId, int deltaMinutes, Long orderItemId, Timestamp from) {
        this(cookId, deltaMinutes, orderItemId, Instant.now(), false);
    }
}
