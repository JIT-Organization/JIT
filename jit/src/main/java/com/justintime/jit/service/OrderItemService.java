package com.justintime.jit.service;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;

import java.util.List;

public interface OrderItemService {
    // TODO Refactor required
    List<OrderItemDTO> getAllOrderItems();
    List<OrderItemDTO> getOrderItemsByRestaurantCodeAndOrderNumber(String restaurantCode,String orderNumber);
    OrderItemDTO updateOrderItem(OrderItemDTO orderItemDTO);
    OrderItemDTO patchOrderItem(String restaurantCode, PatchRequest<OrderItemDTO> payload);
    void saveOrderItem(String restaurantCode,OrderItemDTO orderItemDTO);
    void deleteOrderItem(String restaurantCode, String itemName, String orderNumber);
    List<OrderItemDTO> getOrderItemsForCook(Long cookId);
    List<OrderItemDTO> getOrderItemsForRestaurant();
    List<OrderItemDTO> getOrderItemsForCookByNameAndRestaurant(String cookName, String restaurantCode);
    List<OrderItem> createAndPersistOrderItems(OrderDTO orderDTO, String restaurantCode, Order savedOrder);
}
