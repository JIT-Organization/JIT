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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDTO>>> getItemsByRestaurantAndFoodType(
            @RequestParam(required = false) FoodType foodType) {
        if (foodType != null) {
            return success(itemService.getAllItemsForRestaurantAndFoodType(foodType));
        } else {
            return success(itemService.getAllItemsForRestaurant());
        }
    }

    @GetMapping("/getItemNames")
    public ResponseEntity<ApiResponse<List<String>>> getItemNamesByRestaurantAndFoodType(
            @RequestParam(required = false) FoodType foodType) {
        if (foodType != null) {
            return success(itemService.getAllItemNamesForRestaurantAndFoodType(foodType));
        } else {
            return success(itemService.getAllItemNamesForRestaurant());
        }
    }

    @GetMapping("/{itemName}")
    public ResponseEntity<ApiResponse<ItemDTO>> getItemByRestaurantAndName(
            @PathVariable String itemName,
            @RequestParam FoodType foodType) {
        return success(itemService.getItemByRestaurantAndNameAndFoodType(itemName, foodType));
    }
    @PostMapping("")
    public ResponseEntity<ApiResponse<ItemDTO>> createItem(
            @RequestParam FoodType foodType,
            @RequestBody ItemDTO itemDTO) {
        return success(itemService.createItem(foodType, itemDTO));
    }

    @PutMapping("/{itemName}")
    public ResponseEntity<ApiResponse<ItemDTO>> updateItem(
            @PathVariable String itemName,
            @RequestParam FoodType foodType,
            @RequestBody ItemDTO itemDTO) {
        return success(itemService.updateItem(itemName, foodType, itemDTO));
    }

    @PatchMapping("/{itemName}")
    public ResponseEntity<ApiResponse<ItemDTO>> patchItem(
            @PathVariable String itemName,
            @RequestParam FoodType foodType,
            @RequestBody PatchRequest<ItemDTO> payload) {
        return success(itemService.patchItem(itemName, foodType, payload.getDto(), payload.getPropertiesToBeUpdated()));
    }

    @DeleteMapping("/{itemName}")
    public void deleteItem(
            @PathVariable String itemName,
            @RequestParam FoodType foodType) {
        itemService.deleteItem(itemName, foodType);
    }



}
