package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.dto.RestaurantDTO;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jit-api/restaurants")
public class RestaurantController extends BaseController {

    @Autowired
    private RestaurantService restaurantService;

    // Add a new restaurant
    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant createdRestaurant = restaurantService.addRestaurant(restaurant);
        return ResponseEntity.ok(createdRestaurant);
    }




    // Get all restaurants
//    @GetMapping
//    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
//        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
//        return ResponseEntity.ok(restaurants);
//    }

    //Get a restaurant by ID
    @GetMapping
    public ResponseEntity<ApiResponse<RestaurantDTO>> getRestaurantByCode() {
        RestaurantDTO restaurant = restaurantService.getRestaurantDTOByRestaurantCode();
        return success(restaurant);
    }

    @GetMapping("/getUpiId")
    public ResponseEntity<ApiResponse<String>> getUpiIdByRestaurantCode() {
        String upiId = restaurantService.getUpiIdByRestaurantCode();
        return success(upiId);
    }

    // Update restaurant details
    @PutMapping
    public ResponseEntity<ApiResponse<RestaurantDTO>> updateRestaurant(
            @RequestBody Restaurant restaurant) {
        restaurantService.updateRestaurant(restaurant);
        return success(null);
    }

    @PatchMapping
    @PreAuthorize("hasPermission(null, 'MANAGE_SETTINGS')")
    public ResponseEntity<ApiResponse<RestaurantDTO>> patchUpdateRestaurant (@RequestBody PatchRequest<RestaurantDTO> payload) {
        restaurantService.patchUpdateRestaurant(payload.getDto(), payload.getPropertiesToBeUpdated());
        return success(null);
    }

    // Delete a restaurant
    @DeleteMapping
    public ResponseEntity<ApiResponse<RestaurantDTO>> deleteRestaurant() {
        restaurantService.deleteRestaurant();
        return success(null);
    }

//    @GetMapping("/search")
//    public ResponseEntity<?> searchRestaurants(@RequestParam String name) {
//        List<String> similarNames = restaurantService.findSimilarNames(name);
//
//        if (!similarNames.isEmpty()) {
//            return ResponseEntity.ok(similarNames);
//        }
//
//        String suggestion = restaurantService.suggestCorrectName(name);
//
//        if (suggestion != null) {
//            return ResponseEntity.badRequest().body("Did you mean: " + suggestion + "?");
//        }
//
//        return ResponseEntity.badRequest().body("No similar restaurants found.");
//    }
}
