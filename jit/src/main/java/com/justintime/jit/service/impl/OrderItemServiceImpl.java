package com.justintime.jit.service.impl;

import com.justintime.jit.dto.*;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.ComboEntities.Combo;
import com.justintime.jit.entity.ComboEntities.ComboItem;
import com.justintime.jit.entity.Enums.ConfigurationName;
import com.justintime.jit.entity.Enums.OrderItemStatus;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.ComboRepo.ComboRepository;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.BusinessConfigurationService;
import com.justintime.jit.service.NotificationService;
import com.justintime.jit.service.OrderItemService;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.justintime.jit.util.constants.JITConstants.COMBO;

@Service
// TODO Refactor
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItem,Long> implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

   @Autowired
   private CommonServiceImplUtil commonServiceImplUtil;

   @Autowired
   private NotificationService notificationService;

   @Autowired
   private ComboRepository comboRepository;

   @Autowired
   private BusinessConfigurationService businessConfigurationService;

   private final GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);

   private final GenericMapper<AddOn, AddOnDTO> addOnMapper = MapperFactory.getMapper(AddOn.class, AddOnDTO.class);

    public List<OrderItemDTO> getAllOrderItems() {
        return new ArrayList<>();
    }

//    public OrderItemDTO getOrderItemById(Long id) {
//        return orderItemRepository.findById(id).orElse(null);
//    }

     public List<OrderItemDTO> getOrderItemsByRestaurantCodeAndOrderNumber(String restaurantCode,String orderNumber){
        List<OrderItem>  orderItems=orderItemRepository.findByRestaurantCodeAndOrderNumber(restaurantCode, orderNumber);
        GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for(OrderItem orderItem : orderItems) {
            orderItemDTOs.add(mapToDTO(orderItem, orderItemMapper));
        }
        return orderItemDTOs;
     }

        public OrderItemDTO patchOrderItem(String restaurantCode, PatchRequest<OrderItemDTO> payload) {
            OrderItemDTO orderItemDTO= payload.getDto();
            OrderItem existingItem = orderItemRepository.findByRestaurantCodeAndItemNameAndOrderNumber(restaurantCode, orderItemDTO.getItemName(), orderItemDTO.getOrderNumber());
            GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
            OrderItem patchedItem = orderItemMapper.toEntity(orderItemDTO);
          //resolveRelationships(patchedItem, menuItemDTO, propertiesToBeUpdated, true);
            commonServiceImplUtil.copySelectedProperties(patchedItem, existingItem, payload.getPropertiesToBeUpdated());
            existingItem.setUpdatedDttm(LocalDateTime.now());
            OrderItem orderItem = orderItemRepository.save(existingItem);
            if(payload.getPropertiesToBeUpdated().contains("status")) notificationService.notifyOrderItemStatusUpdate(orderItem);
            return mapToDTO(orderItem, orderItemMapper);
    }

     public OrderItemDTO updateOrderItem(String restaurantCode, OrderItemDTO orderItemDTO){
         OrderItem existingItem = orderItemRepository.findByRestaurantCodeAndItemNameAndOrderNumber(restaurantCode,orderItemDTO.getItemName(), orderItemDTO.getOrderNumber());
         GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
         OrderItem patchedOrderItem = orderItemMapper.toEntity(orderItemDTO);
         MenuItem menuItem= menuItemRepository.findByRestaurantCodeAndMenuItemName(restaurantCode,orderItemDTO.getItemName());
         patchedOrderItem.setMenuItem(menuItem);
         if(orderItemDTO.getOrderNumber().isEmpty()) {
             Order order=orderRepository.findByOrderNumber(orderItemDTO.getOrderNumber())
                     .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderItemDTO.getOrderNumber()));
             patchedOrderItem.setOrder(order);
         }
         //resolveRelationships(patchedItem, menuItemDTO, new HashSet<>(), false);
         BeanUtils.copyProperties(patchedOrderItem, existingItem, "id", "createdDttm");
         existingItem.setUpdatedDttm(LocalDateTime.now());
        orderItemRepository.save(existingItem);
        return  orderItemDTO;
     }

    public void saveOrderItem(String restaurantCode,OrderItemDTO orderItemDTO) {
        String itemName =orderItemDTO.getItemName();
        GenericMapper<OrderItem, OrderItemDTO> menuItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
        OrderItem orderItem = menuItemMapper.toEntity(orderItemDTO);
        MenuItem menuItem= menuItemRepository.findByRestaurantCodeAndMenuItemName(restaurantCode,itemName);
        orderItem.setMenuItem(menuItem);
        orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(String restaurantCode,String orderNumber,String itemName) {
        Long orderId= orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber)).getId();
        Long menuItemId=menuItemRepository.findByRestaurantCodeAndMenuItemName(restaurantCode,itemName).getId();
        orderItemRepository.deleteByOrderIdAndMenuItemId(orderId,menuItemId);
    }

    @Override
    public List<OrderItemDTO> getOrderItemsForCook(Long cookId) {
         List<OrderItem> orderItems=orderItemRepository.findOrderItemsByCookIdAndUnassigned(cookId);
         GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
         List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
         for(OrderItem orderItem : orderItems) {
             orderItemDTOs.add(mapToDTO(orderItem, orderItemMapper));
         }
         return orderItemDTOs;
    }

    private OrderItemDTO mapToDTO(OrderItem orderItem, GenericMapper<OrderItem, OrderItemDTO> mapper) {
        return mapper.toDto(orderItem);
    }

    @Override
    public List<OrderItemDTO> getOrderItemsForCookByNameAndRestaurant(String cookName, String restaurantCode) {
        Long restaurantId = restaurantRepository.findByRestaurantCode(restaurantCode)
                .map(BaseEntity::getId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with code: " + restaurantCode));

        User cook = userRepository.findByRestaurantIdAndRoleAndUserName(restaurantId, Role.COOK,cookName);
        if (null==cook){
            throw new ResourceNotFoundException("Cook not found for restaurant " + restaurantCode);
        }
        return getOrderItemsForCook(cook.getId());
    }

    @Override
    public List<OrderItem> createAndPersistOrderItems(OrderDTO orderDTO, String restaurantCode, Order savedOrder) {
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
            dto.setOrderItemStatus(OrderItemStatus.UNASSIGNED); // Set the initial status as unassigned
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
        boolean isCookAssignmentManual = StringUtils.equals(businessConfigurationService.getConfigValue(savedOrder.getRestaurant().getRestaurantCode(), ConfigurationName.MANUAL_COOK_ASSIGNMENT), "Y");
        for (OrderItemDTO dto : orderItemDTOList) {
            OrderItem item = orderItemMapper.toEntity(dto);
            item.setOrder(savedOrder);
            if (COMBO.equalsIgnoreCase(dto.getFoodType())) {
                Combo combo = comboMap.get(dto.getItemName());
//                if (combo == null) throw new IllegalArgumentException("Invalid combo: " + dto.getItemName());
                item.setCombo(combo);
                List<OrderItem> subItems = getOrderItemForEachMenuItemInCombo(item);
                subItems.forEach(subItem -> {
                    if(!isCookAssignmentManual) setMaxTimeLimitAndAssignCook(subItem, restaurantCode);
                });
                allOrderItems.addAll(subItems);
            } else {
                MenuItem menuItem = menuItemMap.get(dto.getItemName());
//                if (menuItem == null) throw new IllegalArgumentException("Invalid menu item: " + dto.getItemName());
                item.setMenuItem(menuItem);
                if(!isCookAssignmentManual) setMaxTimeLimitAndAssignCook(item, restaurantCode);
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

        assignCook(orderItem, mostAvailableCook, shortestWaitTime);
    }

    private void assignCook(OrderItem orderItem, User mostAvailableCook, long shortestWaitTime) {
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
    private List<OrderItem> getOrderItemForEachMenuItemInCombo(OrderItem parent) {
        List<OrderItem> subItems = new ArrayList<>();
        for (ComboItem comboItem : parent.getCombo().getComboItemSet()) {
            OrderItem subItem = new OrderItem();
            subItem.setOrder(parent.getOrder());
            subItem.setParentItem(parent);
            subItem.setMenuItem(comboItem.getMenuItem());
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
}
