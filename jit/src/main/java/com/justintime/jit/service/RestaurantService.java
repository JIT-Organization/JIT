package com.justintime.jit.service;

import com.justintime.jit.dto.RestaurantDTO;
import com.justintime.jit.entity.Restaurant;

import java.util.HashSet;
import java.util.List;

public interface RestaurantService {
    Restaurant addRestaurant(Restaurant restaurant);
    List<Restaurant> getAllRestaurants();
    Restaurant getRestaurantById(Long id);
    void updateRestaurant(Restaurant restaurant);
    void deleteRestaurant();
    Restaurant getRestaurantByRestaurantCode();
    RestaurantDTO getRestaurantDTOByRestaurantCode();
    String getUpiIdByRestaurantCode();
    void patchUpdateRestaurant(RestaurantDTO dto, HashSet<String> propertiesToBeUpdated);

//    List<String> findSimilarNames(String name);
//
//    String suggestCorrectName(String name);
}

