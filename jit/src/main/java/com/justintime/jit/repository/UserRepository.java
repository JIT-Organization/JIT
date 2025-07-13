package com.justintime.jit.repository;

import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.justintime.jit.entity.MenuItem;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    // Find all users by role
    List<User> findByRole(Role role);

    // Find a user by email
    User findByEmail(String email);

    List<User> findByUserName(String userName);

    // Do not delete
    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurant r ON ur.restaurant_id = r.id " +
            "WHERE r.id = :restaurantId AND u.role = :role AND u.user_name = :userName", nativeQuery = true)
    User findByRestaurantIdAndRoleAndUserName(
            @Param("restaurantId") Long restaurantId,
            @Param("role") Role role,
            @Param("userName") String userName);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurant r ON ur.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND u.role = :role AND u.user_name = :userName",
            nativeQuery = true)
    User findByRestaurantCodeAndRoleAndUserName(
            @Param("restaurantCode") String restaurantCode,
            @Param("role") Role role,
            @Param("userName") String userName);

    @Query(value = "SELECT r.id FROM restaurant r " +
            "JOIN user_restaurant ur ON r.id = ur.restaurant_id " +
            "JOIN users u ON ur.user_id = u.id " +
            "WHERE u.email = :email", nativeQuery = true)
    List<Long> findRestaurantIdsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN u.restaurants r " +
            "WHERE r.id = :restaurantId AND u.role = :role AND u.userName IN :cookNames")
    Set<User> findCooksByRestaurantIdAndRoleAndUserNames(@Param("restaurantId") Long restaurantId,
                                                          @Param("role") Role role,
                                                          @Param("cookNames") Set<String> cookNames);
//    DO NOT DELETE
//    @Query("SELECT u FROM User u " +
//            "JOIN u.restaurants r " +
//            "WHERE r.restaurantCode = :restaurantCode " +
//            "AND u.role = :role " +
//            "AND u.userName IN :cookNames")
//    Set<User> findCooksByRestaurantCodeAndRoleAndUserNames(
//            @Param("restaurantCode") String restaurantCode,
//            @Param("role") Role role,
//            @Param("cookNames") Set<String> cookNames);

    @Query(value = "SELECT r.restaurant_code FROM restaurant r " +
            "JOIN user_restaurant ur ON r.id = ur.restaurant_id " +
            "JOIN users u ON ur.user_id = u.id " +
            "WHERE u.email = :email", nativeQuery = true)
    List<String> findRestaurantCodesByEmail(@Param("email") String email);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurant r ON ur.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode", nativeQuery = true)
    List<User> findAllByRestaurantCode(@Param("restaurantCode") String restaurantCode);

    @Query(value = "SELECT mi.* FROM menu_item mi " +
           "JOIN menu_item_cook mic ON mi.id = mic.menu_item_id " +
           "WHERE mic.cook_id = :cookId", nativeQuery = true)
    Set<MenuItem> findMenuItemsByCookId(@Param("cookId") Long cookId);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "WHERE ur.restaurant_id = :restaurantId AND u.user_name = :userName", nativeQuery = true)
    User findByRestaurantIdAndUserName(@Param("restaurantId") Long restaurantId, @Param("userName") String userName);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurant r ON ur.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND u.user_name = :userName",
            nativeQuery = true)
    User findByRestaurantCodeAndUserName(
            @Param("restaurantCode") String restaurantCode,
            @Param("userName") String userName);

    @Query(
            value = "SELECT COUNT(u.id) FROM users u " +
                    "JOIN user_restaurant ur ON u.id = ur.user_id " +
                    "JOIN restaurant r ON ur.restaurant_id = r.id " +
                    "WHERE r.id = :restaurantId AND u.role = :role",
            nativeQuery = true
    )
    long countByRestaurantIdAndRole(Long restaurantId, Role role);
}
