package com.justintime.jit.service.impl;

import com.justintime.jit.dto.MenuItemDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.dto.PatchRequest;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.Role;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.MenuItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.repository.UserRepository;
import com.justintime.jit.service.NotificationService;
import com.justintime.jit.service.OrderItemService;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.util.CommonServiceImplUtil;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        public List<OrderItemDTO> getAllOrderItems() {
            return new ArrayList<>();
        }

//        public OrderItemDTO getOrderItemById(Long id) {
//            return orderItemRepository.findById(id).orElse(null);
//        }

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
        OrderItemDTO orderItemDTO = mapper.toDto(orderItem);
        return orderItemDTO;
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
}
