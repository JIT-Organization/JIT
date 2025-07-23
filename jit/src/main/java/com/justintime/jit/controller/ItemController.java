package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.ItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/items")
public class ItemController extends BaseController{

    @Autowired
    private ItemService itemService;

    @GetMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<List<ItemDTO>>> getItemsByRestaurantAndFoodType(
            @PathVariable String restaurantCode,
            @RequestParam(required = false) FoodType foodType) {
        if (foodType != null) {
            return success(itemService.getAllItemsForRestaurantAndFoodType(restaurantCode, foodType));
        } else {
            return success(itemService.getAllItemsForRestaurant(restaurantCode));
        }
    }

    @GetMapping("/{restaurantCode}/{itemName}")
    public ResponseEntity<ApiResponse<ItemDTO>> getItemByRestaurantAndName(
            @PathVariable String restaurantCode,
            @PathVariable String itemName,
            @RequestParam FoodType foodType) {
        return success(itemService.getItemByRestaurantAndNameAndFoodType(restaurantCode, itemName, foodType));
    }
    @PostMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<ItemDTO>> createItem(
            @PathVariable String restaurantCode,
            @RequestParam FoodType foodType,
            @RequestBody ItemDTO itemDTO) {
        return success(itemService.createItem(restaurantCode, foodType, itemDTO));
    }

    @PutMapping("/{restaurantCode}/{itemName}")
    public ResponseEntity<ApiResponse<ItemDTO>> updateItem(
            @PathVariable String restaurantCode,
            @RequestParam FoodType foodType,
            @RequestBody ItemDTO itemDTO) {
        return success(itemService.updateItem(restaurantCode, foodType, itemDTO));
    }

    @PatchMapping("/{restaurantCode}/{itemName}")
    public ResponseEntity<ApiResponse<ItemDTO>> patchItem(
            @PathVariable String restaurantCode,
            @RequestParam FoodType foodType,
            @RequestBody PatchRequest<ItemDTO> payload) {
        return success(itemService.patchItem(restaurantCode, foodType, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{restaurantCode}/{itemName}")
    public void deleteItem(
            @PathVariable String restaurantCode,
            @PathVariable String itemName,
            @RequestParam FoodType foodType) {
        itemService.deleteItem(restaurantCode, itemName, foodType);
    }



}
