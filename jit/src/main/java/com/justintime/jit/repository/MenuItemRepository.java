package com.justintime.jit.repository;

import com.justintime.jit.entity.MenuItem;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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

    MenuItem findByRestaurantIdAndId(Long restaurantId, Long id);
    @Query(value = "SELECT mi.* FROM menu_item mi " +
            "JOIN restaurant r ON r.id = mi.restaurant_id " +
            "WHERE r.restaurant_code = :restaurantCode", nativeQuery = true)
    List<MenuItem> findByRestaurantCode(@Param("restaurantCode") String restaurantCode);

    @Query(value = "SELECT mi.* FROM menu_item mi " +
            "JOIN restaurant r ON r.id = mi.restaurant_id " +
            "WHERE r.restaurant_code = :restaurantCode and mi.menu_item_name = :menuItemName", nativeQuery = true)
    Optional<MenuItem> findByRestaurantCodeAndMenuItemName(String restaurantCode, String menuItemName);

    @Modifying
    @Query(value = "DELETE mi FROM menu_item mi " +
            "JOIN restaurant r ON mi.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND mi.menu_item_name = :menuItemName", nativeQuery = true)
    void deleteByRestaurantCodeAndMenuItemName(@Param("restaurantCode") String restaurantCode, @Param("menuItemName") String menuItemName);
}
