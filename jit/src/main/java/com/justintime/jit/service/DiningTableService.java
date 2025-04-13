package com.justintime.jit.service;

import com.justintime.jit.dto.DiningTableDTO;

import java.util.HashSet;
import java.util.List;

public interface DiningTableService{
    List<DiningTableDTO> getDiningTablesByRestaurantCode(String restaurantCode);
    DiningTableDTO patchUpdateTablesByRestaurantCode(String restaurantCode, DiningTableDTO dto, HashSet<String> propertiesToBeUpdated);

    DiningTableDTO createTable(String restaurantCode, DiningTableDTO dto);

    void deleteTable(String restaurantCode, String tableNumber);
}
