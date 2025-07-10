package com.justintime.jit.service;

import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.OrderEntities.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItemDTO> getAllOrderItems();
    List<OrderItemDTO> getOrderItemsByRestaurantCodeAndOrderNumber(String restaurantCode,String orderNumber);
    OrderItemDTO updateOrderItem(String restaurantCode, OrderItemDTO orderItemDTO);
    OrderItemDTO patchOrderItem(String restaurantCode, PatchRequest<OrderItemDTO> payload);
    void saveOrderItem(String restaurantCode,OrderItemDTO orderItemDTO);
    void deleteOrderItem(String restaurantCode, String itemName, String orderNumber);
    List<OrderItemDTO> getOrderItemsForCook(Long cookId);
    List<OrderItemDTO> getOrderItemsForCookByNameAndRestaurant(String cookName, String restaurantCode);
}
