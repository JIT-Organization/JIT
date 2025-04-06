package com.justintime.jit.service;

import com.justintime.jit.dto.DiningTableDTO;

import java.util.List;

public interface DiningTableService{
    List<DiningTableDTO> getDiningTablesByRestaurantId(Long restaurantId);
}
