package com.justintime.jit.service.impl;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.Enums.OrderStatus;
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
    public ResponseEntity<String> createOrder(String restaurantCode, String username, OrderDTO orderDTO) {
        GenericMapper<Order, OrderDTO> mapper = MapperFactory.getMapper(Order.class, OrderDTO.class);
        Order order = mapper.toEntity(orderDTO);
        order.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode));
        order.setUser(userRepository.findByRestaurantCodeAndUsername(restaurantCode, username));
        order.setStatus(OrderStatus.PENDING);
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
        existingOrder.setRestaurant(restaurantRepository.findByRestaurantCode(restaurantCode));
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
                                    .orderItemStatus(orderItem.getOrderItemStatus().name())
                                    .itemName(Objects.nonNull(orderItem.getMenuItem()) ?
                                            orderItem.getMenuItem().getMenuItemName() : orderItem.getCombo().getComboName())
                                    .isCombo(Objects.nonNull(orderItem.getCombo()))
                                    .quantity(orderItem.getQuantity())
                                    .totalPrice(orderItem.getTotalPrice())
                                    .build()
                )
                .toList());
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

    private void saveEachOrderItem(OrderDTO orderDTO, String resCode, Order savedOrder) {
        List<OrderItemDTO> orderItemDTOList = orderDTO.getOrderItems();

        Set<String> comboItemNames = new HashSet<>();
        Set<String> menuItemNames = new HashSet<>();

        for (OrderItemDTO dto : orderItemDTOList) {
            if (Boolean.TRUE.equals(dto.getIsCombo())) {
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

            if (Boolean.TRUE.equals(dto.getIsCombo())) {
                orderItem.setCombo(comboMap.get(dto.getItemName()));
            } else {
                orderItem.setMenuItem(menuItemMap.get(dto.getItemName()));
            }

            orderItem.setOrder(savedOrder);
        }
    }
}
