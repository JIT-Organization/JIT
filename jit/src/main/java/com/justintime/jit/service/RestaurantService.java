package com.justintime.jit.service;

import com.justintime.jit.dto.RestaurantDTO;
import com.justintime.jit.entity.Restaurant;

import java.util.List;

public interface RestaurantService {
    RestaurantDTO addRestaurant(RestaurantDTO restaurantDTO);
    List<RestaurantDTO> getAllRestaurants();
    RestaurantDTO getRestaurantById(Long id);
    RestaurantDTO updateRestaurant(Long id, RestaurantDTO restaurantDTO);
    void deleteRestaurant(Long id);

//    List<String> findSimilarNames(String name);
//
//    String suggestCorrectName(String name);
}

