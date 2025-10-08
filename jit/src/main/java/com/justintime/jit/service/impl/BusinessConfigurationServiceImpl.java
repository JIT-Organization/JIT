package com.justintime.jit.service.impl;

import com.justintime.jit.entity.BusinessConfiguration;
import com.justintime.jit.entity.Enums.ConfigurationName;
import com.justintime.jit.repository.BusinessConfigurationRepository;
import com.justintime.jit.service.BusinessConfigurationService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BusinessConfigurationServiceImpl implements BusinessConfigurationService {

    private final BusinessConfigurationRepository businessConfigurationRepository;

    public BusinessConfigurationServiceImpl(BusinessConfigurationRepository businessConfigurationRepository) {
        this.businessConfigurationRepository = businessConfigurationRepository;
    }

    @Override
    @Cacheable(value = "businessConfig", key = "#restaurantCode + ':' + #configurationName")
    public String getConfigValue(String restaurantCode, ConfigurationName configurationName) {
        return businessConfigurationRepository.findByRestaurantCodeAndConfigurationName(restaurantCode, configurationName)
                .map(BusinessConfiguration::getValue)
                .orElse("N");
    }

    @Override
    @Transactional
    @CacheEvict(value = "businessConfig", key = "#entity.restaurantCode + ':' + #entity.configurationName")
    public BusinessConfiguration saveOrUpdate(BusinessConfiguration entity) {
        return businessConfigurationRepository.save(entity);
    }

    @Override
    @CacheEvict(value = "businessConfig", key = "#restaurantCode + ':' + #configurationName")
    public void evictCacheForRestaurant(String restaurantCode) {
        // Evicts cache for the given restaurantCode
    }
}

