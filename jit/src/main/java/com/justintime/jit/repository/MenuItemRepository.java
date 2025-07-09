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

    MenuItem findByRestaurantIdAndMenuItemName(Long restaurantCode, String menuItemName);
    @Query(value = "SELECT mi.* FROM menu_item mi " +
            "JOIN restaurant r ON r.id = mi.restaurant_id " +
            "WHERE r.restaurant_code = :restaurantCode", nativeQuery = true)
    List<MenuItem> findByRestaurantCode(@Param("restaurantCode") String restaurantCode);

    @Query(value = "SELECT mi.* FROM menu_item mi " +
            "JOIN restaurant r ON r.id = mi.restaurant_id " +
            "WHERE r.restaurant_code = :restaurantCode and mi.menu_item_name = :menuItemName", nativeQuery = true)
    MenuItem findByRestaurantCodeAndMenuItemName(String restaurantCode, String menuItemName);

    //@Modifying
    void deleteByRestaurantIdAndMenuItemName(@Param("restaurantId") Long restaurantId, @Param("menuItemName") String menuItemName);

    @Query(value = "SELECT mi.* FROM menu_item mi " +
            "JOIN restaurant r ON r.id = mi.restaurant_id " +
            "WHERE mi.menu_item_name IN (:menuItemNames) " +
            "AND mi.restaurant_code = :restaurantCode", nativeQuery = true)
    Set<MenuItem> findByMenuItemNamesAndRestaurantCode(@Param("menuItemNames") Set<String> menuItemNames, @Param("restaurantCode") String restaurantCode);

    @Query(value = "SELECT mi.* FROM menu_item mi " +
            "JOIN restaurant r ON r.id = mi.restaurant_id " +
            "WHERE mi.menu_item_name = :menuItemName " +
            "AND mi.restaurant_code = :restaurantCode", nativeQuery = true)
    MenuItem findMenuItemByMenuItemNamesAndRestaurantCode(@Param("menuItemName") String menuItemName, @Param("restaurantCode") String restaurantCode);
}
