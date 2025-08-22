package com.justintime.jit.repository;

import com.justintime.jit.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // Find a restaurant by name
//    Restaurant findByName(String name);

    // Get all restaurants
    List<Restaurant> findAll();

    List<Restaurant> findByRestaurantNameContaining(String restaurantName);

    Optional<Restaurant> findByRestaurantCode(String restaurantCode);

    Optional<Set<Restaurant>> findByRestaurantCodeIn(Set<String> restaurantCodes);
}
