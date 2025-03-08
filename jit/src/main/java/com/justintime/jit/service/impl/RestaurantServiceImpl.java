package com.justintime.jit.service.impl;

import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RestaurantServiceImpl extends BaseServiceImpl<Restaurant,Long> implements RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    private static final int SUGGESTION_THRESHOLD = 3;

    @Override
    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }


    @Override
    public Restaurant updateRestaurant(Long id, Restaurant restaurant) {
        Restaurant existingRestaurant = getRestaurantById(id);

        existingRestaurant.setRestaurantName(restaurant.getRestaurantName());
        existingRestaurant.setContactNumber(restaurant.getContactNumber());
        existingRestaurant.setEmail(restaurant.getEmail());
        existingRestaurant.setAdmins(restaurant.getAdmins());
        existingRestaurant.setMenu(restaurant.getMenu());
        existingRestaurant.setOrders(restaurant.getOrders());
        existingRestaurant.setContactNumber(restaurant.getContactNumber());

        return restaurantRepository.save(existingRestaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {
        Restaurant existingRestaurant = getRestaurantById(id);
        restaurantRepository.delete(existingRestaurant);
    }
}