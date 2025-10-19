package com.justintime.jit.service.impl;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.OrderItemStatus;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.event.OrderCreatedEvent;
import com.justintime.jit.event.OrderStatusUpdateEvent;
import com.justintime.jit.event.TableAvailabilityEvent;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.service.*;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl extends BaseServiceImpl<Order, Long> implements OrderService {

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final OrderRepository orderRepository;

    private final CommonServiceImplUtil commonServiceImplUtil;

    private final RestaurantService restaurantService;

    private final ReservationService reservationService;

    private final UserService userService;

    private final OrderItemService orderItemService;

    private final PaymentService paymentService;

    private final DiningTableService tableService;

    private final static Set<OrderItemStatus> activeOrderItemStatuses = Set.of(
            OrderItemStatus.STARTED,
            OrderItemStatus.READY_TO_SERVE,
            OrderItemStatus.SERVED
    );

    private final GenericMapper<Order, OrderDTO> orderMapper = MapperFactory.getMapper(Order.class, OrderDTO.class);

    @SuppressFBWarnings(value = "EI2", justification = "All the params are Spring-managed beans and are not exposed.")
    public OrderServiceImpl(OrderRepository orderRepository, CommonServiceImplUtil commonServiceImplUtil, RestaurantService restaurantService, ReservationService reservationService, UserService userService, OrderItemService orderItemService, PaymentService paymentService, DiningTableService tableService) {
        this.orderRepository = orderRepository;
        this.commonServiceImplUtil = commonServiceImplUtil;
        this.restaurantService = restaurantService;
        this.reservationService = reservationService;
        this.userService = userService;
        this.orderItemService = orderItemService;
        this.paymentService = paymentService;
        this.tableService = tableService;
    }

    @Override
    public ResponseEntity<String> createOrder(OrderDTO orderDTO) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        String username = getUsernameFromJWTBean();
        Restaurant restaurant = restaurantService.getRestaurantByRestaurantCode();
        Order order = orderMapper.toEntity(orderDTO);
        order.setRestaurant(restaurant);

        if (StringUtils.isNotEmpty(username)) {
            User orderedBy = userService.getUserByRestaurantCodeAndUsername(username);
            if (orderedBy != null) {
                order.setUser(orderedBy);
            } else {
                throw new ResourceNotFoundException("User not found with name: " + username + " for restaurant: " + restaurantCode);
            }
        } else {
            throw new IllegalArgumentException("Order must have a customer (orderedBy field cannot be null or empty)");
        }
        order.setStatus(OrderStatus.NEW);
        Order savedOrder = orderRepository.save(order);
        entityManager.flush();
        List<OrderItem> orderItems = orderItemService.createAndPersistOrderItems(orderDTO, restaurantCode, savedOrder);
        List<DiningTable> diningTables = new ArrayList<>();
        orderDTO.getDiningTables().forEach(table -> {
            DiningTable diningTable = tableService.changeAvailabilityStatus(table, false);
            diningTables.add(diningTable);
        });
        publishToOrderCreatedEventListener(orderItems);
        publishToTableAvailabilityListener(diningTables);
        if (savedOrder.getId() != null) {
            return ResponseEntity.ok("Order created successfully with Order Number: " + savedOrder.getOrderNumber());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order.");
        }
    }

    @Override
    public List<OrderDTO> getOrdersByRestaurantId() {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        List<Order> orders = orderRepository.findByRestaurantCode(restaurantCode);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for restaurant with code: " + restaurantCode);
        }
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderByRestaurantAndOrderNumber(String restaurantCode, String orderNumber) {
        Order order = orderRepository.findByRestaurantCodeAndOrderNumber(restaurantCode, orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber + " for restaurant: " + restaurantCode));
        return mapToDTO(order);
    }

    @Override
    public OrderDTO updateOrderStatus(String restaurantCode, String orderNumber, OrderStatus status) {
        Order existingOrder = orderRepository.findByRestaurantCodeAndOrderNumber(restaurantCode, orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber + " for restaurant: " + restaurantCode));
        existingOrder.setStatus(status);  // Update the order's status
        orderRepository.save(existingOrder);
        return mapToDTO(existingOrder);
    }

    @Override
    public OrderDTO patchUpdateOrder(String restaurantCode, String orderNumber, OrderDTO orderDTO, HashSet<String> propertiesToBeUpdated){
        Order existingOrder = orderRepository.findByRestaurantCodeAndOrderNumber(restaurantCode, orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber + " for restaurant: " + restaurantCode));
        existingOrder.setRestaurant(restaurantService.getRestaurantByRestaurantCode());
        Order patchedOrder = orderMapper.toEntity(orderDTO);
        resolveRelationships(patchedOrder, orderDTO);
        commonServiceImplUtil.copySelectedProperties(patchedOrder, existingOrder,propertiesToBeUpdated);
        existingOrder.setUpdatedDttm(LocalDateTime.now());
        orderRepository.save(existingOrder);
        return mapToDTO(existingOrder);
    }

    @Override
    public void deleteOrder(String restaurantCode, String orderNumber) {
        Order existingOrder = orderRepository.findByRestaurantCodeAndOrderNumber(restaurantCode, orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber + " for restaurant: " + restaurantCode));
        orderRepository.delete(existingOrder);
    }

    public BigDecimal calculateTotalRevenue(String restaurantCode) {
        List<Order> orders = orderRepository.findByRestaurantCode(restaurantCode);
        return orders.stream()
                .flatMap(order -> order.getPayments().stream())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); }

    @Override
    public List<OrderItemDTO> getAllInProgressOrderItemsForRestaurant() {
        return orderItemService.getOrderItemsForRestaurant();
    }

    @Override
    @Transactional
    public OrderItemDTO updateOrderItemStatus(OrderItemDTO orderItemDTO) {
        String restaurantCode = getRestaurantCodeFromJWTBean();
        OrderItemDTO updatedOrderItemDTO = orderItemService.updateOrderItem(orderItemDTO);
        Optional<Order> order = orderRepository.findByRestaurantCodeAndOrderNumber(restaurantCode, orderItemDTO.getOrderNumber());
        order.ifPresent(ord -> {
            boolean isNew = ord.getStatus() == OrderStatus.NEW;
            boolean hasActiveItem = ord.getOrderItems().stream()
                    .anyMatch(item -> activeOrderItemStatuses.contains(item.getOrderItemStatus()));
            boolean allItemsServed = ord.getOrderItems().stream()
                    .allMatch(item -> item.getOrderItemStatus().equals(OrderItemStatus.SERVED));
            if (isNew && hasActiveItem) {
                ord.setStatus(OrderStatus.PREPARING);
            } else if (allItemsServed) {
                ord.setStatus(OrderStatus.SERVED);
            }
            orderRepository.save(ord);
            publishToOrderStatusUpdateEventListener(mapToDTO(ord));
        });
        return updatedOrderItemDTO;
    }

    @Override
    public List<OrderItemDTO> getAllAssignedOrdersForUser() {
        return orderItemService.getAllAssignedOrdersForUser();
    }

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
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void resolveRelationships(Order order, OrderDTO orderDTO){
        if (orderDTO.getReservationNumber()!=null){
            Reservation reservation = reservationService.getReservationByReservationNumber(orderDTO.getReservationNumber());
            if (reservation == null) {
                throw new ResourceNotFoundException("Reservation not found with number: " + orderDTO.getReservationNumber());
            }
            order.setReservation(reservation);
        }
        if (orderDTO.getPaymentNumber()!=null){
            List<Payment> payment = paymentService.getPaymentsByOrderId(order.getId());
            order.setPayments(payment);
        }
    }

    private OrderDTO mapToDTO(Order order){
        OrderDTO dto = orderMapper.toDto(order);
        
        // Safely handle user information
        if (order.getUser() != null) {
            String firstName = order.getUser().getFirstName() != null ? order.getUser().getFirstName() : "";
            String lastName = order.getUser().getLastName() != null ? order.getUser().getLastName() : "";
            dto.setOrderedBy(firstName + " " + lastName); // will this be username when it is coming from ui or similar to this a full name?
            dto.setServerEmail(order.getUser().getEmail());
        }
        
        // Safely handle reservation and dining tables
        if (order.getReservation() != null && order.getReservation().getDiningTableSet() != null) {
            dto.setDiningTables(order.getReservation().getDiningTableSet()
                    .stream()
                    .map(DiningTable::getTableNumber)
                    .collect(Collectors.toList()));
        } else {
            dto.setDiningTables(new ArrayList<>());
        }
        
        // Safely handle payments
        if (order.getPayments() != null) {
            dto.setPaymentNumber(order.getPayments().stream()
                    .map(Payment::getPaymentNumber)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        } else {
            dto.setPaymentNumber(new ArrayList<>());
        }
        
        // Safely handle order items
        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems().stream()
                    .filter(Objects::nonNull)
                    .map(orderItem -> OrderItemDTO.builder()
                                .orderItemStatus(orderItem.getOrderItemStatus())
                                .itemName(Objects.nonNull(orderItem.getMenuItem()) ?
                                        orderItem.getMenuItem().getMenuItemName() : 
                                        (Objects.nonNull(orderItem.getCombo()) ? orderItem.getCombo().getComboName() : "Unknown Item"))
                                .quantity(orderItem.getQuantity())
                                .totalPrice(orderItem.getTotalPrice())
                                .build())
                    .toList());
        } else {
            dto.setOrderItems(new ArrayList<>());
        }
        
        return dto;
    }

    private void publishToOrderCreatedEventListener(List<OrderItem> orderItems) {
        OrderCreatedEvent event = new OrderCreatedEvent(this, orderItems);
        eventPublisher.publishEvent(event);
    }

    private void publishToTableAvailabilityListener(List<DiningTable> diningTables) {
        TableAvailabilityEvent event = new TableAvailabilityEvent(this, diningTables);
        eventPublisher.publishEvent(event);
    }

    private void publishToOrderStatusUpdateEventListener(OrderDTO order) {
        OrderStatusUpdateEvent orderStatusUpdateEvent = OrderStatusUpdateEvent.of(this, order);
        eventPublisher.publishEvent(orderStatusUpdateEvent);
    }
}

/** TODO
Auto assign
Predict the time without assigning

cook's start time(bal time) + assigned food item prep time + unassigned food items prep time(for buffer) -> order item serve time

Show food to all responsible cooks


Batch config
 */