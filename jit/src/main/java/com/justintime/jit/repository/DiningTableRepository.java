package com.justintime.jit.repository;

import com.justintime.jit.dto.DiningTableDTO;
import com.justintime.jit.entity.DiningTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiningTableRepository extends BaseRepository<DiningTable, Long> {
    List<DiningTableDTO> findByRestaurantId(Long restaurantId);
}
