package com.justintime.jit.service;

import com.justintime.jit.entity.BusinessConfiguration;
import com.justintime.jit.entity.Enums.ConfigurationName;

public interface BusinessConfigurationService {
    String getConfigValue(String restaurantCode, ConfigurationName configurationName);
    BusinessConfiguration saveOrUpdate(BusinessConfiguration entity);
    void evictCacheForRestaurant(String restaurantCode);
}
