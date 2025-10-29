package com.justintime.jit.repository;

import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.RestaurantRole;
import com.justintime.jit.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.justintime.jit.entity.MenuItem;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {

    // Find all users by restaurant role
    List<User> findByRestaurantRole(RestaurantRole restaurantRole);

    // Find a user by email
    User findByEmail(String email);

    List<User> findByUsername(String username);

    // Do not delete
    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurant r ON ur.restaurant_id = r.id " +
            "WHERE r.id = :restaurantId AND u.restaurant_role_id = :roleId AND u.user_name = :userName", nativeQuery = true)
    User findByRestaurantIdAndRoleAndUserName(
            @Param("restaurantId") Long restaurantId,
            @Param("roleId") Long roleId,
            @Param("userName") String userName);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurant r ON ur.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND u.restaurant_role_id = :roleId AND u.user_name = :userName",
            nativeQuery = true)
    User findByRestaurantCodeAndRoleAndUserName(
            @Param("restaurantCode") String restaurantCode,
            @Param("roleId") Long roleId,
            @Param("userName") String userName);

    @Query(value = "SELECT r.id FROM restaurant r " +
            "JOIN user_restaurant ur ON r.id = ur.restaurant_id " +
            "JOIN users u ON ur.user_id = u.id " +
            "WHERE u.email = :email", nativeQuery = true)
    List<Long> findRestaurantIdsByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN u.restaurants r " +
            "WHERE r.id = :restaurantId AND u.restaurantRole.role_type = :roleType AND u.username IN :cookNames")
    Set<User> findCooksByRestaurantIdAndRoleAndUserNames(@Param("restaurantId") Long restaurantId,
                                                          @Param("roleType") Role roleType,
                                                          @Param("cookNames") Set<String> cookNames);
    @Query(value = "SELECT u.user_name " +
            "FROM users u " +
            "JOIN user_restaurant ur ON u.id = ur.user_id " +
            "JOIN restaurants r ON ur.restaurant_id = r.id " +
            "WHERE r.restaurant_code = :restaurantCode AND u.restaurantRole.role_type = :roleType", nativeQuery = true)
    List<String> findUserNamesByRestaurantCodeAndRole(@Param("restaurantCode") String restaurantCode,
                                                      @Param("roleType") Role roleType);

//    DO NOT DELETE
//    @Query("SELECT u FROM User u " +
//            "JOIN u.restaurants r " +
//            "WHERE r.restaurantCode = :restaurantCode " +
//            "AND u.restaurantRole.id = :roleId " +
//            "AND u.userName IN :cookNames")
//    Set<User> findCooksByRestaurantCodeAndRoleAndUserNames(
//            @Param("restaurantCode") String restaurantCode,
//            @Param("roleId") Long roleId,
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
                    "WHERE r.id = :restaurantId AND u.restaurant_role_id = :roleId",
            nativeQuery = true
    )
    long countByRestaurantIdAndRole(Long restaurantId, Long roleId);
}
