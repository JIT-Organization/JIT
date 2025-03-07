package com.justintime.jit.service.impl;

import com.justintime.jit.dto.RestaurantDTO;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.RestaurantService;
import com.justintime.jit.util.mapper.GenericMapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.*;

@Service
public class RestaurantServiceImpl extends BaseServiceImpl<Restaurant,Long> implements RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private GenericMapperImpl<Restaurant, RestaurantDTO> restaurantMapper;

    private static final int SUGGESTION_THRESHOLD = 3;

    @Override
    public RestaurantDTO addRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantMapper.toEntity(restaurantDTO, Restaurant.class);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return restaurantMapper.toDTO(savedRestaurant, RestaurantDTO.class);
    }


    @Override
    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(item -> restaurantMapper.toDTO(item, RestaurantDTO.class))
                .toList(); // Collecting to a list
    }

    @Override
    public RestaurantDTO getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        return restaurantMapper.toDTO(restaurant, RestaurantDTO.class);
    }

    @Override
    public RestaurantDTO updateRestaurant(Long id, RestaurantDTO restaurantDTO) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        // Copy properties from DTO to entity, ignoring "id" to prevent accidental overwrites
        BeanUtils.copyProperties(restaurantDTO, existingRestaurant, "id");

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        return restaurantMapper.toDTO(updatedRestaurant, RestaurantDTO.class);
    }

    @Override
    public void deleteRestaurant(Long id) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        restaurantRepository.delete(existingRestaurant);
    }

//    public List<String> findSimilarNames(String name) {
//        List<String> allNames = restaurantRepository.findAll().stream()
//                .map(Restaurant::getName)
//                .collect(Collectors.toList());
//
//        return allNames.stream()
//                .filter(existingName -> existingName.toLowerCase().contains(name.toLowerCase()))
//                .collect(Collectors.toList());
//    }
//
//    public String suggestCorrectName(String name) {
//        List<String> allNames = restaurantRepository.findAll().stream()
//                .map(Restaurant::getName)
//                .collect(Collectors.toList());
//
//        LevenshteinDistance distance = new LevenshteinDistance();
//
//        return allNames.stream()
//                .min(Comparator.comparingInt(existingName -> distance.apply(name.toLowerCase(), existingName.toLowerCase())))
//                .filter(existingName -> distance.apply(name.toLowerCase(), existingName.toLowerCase()) <= SUGGESTION_THRESHOLD)
//                .orElse(null);
//    }
}
