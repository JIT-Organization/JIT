package com.justintime.jit.controller;

import com.justintime.jit.dto.RestaurantDTO;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // Add a new restaurant
    @PostMapping
    public ResponseEntity<RestaurantDTO> addRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO createdRestaurant = restaurantService.addRestaurant(restaurantDTO);
        return ResponseEntity.ok(createdRestaurant);
    }

    // Get all restaurants
    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

     //Get a restaurant by ID
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long restaurantId) {
        RestaurantDTO restaurantDTO = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurantDTO);
    }

    // Update restaurant details
    @PutMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO updatedRestaurantDTO = restaurantService.updateRestaurant(restaurantId, restaurantDTO);
        return ResponseEntity.ok(updatedRestaurantDTO);
    }

    // Delete a restaurant
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable Long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok("Restaurant deleted successfully.");
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
