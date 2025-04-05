package com.justintime.jit.repository;

import com.justintime.jit.entity.MenuItem;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface MenuItemRepository extends BaseRepository<MenuItem, Long> {
    List<MenuItem> findByRestaurantId(Long restaurantId);
    List<MenuItem> findByMenuItemNameContaining(String restaurantName);

    @Query(value = "SELECT mi.* FROM menu_item mi " +
            "JOIN category_menu_item cmi ON mi.id = cmi.menu_item_id " +
            "WHERE mi.menu_item_name IN (:formattedMenuItemNames) " +
            "AND mi.restaurant_id = :restaurantId",
            nativeQuery = true)
    Set<MenuItem> findByMenuItemNamesAndRestaurantId(@Param("formattedMenuItemNames") Set<String> formattedMenuItemNames,
                                                   @Param("restaurantId") Long restaurantId);


}
