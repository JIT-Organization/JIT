package com.justintime.jit.repository;

import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    // Find all users by role
    List<User> findByRole(Role role);

    // Find a user by email
    User findByEmail(String email);

    List<User> findByUsername(String userName);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurant r ON ur.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode and u.user_name = :username", nativeQuery = true)
    User findByRestaurantCodeAndUsername(@Param("restaurantCode") String restaurantCode, @Param("username") String userName);

    @Query(value = "SELECT r.id FROM restaurant r " +
            "JOIN user_restaurant ur ON r.id = ur.restaurant_id " +
            "JOIN users u ON ur.user_id = u.id " +
            "WHERE u.email = :email", nativeQuery = true)
    List<Long> findRestaurantIdsByEmail(@Param("email") String email);

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
}
