package com.justintime.jit.repository;

import com.justintime.jit.entity.Category;
import com.justintime.jit.entity.Cook;
import com.justintime.jit.entity.MenuItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CookRepository extends JpaRepository<Cook, Long> {
    Cook findByName(String name);

    Optional<Cook> findByRestaurantIdAndName(Long restaurantId, String name);
    Set<Cook> findByNameInAndRestaurantId(Set<String> cookNames, Long restaurantId);
    @Query(value = "SELECT mi.* FROM menu_item mi " +
           "JOIN menu_item_cook mic ON mi.id = mic.menu_item_id " +
           "WHERE mic.cook_id = :cookId", nativeQuery = true)
    Set<MenuItem> findMenuItemsByCookId(@Param("cookId") Long cookId);
}
