package com.justintime.jit.controller;

import com.justintime.jit.dto.DiningTableDTO;
import com.justintime.jit.entity.DiningTable;
import com.justintime.jit.service.DiningTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/tables")
public class DiningTableController {
    @Autowired
    private DiningTableService diningTableService;

    @GetMapping("/{restaurantId}")
    public List<DiningTableDTO> getTablesByRestaurant(@PathVariable Long restaurantId){
        return diningTableService.getDiningTablesByRestaurantId(restaurantId);
    }
}
