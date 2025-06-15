package com.justintime.jit.repository;

import com.justintime.jit.entity.ShiftCapacity;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShiftCapacityRepository extends BaseRepository<ShiftCapacity, Long> {
    List<ShiftCapacity> findByRestaurantId(Long restaurantId);
}

