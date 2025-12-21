package com.justintime.jit.repository;

import com.justintime.jit.entity.BusinessConfiguration;
import com.justintime.jit.entity.Enums.ConfigurationName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessConfigurationRepository extends JpaRepository<BusinessConfiguration, Long> {
    Optional<BusinessConfiguration> findAllByRestaurantCode(String restaurantCode);

    Optional<BusinessConfiguration> findByRestaurantCodeAndConfigurationName(String restaurantCode, ConfigurationName configurationName);
}
