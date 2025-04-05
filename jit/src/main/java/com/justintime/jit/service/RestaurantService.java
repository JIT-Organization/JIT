package com.justintime.jit.service;

import com.justintime.jit.dto.RestaurantDTO;
import com.justintime.jit.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    Restaurant addRestaurant(Restaurant restaurant);
    List<Restaurant> getAllRestaurants();
    Restaurant getRestaurantById(Long id);
    Restaurant updateRestaurant(Long id, Restaurant restaurant);
    void deleteRestaurant(Long id);

//    List<String> findSimilarNames(String name);
//
//    String suggestCorrectName(String name);
}

