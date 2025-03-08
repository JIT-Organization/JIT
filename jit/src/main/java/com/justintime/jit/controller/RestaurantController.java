package com.justintime.jit.controller;

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
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant) {
        Restaurant createdRestaurant = restaurantService.addRestaurant(restaurant);
        return ResponseEntity.ok(createdRestaurant);
    }

    // Get all restaurants
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    //Get a restaurant by ID
    @GetMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

    // Update restaurant details
    @PutMapping("/{restaurantId}")
    public ResponseEntity<Restaurant> updateRestaurant(
            @PathVariable Long restaurantId,
            @RequestBody Restaurant restaurant) {
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurantId, restaurant);
        return ResponseEntity.ok(updatedRestaurant);
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
