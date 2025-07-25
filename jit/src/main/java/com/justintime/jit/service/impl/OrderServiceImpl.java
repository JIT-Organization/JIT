package com.justintime.jit.service.impl;

import com.justintime.jit.dto.AddOnDTO;
import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.Enums.OrderItemStatus;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.entity.PaymentEntities.Payment;
import com.justintime.jit.event.OrderCreatedEvent;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.repository.PaymentRepo.PaymentRepository;
import com.justintime.jit.repository.ReservationRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.NotificationService;
import com.justintime.jit.service.OrderService;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.justintime.jit.util.constants.JITConstants.COMBO;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private NotificationService notificationService;

    private final GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);

    private final GenericMapper<Order, OrderDTO> orderMapper = MapperFactory.getMapper(Order.class, OrderDTO.class);

    private final GenericMapper<AddOn, AddOnDTO> addOnMapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);

    @Override
    public ResponseEntity<String> createOrder(String restaurantCode, OrderDTO orderDTO) {
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        Order order = orderMapper.toEntity(orderDTO);
        order.setRestaurant(restaurant);

        if (orderDTO.getOrderedBy() != null && !orderDTO.getOrderedBy().trim().isEmpty()) {
            User customer = userRepository.findByRestaurantCodeAndUserName(restaurantCode, orderDTO.getOrderedBy());
            if (customer != null) {
                order.setUser(customer);
            } else {
                throw new ResourceNotFoundException("User not found with name: " + orderDTO.getOrderedBy() + " for restaurant: " + restaurantCode);
            }
        } else {
            throw new IllegalArgumentException("Order must have a customer (orderedBy field cannot be null or empty)");
        }
        order.setStatus(OrderStatus.NEW);
//        resolveRelationships(order, orderDTO); // Commented this out we need not require this here
        Order savedOrder = orderRepository.save(order);
        entityManager.flush();
        List<OrderItem> orderItems = createAndPersistOrderItems(orderDTO, restaurantCode, savedOrder);
        publishToOrderCreatedEventListener(orderItems);
//        notificationService.notifyOrderItemCreation(orderItems);
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
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderByRestaurantAndId(String restaurantCode, Long id) {
        Order order = orderRepository.findByRestaurantCodeAndId(restaurantCode, id);
        return mapToDTO(order);
    }

    @Override
    public OrderDTO updateOrderStatus(String restaurantCode, Long id, OrderStatus status) {
        Order existingOrder = orderRepository.findByRestaurantCodeAndId(restaurantCode, id);
        existingOrder.setStatus(status);  // Update the order's status
        orderRepository.save(existingOrder);
        return mapToDTO(existingOrder);
    }

    @Override
    public OrderDTO patchUpdateOrder(String restaurantCode, Long orderId, OrderDTO orderDTO, HashSet<String> propertiesToBeUpdated){
        Order existingOrder = orderRepository.findByRestaurantCodeAndId(restaurantCode, orderId);
        existingOrder.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found")));
        Order patchedOrder = orderMapper.toEntity(orderDTO);
        resolveRelationships(patchedOrder, orderDTO);
        commonServiceImplUtil.copySelectedProperties(patchedOrder, existingOrder,propertiesToBeUpdated);
        existingOrder.setUpdatedDttm(LocalDateTime.now());
        orderRepository.save(existingOrder);
        return mapToDTO(existingOrder);
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
        return orders.stream()
                .map(this::mapToDTO)
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

    private OrderDTO mapToDTO(Order order){
        OrderDTO dto = orderMapper.toDto(order);
        
        // Safely handle user information
        if (order.getUser() != null) {
            String firstName = order.getUser().getFirstName() != null ? order.getUser().getFirstName() : "";
            String lastName = order.getUser().getLastName() != null ? order.getUser().getLastName() : "";
            dto.setOrderedBy(firstName + " " + lastName);
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

    private List<OrderItem> createAndPersistOrderItems(OrderDTO orderDTO, String restaurantCode, Order savedOrder) {
        List<OrderItemDTO> orderItemDTOList = orderDTO.getOrderItems();
        
        if (orderItemDTOList == null || orderItemDTOList.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        } // this must be done before coming to service

        Set<String> comboItemNames = new HashSet<>();
        Set<String> menuItemNames = new HashSet<>();

        for (OrderItemDTO dto : orderItemDTOList) {
            if (dto == null || dto.getItemName() == null || dto.getItemName().trim().isEmpty()) {
                continue;
            } // check this in the validation itself
            
            if (COMBO.equalsIgnoreCase(dto.getFoodType())) {
                comboItemNames.add(dto.getItemName());
            } else {
                menuItemNames.add(dto.getItemName());
            }
        }

        Map<String, Combo> comboMap = comboRepository
                .findByComboNamesAndRestaurantCode(comboItemNames, restaurantCode)
                .stream()
                .collect(Collectors.toMap(Combo::getComboName, Function.identity()));

        Map<String, MenuItem> menuItemMap = menuItemRepository
                .findByMenuItemNamesAndRestaurantCode(menuItemNames, restaurantCode)
                .stream()
                .collect(Collectors.toMap(MenuItem::getMenuItemName, Function.identity()));

        List<OrderItem> orderItems = buildOrderItemEntities(orderItemDTOList, comboMap, menuItemMap, savedOrder, restaurantCode);
        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }

    private List<OrderItem> buildOrderItemEntities(List<OrderItemDTO> orderItemDTOList, Map<String, Combo> comboMap, Map<String, MenuItem> menuItemMap, Order savedOrder, String restaurantCode) {
        List<OrderItem> allOrderItems = new ArrayList<>();
        for (OrderItemDTO dto : orderItemDTOList) {
            OrderItem item = orderItemMapper.toEntity(dto);
            item.setOrder(savedOrder);
            if (COMBO.equalsIgnoreCase(dto.getFoodType())) {
                Combo combo = comboMap.get(dto.getItemName());
//                if (combo == null) throw new IllegalArgumentException("Invalid combo: " + dto.getItemName());
                item.setCombo(combo);
                List<OrderItem> subItems = setMaxTimeLimitForCombo(item, restaurantCode);
                allOrderItems.addAll(subItems);
            } else {
                MenuItem menuItem = menuItemMap.get(dto.getItemName());
//                if (menuItem == null) throw new IllegalArgumentException("Invalid menu item: " + dto.getItemName());
                item.setMenuItem(menuItem);
                setMaxTimeLimitAndAssignCook(item, restaurantCode);
            }
            if (dto.getAddOns() != null) {
                Set<AddOn> addOns = dto.getAddOns().stream()
                        .map(addOnMapper::toEntity)
                        .collect(Collectors.toSet());
                item.setAddOnSet(addOns);
            }
            allOrderItems.add(item);
        }
        return allOrderItems;
    }

    /**
     * Calculate and set MaxTimeLimitToStart for a menu item based on cook's availability
     * Also assigns the order item to the most quickly available cook
     */
    private void setMaxTimeLimitAndAssignCook(OrderItem orderItem, String restaurantCode) {
        MenuItem menuItem = orderItem.getMenuItem();

        if (menuItem == null || menuItem.getCookSet() == null || menuItem.getCookSet().isEmpty()) {
            orderItem.setMaxTimeLimitToStart(LocalDateTime.now());
            orderItem.setOrderItemStatus(OrderItemStatus.ASSIGNED);
            return;
        }

        User mostAvailableCook = null;
        long shortestWaitTime = Long.MAX_VALUE;

        for (User cook : menuItem.getCookSet()) {
            long waitTime = calculateCookAvailabilityTime(cook, restaurantCode);
            if (waitTime < shortestWaitTime) {
                shortestWaitTime = waitTime;
                mostAvailableCook = cook;
            }
        }

        if (mostAvailableCook != null) {
            orderItem.setCook(mostAvailableCook);
            orderItem.setMaxTimeLimitToStart(LocalDateTime.now().plusMinutes(shortestWaitTime));
            orderItem.setOrderItemStatus(OrderItemStatus.ASSIGNED);
        }
    }

    /**
     * Calculate and set MaxTimeLimitToStart for a combo item based on cook's availability
     * Also assigns each combo item to the most quickly available cook
     */
    private List<OrderItem> setMaxTimeLimitForCombo(OrderItem parent, String restaurantCode) {
        List<OrderItem> subItems = new ArrayList<>();
        for (ComboItem comboItem : parent.getCombo().getComboItemSet()) {
            OrderItem subItem = new OrderItem();
            subItem.setOrder(parent.getOrder());
            subItem.setParentItem(parent);
            subItem.setMenuItem(comboItem.getMenuItem());
            setMaxTimeLimitAndAssignCook(subItem, restaurantCode);
            subItems.add(subItem);
        }
        return subItems;
    }

    /**
     * Calculate when a cook will be available based on their current workload
     */
    private long calculateCookAvailabilityTime(User cook, String restaurantCode) {
        List<OrderItem> pendingOrderItems = orderItemRepository.findAssignedAndStartedItemsByRestaurantCodeAndCook(restaurantCode, cook.getId());
        return pendingOrderItems.stream()
            .mapToInt(orderItem -> {
                int itemPrepTime = 0;
                int comboPrepTime = 0;

                MenuItem menuItem = orderItem.getMenuItem();
                if (menuItem != null && menuItem.getPreparationTime() != null) {
                    itemPrepTime = menuItem.getPreparationTime();
                }

                Combo combo = orderItem.getCombo();
                if (combo != null && combo.getComboItemSet() != null) {
                    comboPrepTime = combo.getComboItemSet().stream()
                            .map(ComboItem::getMenuItem)
                            .filter(Objects::nonNull)
                            .mapToInt(mi -> mi.getPreparationTime() != null ? mi.getPreparationTime() : 0)
                            .sum();
                }

                return itemPrepTime + comboPrepTime;
            })
            .sum();
    }

    private void publishToOrderCreatedEventListener(List<OrderItem> orderItems) {
        OrderCreatedEvent event = new OrderCreatedEvent(this, orderItems);
        eventPublisher.publishEvent(event);
    }
}


// Auto assign
// Predict the time without assigning

// cook's start time(bal time) + assigned food item prep time + unassigned food items prep time(for buffer) -> order item serve time

// Show food to all responsible cooks


// Batch config