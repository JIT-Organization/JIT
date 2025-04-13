package com.justintime.jit.service.impl;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.repository.PaymentRepo.PaymentRepository;
import com.justintime.jit.repository.ReservationRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.OrderService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CommonServiceImplUtil commonServiceImplUtil;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ResponseEntity<String> createOrder(Long restaurantId, Long userId, OrderDTO orderDTO) {
        if (orderDTO.getPaymentNumber()==null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Please select a payment method before placing an order");
        }
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        Order order = mapper.toEntity(orderDTO);
        order.setRestaurant(restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId)));
        resolveRelationships(order, orderDTO);
        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
        resolveRelationships(order, orderDTO);
        Order savedOrder = orderRepository.save(order);
        if (savedOrder.getId() != null) {
            return ResponseEntity.ok("Order created successfully with Order Number: " + savedOrder.getOrderNumber());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order.");
        }
    }

    @Override
    public List<OrderDTO> getOrdersByRestaurantId(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for restaurant with id: " + restaurantId);
        }
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        return orders.stream()
                .map(order -> mapToDTO(order, mapper))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderByRestaurantAndId(Long restaurantId, Long id) {
        Order order = orderRepository.findByRestaurantIdAndId(restaurantId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id + " for restaurant: " + restaurantId));

        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        return mapToDTO(order, mapper);
    }

    @Override
    public OrderDTO updateOrderStatus(Long restaurantId, Long id, OrderStatus status) {
        Order existingOrder = orderRepository.findByRestaurantIdAndId(restaurantId, id)
                .orElseThrow(()->new ResourceNotFoundException("Order not found with id: " + id + " for restaurant: " + restaurantId));
        existingOrder.setStatus(status);  // Update the order's status
        orderRepository.save(existingOrder);
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        return mapToDTO(existingOrder, mapper);
    }

    @Override
    public OrderDTO patchUpdateOrder(Long restaurantId, Long orderId, OrderDTO orderDTO, HashSet<String> propertiesToBeUpdated){
        Order existingOrder = orderRepository.findByRestaurantIdAndId(restaurantId, orderId)
                .orElseThrow(()->new ResourceNotFoundException("Order not found with id: " + orderId + " for restaurant: " + restaurantId));
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        existingOrder.setRestaurant(restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found")));
        Order patchedOrder = mapper.toEntity(orderDTO);
        resolveRelationships(patchedOrder, orderDTO);
        commonServiceImplUtil.copySelectedProperties(patchedOrder, existingOrder,propertiesToBeUpdated);
        existingOrder.setUpdatedDttm(LocalDateTime.now());
        orderRepository.save(existingOrder);
        return mapToDTO(existingOrder,mapper);
    }

    @Override
    public void deleteOrder(Long restaurantId, Long id) {
        Order existingOrder = orderRepository.findByRestaurantIdAndId(restaurantId, id)
                .orElseThrow(()->new ResourceNotFoundException("Order not found with id: " + id + " for restaurant: " + restaurantId));
        orderRepository.delete(existingOrder);
    }

    public BigDecimal calculateTotalRevenue(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        return orders.stream()
                .flatMap(order -> order.getPayments().stream())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); }

    @Override
    public List<OrderDTO> getOrdersByRestaurantAndUserId(Optional<Long> restaurantId, Optional<Long> userId)
    {
        List<Order> orders;
        if (userId.isPresent() && restaurantId.isPresent()) {
            orders = orderRepository.findByRestaurantIdAndUserId(restaurantId.get(), userId.get());
        }
        else if (userId.isPresent()) {
            orders = orderRepository.findByUserId(userId.get());
        }
        else if (restaurantId.isPresent()) {
            orders = orderRepository.findByRestaurantId(restaurantId.get());
        }
        else orders = orderRepository.findAll();
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        return orders.stream()
                .map(order -> mapToDTO(order, mapper))
                .collect(Collectors.toList());
    }

    private void resolveRelationships(Order order, OrderDTO orderDTO){
        if (orderDTO.getReservationNumber()!=null){
            Reservation reservation = reservationRepository.findByReservationNumber(orderDTO.getReservationNumber());
            if (reservation == null) {
                throw new ResourceNotFoundException("Reservation not found with number: " + orderDTO.getReservationNumber());
            }
            order.setReservation(reservation);
        }
        if (orderDTO.getPaymentNumber()!=null){
            List<Payment> payment = paymentRepository.findByOrderId(orderDTO.getId());
            order.setPayments(payment);
        }

    }

    private OrderDTO mapToDTO(Order order, GenericMapper<Order, OrderDTO> mapper){
        OrderDTO dto = mapper.toDto(order);
        dto.setOrderedBy(order.getUser().getFirstName()+" "+order.getUser().getLastName());
        dto.setDiningTables(order.getReservation().getDiningTableSet()
                .stream()
                .map(DiningTable::getTableNumber)
                .collect(Collectors.toList()));
        dto.setPaymentNumber(order.getPayments().stream()
                .map(Payment::getPaymentNumber)
                .collect(Collectors.toList()));
        return dto;
    }

    private void copySelectedProperties(Object source, Object target, List<String> propertiesToBeChanged) {
        BeanWrapper srcWrapper = new BeanWrapperImpl(source);
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);

        for (String property : propertiesToBeChanged) {
            if (srcWrapper.isReadableProperty(property) && srcWrapper.getPropertyValue(property) != null) {
                targetWrapper.setPropertyValue(property, srcWrapper.getPropertyValue(property));
            }
        }
    }
}
