package com.justintime.jit.repository;

import com.justintime.jit.entity.AddOn;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddOnRepository extends BaseRepository<AddOn, Long> {
    List<AddOn> findAllByRestaurant_RestaurantCode(String restaurantCode);
}
