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

    @Query(value = "SELECT r.id FROM restaurant r " +
            "JOIN user_restaurant ur ON r.id = ur.restaurant_id " +
            "JOIN users u ON ur.user_id = u.id " +
            "WHERE u.email = :email", nativeQuery = true)
    List<Long> findRestaurantIdsByEmail(@Param("email") String email);;
}
