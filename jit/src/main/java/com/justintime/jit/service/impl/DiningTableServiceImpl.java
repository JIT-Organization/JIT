package com.justintime.jit.service.impl;

import com.justintime.jit.dto.DiningTableDTO;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.repository.DiningTableRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.DiningTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiningTableServiceImpl implements DiningTableService {
    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public List<DiningTableDTO> getDiningTablesByRestaurantId(Long restaurantId){
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return diningTableRepository.findByRestaurantId(restaurantId);
        } else {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }
    }
}
