package com.justintime.jit.controller;

import com.justintime.jit.entity.ShiftCapacity;
import com.justintime.jit.service.ShiftCapacityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shift-capacity")
public class ShiftCapacityController {

    @Autowired
    private ShiftCapacityService shiftCapacityService;

    @GetMapping
    public List<ShiftCapacity> getAllShiftCapacities() {
        return shiftCapacityService.getAllShiftCapacities();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<ShiftCapacity> getShiftCapacitiesByRestaurant(@PathVariable Long restaurantId) {
        return shiftCapacityService.getShiftCapacitiesByRestaurantId(restaurantId);
    }

    @PostMapping
    public ShiftCapacity addShiftCapacity(@RequestBody ShiftCapacity shiftCapacity) {
        return shiftCapacityService.addShiftCapacity(shiftCapacity);
    }

    @PutMapping("/{id}")
    public ShiftCapacity updateShiftCapacity(@PathVariable Long id, @RequestBody ShiftCapacity updatedShiftCapacity) {
        return shiftCapacityService.updateShiftCapacity(id, updatedShiftCapacity);
    }

    @DeleteMapping("/{id}")
    public void deleteShiftCapacity(@PathVariable Long id) {
        shiftCapacityService.deleteShiftCapacity(id);
    }
}
