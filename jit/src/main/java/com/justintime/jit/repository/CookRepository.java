package com.justintime.jit.repository;

import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Cook;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface CookRepository extends BaseRepository<Cook,Long> {
    Cook findByName(String name);


    Set<Cook> findByNameInAndRestaurantId(Set<String> cookNames, Long restaurantId);
}
