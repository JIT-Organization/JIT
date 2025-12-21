package com.justintime.jit.repository;

import com.justintime.jit.entity.TimeInterval;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface TimeIntervalRepository extends BaseRepository<TimeInterval, Long> {
    Optional<TimeInterval> findByStartTimeAndEndTime(LocalTime startTime, LocalTime endTime);
}
