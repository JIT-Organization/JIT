package com.justintime.jit.repository;

import com.justintime.jit.entity.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends BaseRepository<Restaurant, Long> {

    // Find a restaurant by name
//    Restaurant findByName(String name);

    // Get all restaurants
    List<Restaurant> findAll();

    List<Restaurant> findByRestaurantNameContaining(String restaurantName);

    Restaurant findByRestaurantCode(String restaurantCode);
}
