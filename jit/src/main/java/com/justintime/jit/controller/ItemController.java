package com.justintime.jit.controller;

import com.justintime.jit.dto.ItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/items")
public class ItemController extends BaseController{

    @Autowired
    private ItemService itemService;

    @GetMapping("/{restaurantCode}")
    public List<ItemDTO> getItemsByRestaurantAndFoodType(
            @PathVariable String restaurantCode,
            @RequestParam(required = false) FoodType foodType) {
        if (foodType != null) {
            return itemService.getAllItemsForRestaurantAndFoodType(restaurantCode, foodType);
        } else {
            return itemService.getAllItemsForRestaurant(restaurantCode);
        }
    }

    @GetMapping("/{restaurantCode}/{itemName}")
    public ItemDTO getItemByRestaurantAndName(
            @PathVariable String restaurantCode,
            @PathVariable String itemName,
            @RequestParam FoodType foodType) {
        return itemService.getItemByRestaurantAndNameAndFoodType(restaurantCode, itemName, foodType);
    }
    @PostMapping("/{restaurantCode}")
    public ItemDTO createItem(
            @PathVariable String restaurantCode,
            @RequestBody ItemDTO itemDTO) {
        return itemService.createItem(restaurantCode, itemDTO);
    }

    @PutMapping("/{restaurantCode}/{itemName}")
    public ItemDTO updateItem(
            @PathVariable String restaurantCode,
            @RequestBody ItemDTO itemDTO) {
        return itemService.updateItem(restaurantCode, itemDTO);
    }

    @PatchMapping("/{restaurantCode}/{itemName}")
    public ItemDTO patchItem(
            @PathVariable String restaurantCode,
            @RequestBody PatchRequest<ItemDTO> payload) {
        return itemService.patchItem(restaurantCode, payload.getDto(), payload.getPropertiesToBeUpdated());
    }

    @DeleteMapping("/{restaurantCode}/{itemName}")
    public void deleteItem(
            @PathVariable String restaurantCode,
            @PathVariable String itemName,
            @RequestParam FoodType foodType) {
        itemService.deleteItem(restaurantCode, itemName, foodType);
    }



}
