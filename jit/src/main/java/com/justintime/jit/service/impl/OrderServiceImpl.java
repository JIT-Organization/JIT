package com.justintime.jit.service.impl;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.repository.PaymentRepo.PaymentRepository;
import com.justintime.jit.repository.ReservationRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.OrderService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
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
    private MenuItemRepository menuItemRepository;

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private UserRepository userRepository;

    private final GenericMapper<OrderItem, OrderItemDTO> orderItemMapper =
            MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);


    @Override
    public ResponseEntity<String> createOrder(String restaurantCode, OrderDTO orderDTO) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        Order order = mapper.toEntity(orderDTO);
        order.setRestaurant(restaurant);

        // need to check this is correct
        order.setUser(userRepository.findByRestaurantIdAndRoleAndUserName(restaurant.getId(), Role.COOK, orderDTO.getOrderedBy()));
        order.setStatus(OrderStatus.NEW);
        
        // Calculate and set serve time based on order items
        LocalDateTime serveTime = calculateServeTime(orderDTO, restaurantCode);
        order.setServeTime(serveTime);
        
        resolveRelationships(order, orderDTO);
        Order savedOrder = orderRepository.save(order);
        saveEachOrderItem(orderDTO, restaurantCode, savedOrder);
        if (savedOrder.getId() != null) {
            return ResponseEntity.ok("Order created successfully with Order Number: " + savedOrder.getOrderNumber());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order.");
        }
    }

    @Override
    public List<OrderDTO> getOrdersByRestaurantId(String restaurantCode) {
        List<Order> orders = orderRepository.findByRestaurantCode(restaurantCode);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for restaurant with code: " + restaurantCode);
        }
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        return orders.stream()
                .map(order -> mapToDTO(order, mapper))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderByRestaurantAndId(String restaurantCode, Long id) {
        Order order = orderRepository.findByRestaurantCodeAndId(restaurantCode, id);

        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        return mapToDTO(order, mapper);
    }

    @Override
    public OrderDTO updateOrderStatus(String restaurantCode, Long id, OrderStatus status) {
        Order existingOrder = orderRepository.findByRestaurantCodeAndId(restaurantCode, id);
        existingOrder.setStatus(status);  // Update the order's status
        orderRepository.save(existingOrder);
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        return mapToDTO(existingOrder, mapper);
    }

    @Override
    public OrderDTO patchUpdateOrder(String restaurantCode, Long orderId, OrderDTO orderDTO, HashSet<String> propertiesToBeUpdated){
        Order existingOrder = orderRepository.findByRestaurantCodeAndId(restaurantCode, orderId);
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        existingOrder.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")));
        Order patchedOrder = mapper.toEntity(orderDTO);
        String resCode = "TGSR";
        resolveRelationships(patchedOrder, orderDTO);
        commonServiceImplUtil.copySelectedProperties(patchedOrder, existingOrder,propertiesToBeUpdated);
        existingOrder.setUpdatedDttm(LocalDateTime.now());
        orderRepository.save(existingOrder);
        return mapToDTO(existingOrder,mapper);
    }

    @Override
    public void deleteOrder(String restaurantCode, Long id) {
        Order existingOrder = orderRepository.findByRestaurantCodeAndId(restaurantCode, id);
        orderRepository.delete(existingOrder);
    }

    public BigDecimal calculateTotalRevenue(String restaurantCode) {
        List<Order> orders = orderRepository.findByRestaurantCode(restaurantCode);
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
        dto.setOrderItems(order.getOrderItems().stream()
                .map(
                        orderItem -> OrderItemDTO.builder()
                                    .orderItemStatus(orderItem.getOrderItemStatus())
                                    .itemName(Objects.nonNull(orderItem.getMenuItem()) ?
                                            orderItem.getMenuItem().getMenuItemName() : orderItem.getCombo().getComboName())
//                                    .isCombo(Objects.nonNull(orderItem.getCombo()))
                                    .quantity(orderItem.getQuantity())
                                    .totalPrice(orderItem.getTotalPrice())
                                    .build()
                )
                .toList());
        return dto;
    }

    private void saveEachOrderItem(OrderDTO orderDTO, String resCode, Order savedOrder) {
        List<OrderItemDTO> orderItemDTOList = orderDTO.getOrderItems();

        Set<String> comboItemNames = new HashSet<>();
        Set<String> menuItemNames = new HashSet<>();

        for (OrderItemDTO dto : orderItemDTOList) {
            if (FoodType.COMBO.equals(dto.getFoodType())) {
                comboItemNames.add(dto.getItemName());
            } else {
                menuItemNames.add(dto.getItemName());
            }
        }

        Map<String, Combo> comboMap = comboRepository
                .findByComboNamesAndRestaurantCode(comboItemNames, resCode)
                .stream()
                .collect(Collectors.toMap(Combo::getComboName, Function.identity()));

        Map<String, MenuItem> menuItemMap = menuItemRepository
                .findByMenuItemNamesAndRestaurantCode(menuItemNames, resCode)
                .stream()
                .collect(Collectors.toMap(MenuItem::getMenuItemName, Function.identity()));

        for (OrderItemDTO dto : orderItemDTOList) {
            OrderItem orderItem = orderItemMapper.toEntity(dto);

            if (FoodType.COMBO.equals(dto.getFoodType())) {
                orderItem.setCombo(comboMap.get(dto.getItemName()));
                // Calculate and set MaxTimeLimitToStart for combo items
                calculateAndSetMaxTimeLimitForCombo(orderItem, resCode);
            } else {
                orderItem.setMenuItem(menuItemMap.get(dto.getItemName()));
                // Calculate and set MaxTimeLimitToStart for menu items
                calculateAndSetMaxTimeLimitForMenuItem(orderItem, resCode);
            }

            orderItem.setOrder(savedOrder);
        }
    }

    /**
     * Calculate serve time based on maximum time required across all order items.
     * For each order item: maxTimeLimitToStart + cookingTime from its menu item.
     * Return the maximum time from order time.
     */
    private LocalDateTime calculateServeTime(OrderDTO orderDTO, String restaurantCode) {
        List<OrderItemDTO> orderItemDTOList = orderDTO.getOrderItems();
        
        // Get current time as base (order time)
        LocalDateTime orderTime = LocalDateTime.now();
        
        Set<String> comboItemNames = new HashSet<>();
        Set<String> menuItemNames = new HashSet<>();

        // Separate combo items and menu items
        for (OrderItemDTO dto : orderItemDTOList) {
            if (FoodType.COMBO.equals(dto.getFoodType())) {
                comboItemNames.add(dto.getItemName());
            } else {
                menuItemNames.add(dto.getItemName());
            }
        }

        // Fetch combos and menu items
        Map<String, Combo> comboMap = comboRepository
                .findByComboNamesAndRestaurantCode(comboItemNames, restaurantCode)
                .stream()
                .collect(Collectors.toMap(Combo::getComboName, Function.identity()));

        Map<String, MenuItem> menuItemMap = menuItemRepository
                .findByMenuItemNamesAndRestaurantCode(menuItemNames, restaurantCode)
                .stream()
                .collect(Collectors.toMap(MenuItem::getMenuItemName, Function.identity()));

        long maxTimeInMinutes = 0;

        // Calculate maximum time required across all order items
        for (OrderItemDTO dto : orderItemDTOList) {
            long itemTimeInMinutes = 0;
            
            if (FoodType.COMBO.equals(dto.getFoodType())) {
                Combo combo = comboMap.get(dto.getItemName());
                if (combo != null) {
                    // For combos, use preparation time directly
                    // In a real scenario, you might want to calculate the maximum preparation time
                    // from all combo items, but for now using the combo's preparation time
                    itemTimeInMinutes = combo.getPreparationTime() != null ? combo.getPreparationTime() : 0;
                }
            } else {
                MenuItem menuItem = menuItemMap.get(dto.getItemName());
                if (menuItem != null) {
                    // For menu items, use preparation time directly
                    // In a real scenario, you might want to add cook availability calculation
                    itemTimeInMinutes = menuItem.getPreparationTime() != null ? menuItem.getPreparationTime() : 0;
                }
            }
            
            // Update maximum time
            maxTimeInMinutes = Math.max(maxTimeInMinutes, itemTimeInMinutes);
        }

        // Return order time + maximum preparation time
        return orderTime.plusMinutes(maxTimeInMinutes);
    }

    /**
     * Calculate and set MaxTimeLimitToStart for a menu item based on cook's availability
     * Also assigns the order item to the most quickly available cook
     */
    private void calculateAndSetMaxTimeLimitForMenuItem(OrderItem orderItem, String restaurantCode) {
        MenuItem menuItem = orderItem.getMenuItem();
        if (menuItem == null || menuItem.getCookSet() == null || menuItem.getCookSet().isEmpty()) {
            orderItem.setMaxTimeLimitToStart(LocalDateTime.now()); // Default if no cooks assigned - can start immediately
            return;
        }

        // Find the most quickly available cook among all responsible cooks
        User mostAvailableCook = null;
        long shortestWaitTime = Long.MAX_VALUE;

        for (User responsibleCook : menuItem.getCookSet()) {
            long cookAvailabilityTime = calculateCookAvailabilityTime(responsibleCook, restaurantCode);
            if (cookAvailabilityTime < shortestWaitTime) {
                shortestWaitTime = cookAvailabilityTime;
                mostAvailableCook = responsibleCook;
            }
        }

        // Assign the order item to the most available cook
        if (mostAvailableCook != null) {
            orderItem.setCook(mostAvailableCook);
            // Calculate the actual start time by adding wait time to current time
            orderItem.setMaxTimeLimitToStart(LocalDateTime.now().plusMinutes(shortestWaitTime));
        } else {
            orderItem.setMaxTimeLimitToStart(LocalDateTime.now());
        }
    }

    /**
     * Calculate and set MaxTimeLimitToStart for a combo item based on cook's availability
     * Also assigns each combo item to the most quickly available cook
     */
    private void calculateAndSetMaxTimeLimitForCombo(OrderItem orderItem, String restaurantCode) {
        Combo combo = orderItem.getCombo();
        if (combo == null) {
            orderItem.setMaxTimeLimitToStart(LocalDateTime.now()); // Default if no combo - can start immediately
            return;
        }

        // For combos, we need to check all responsible cooks for each combo item
        // and find the maximum time required across all combo items
        long maxTimeLimitToStart = 0L;
        Map<User, Long> cookAssignments = new HashMap<>();

        // Assuming combo has a collection of combo items with menu items
        if (combo.getComboItemSet() != null) {
            for (ComboItem comboItem : combo.getComboItemSet()) {
                if (comboItem.getMenuItem() != null && 
                    comboItem.getMenuItem().getCookSet() != null && 
                    !comboItem.getMenuItem().getCookSet().isEmpty()) {
                    
                    // Find the most quickly available cook for this combo item
                    User mostAvailableCook = null;
                    long shortestWaitTime = Long.MAX_VALUE;

                    for (User responsibleCook : comboItem.getMenuItem().getCookSet()) {
                        long cookAvailabilityTime = calculateCookAvailabilityTime(responsibleCook, restaurantCode);
                        
                        // Consider existing assignments in this combo to balance load
                        if (cookAssignments.containsKey(responsibleCook)) {
                            cookAvailabilityTime += cookAssignments.get(responsibleCook);
                        }

                        if (cookAvailabilityTime < shortestWaitTime) {
                            shortestWaitTime = cookAvailabilityTime;
                            mostAvailableCook = responsibleCook;
                        }
                    }

                    // Assign this combo item to the most available cook
                    if (mostAvailableCook != null) {
                        // Track cook assignments within this combo
                        Integer preparationTime = comboItem.getMenuItem().getPreparationTime();
                        cookAssignments.put(mostAvailableCook, 
                            cookAssignments.getOrDefault(mostAvailableCook, 0L) + 
                            (preparationTime != null ? preparationTime.longValue() : 0L));
                        
                        maxTimeLimitToStart = Math.max(maxTimeLimitToStart, shortestWaitTime);
                    }
                }
            }
        }
        
        // Calculate the actual start time by adding wait time to current time
        orderItem.setMaxTimeLimitToStart(LocalDateTime.now().plusMinutes(maxTimeLimitToStart));
    }

    /**
     * Calculate when a cook will be available based on their current workload
     */
    private long calculateCookAvailabilityTime(User cook, String restaurantCode) {
        LocalDateTime currentTime = LocalDateTime.now();
        
        // Get all orders for this restaurant
        List<Order> allOrders = orderRepository.findByRestaurantCode(restaurantCode);
        
        // Filter to get only pending/in-progress orders
        List<Order> pendingOrders = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.NEW || 
                               order.getStatus() == OrderStatus.PREPARING || 
                               order.getStatus() == OrderStatus.SERVING)
                .collect(Collectors.toList());

        long totalWorkloadMinutes = 0L;

        // Calculate current workload for this cook
        for (Order order : pendingOrders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                User itemCook = null;
                Long cookingTime = 0L;

                // Check if this order item is assigned to our cook
                if (orderItem.getMenuItem() != null && orderItem.getCook() != null) {
                    // Use the assigned cook for this order item
                    itemCook = orderItem.getCook();
                    cookingTime = orderItem.getMenuItem().getPreparationTime() != null ? 
                        orderItem.getMenuItem().getPreparationTime().longValue() : 0L;
                } else if (orderItem.getMenuItem() != null && orderItem.getMenuItem().getCookSet() != null) {
                    // Fallback: check if cook is among responsible cooks (for backward compatibility)
                    for (User responsibleCook : orderItem.getMenuItem().getCookSet()) {
                        if (responsibleCook.getId().equals(cook.getId())) {
                            itemCook = cook;
                            cookingTime = orderItem.getMenuItem().getPreparationTime() != null ? 
                                orderItem.getMenuItem().getPreparationTime().longValue() : 0L;
                            break;
                        }
                    }
                } else if (orderItem.getCombo() != null && orderItem.getCombo().getComboItemSet() != null) {
                    // For combos, check if any combo item is assigned to our cook
                    for (ComboItem comboItem : orderItem.getCombo().getComboItemSet()) {
                        if (comboItem.getMenuItem() != null && 
                            comboItem.getMenuItem().getCookSet() != null) {
                            // Check if cook is among responsible cooks for this combo item
                            for (User responsibleCook : comboItem.getMenuItem().getCookSet()) {
                                if (responsibleCook.getId().equals(cook.getId())) {
                                    itemCook = cook;
                                    cookingTime = Math.max(cookingTime, 
                                        comboItem.getMenuItem().getPreparationTime() != null ? 
                                            comboItem.getMenuItem().getPreparationTime().longValue() : 0L);
                                    break;
                                }
                            }
                        }
                    }
                }

                // If this cook is responsible for this item, add to workload
                if (itemCook != null && itemCook.getId().equals(cook.getId())) {
                    // Calculate remaining time for this order item
                    LocalDateTime orderTime = order.getOrderDate() != null ? order.getOrderDate() : order.getCreatedDttm();
                    LocalDateTime expectedStartTime = orderItem.getMaxTimeLimitToStart() != null ? orderItem.getMaxTimeLimitToStart() : orderTime;
                    LocalDateTime expectedEndTime = expectedStartTime.plusMinutes(cookingTime != null ? cookingTime : 0);

                    // If the item is still being prepared or will be prepared, add to workload
                    if (expectedEndTime.isAfter(currentTime)) {
                        long remainingTime = expectedEndTime.isAfter(currentTime) ? 
                            java.time.Duration.between(currentTime, expectedEndTime).toMinutes() : 0;
                        totalWorkloadMinutes += Math.max(0, remainingTime);
                    }
                }
            }
        }

        return totalWorkloadMinutes;
    }
}
