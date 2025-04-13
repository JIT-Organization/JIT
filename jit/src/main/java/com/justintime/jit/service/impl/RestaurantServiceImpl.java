package com.justintime.jit.service.impl;

import com.justintime.jit.dto.RestaurantDTO;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.RestaurantService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RestaurantServiceImpl extends BaseServiceImpl<Restaurant,Long> implements RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;

    private static final int SUGGESTION_THRESHOLD = 3;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    @Override
    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }


    @Override
    public void updateRestaurant(String code, Restaurant restaurant) {
        Restaurant existingRestaurant = restaurantRepository.findByRestaurantCode(code);

        existingRestaurant.setRestaurantName(restaurant.getRestaurantName());
        existingRestaurant.setContactNumber(restaurant.getContactNumber());
        existingRestaurant.setEmail(restaurant.getEmail());
        existingRestaurant.setMenu(restaurant.getMenu());
        existingRestaurant.setOrders(restaurant.getOrders());
        existingRestaurant.setContactNumber(restaurant.getContactNumber());

        restaurantRepository.save(existingRestaurant);
    }

    @Override
    public void deleteRestaurant(String restaurantCode) {
        Restaurant existingRestaurant = restaurantRepository.findByRestaurantCode(restaurantCode);
        restaurantRepository.delete(existingRestaurant);
    }

    @Override
    public RestaurantDTO getRestaurantByRestaurantCode(String restaurantCode) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode);
        GenericMapper<Restaurant, RestaurantDTO> mapper = MapperFactory.getMapper(Restaurant.class, RestaurantDTO.class);
        return mapper.toDto(restaurant);
    }

    @Override
    public void patchUpdateRestaurant(String restaurantCode, RestaurantDTO dto, HashSet<String> propertiesToBeUpdated) {
        Restaurant existingItem = restaurantRepository.findByRestaurantCode(restaurantCode);
        GenericMapper<Restaurant, RestaurantDTO> restaurantMapper = MapperFactory.getMapper(Restaurant.class, RestaurantDTO.class);
        Restaurant patchedRestaurant = restaurantMapper.toEntity(dto);
        commonServiceImplUtil.copySelectedProperties(patchedRestaurant, existingItem, propertiesToBeUpdated);
        existingItem.setUpdatedDttm(LocalDateTime.now());
        restaurantRepository.save(existingItem);
    }
}