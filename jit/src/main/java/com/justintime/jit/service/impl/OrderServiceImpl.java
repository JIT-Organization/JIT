package com.justintime.jit.service.impl;

import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public Order createOrder(Order order) {
        // Ensure the restaurant exists before saving the order
        Long restaurantId = order.getRestaurant().getId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));

        order.setRestaurant(restaurant);  // Associate restaurant
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order existingOrder = getOrderById(id);

        existingOrder.setStatus(status);  // Update the order's status
        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order existingOrder = getOrderById(id);
        orderRepository.delete(existingOrder);
    }

    // Get all orders for a specific restaurant
    public List<Order> getOrdersByRestaurantId(Long restaurantId) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant.isPresent()) {
            return orderRepository.findByRestaurantId(restaurantId);
        } else {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }
    }

    // Calculate total revenue for a specific restaurant
    public BigDecimal calculateTotalRevenue(Long restaurantId) {
        List<Order> orders = getOrdersByRestaurantId(restaurantId);
        return orders.stream() .flatMap(order -> order.getPayments().stream())
                                .map(Payment::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add); }

    @Override
    public List<Order> getOrdersByRestaurantAndCustomerId(Long restaurantId, Long customerId)
    { return orderRepository.findByRestaurantIdAndCustomerId(restaurantId, customerId); }
}
