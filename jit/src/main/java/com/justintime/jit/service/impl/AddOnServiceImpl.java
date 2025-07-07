package com.justintime.jit.service.impl;

import com.justintime.jit.entity.AddOn;
import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.repository.AddOnRepository;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.service.AddOnService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddOnServiceImpl extends BaseServiceImpl<AddOn, Long> implements AddOnService {

    @Autowired
    private AddOnRepository addOnRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<AddOnDTO> getAddOnsForRestaurant(String restaurantCode){
        List<AddOn> addOns = addOnRepository.findAllByRestaurant_RestaurantCode(restaurantCode);
        GenericMapper<AddOn, AddOnDTO> mapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);
    }

}
