package com.justintime.jit.service.impl;

import com.justintime.jit.entity.BaseEntity;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.service.OrderItemService;
import com.justintime.jit.repository.CookRepository;
import com.justintime.jit.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItem,Long> implements OrderItemService {

        @Autowired
        private OrderItemRepository orderItemRepository;

        @Autowired
        private CookRepository cookRepository;

        @Autowired
        private RestaurantRepository restaurantRepository;

        public List<OrderItem> getAllOrderItems() {
            return orderItemRepository.findAll();
        }

        public OrderItem getOrderItemById(Long id) {
            return orderItemRepository.findById(id).orElse(null);
        }

        public OrderItem saveOrderItem(OrderItem orderItem) {
            return orderItemRepository.save(orderItem);
        }

        public void deleteOrderItem(Long id) {
            orderItemRepository.deleteById(id);
        }

        @Override
        public List<OrderItem> getOrderItemsForCook(Long cookId) {
            return orderItemRepository.findOrderItemsByCookIdAndUnassigned(cookId);
        }

        @Override
        public List<OrderItem> getOrderItemsForCookByNameAndRestaurant(String cookName, String restaurantCode) {
            Long restaurantId = restaurantRepository.findByRestaurantCode(restaurantCode)
                    .map(BaseEntity::getId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found with code: " + restaurantCode));
                    
            Long cookId = cookRepository.findByRestaurantIdAndName(restaurantId, cookName)
                    .map(BaseEntity::getId)
                    .orElseThrow(() -> new RuntimeException("Cook not found with name: " + cookName + " and restaurant code: " + restaurantCode));
                    
            return getOrderItemsForCook(cookId);
        }
}
