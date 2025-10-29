package com.justintime.jit.service.impl;

import com.justintime.jit.dto.BatchConfigDTO;
import com.justintime.jit.dto.BatchDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.BatchStatus;
import com.justintime.jit.entity.Enums.FoodType;
import com.justintime.jit.entity.Enums.OrderType;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.exception.IllegalBatchStateException;
import com.justintime.jit.exception.ResourceNotFoundException;
import com.justintime.jit.repository.*;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.service.BatchService;
import com.justintime.jit.util.constants.JITConstants;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl extends BaseServiceImpl<Batch, Long> implements BatchService {
    
    private static class ScoredOrderItem {
        OrderItem oi;
        @Getter
        double score;
        
        public ScoredOrderItem(OrderItem oi, double score) {
            this.oi = oi;
            this.score = score;
        }

    }

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private BatchConfigRepository batchConfigRepository;

    @Autowired
    private BatchOrderItemRepository batchOrderItemRepository;
    
    @Autowired
    private RestaurantRoleRepository restaurantRoleRepository;

    private static final int BUFFER_TIME_MINUTES = 5;

    private LocalDateTime computeMaxTimeLimitToStart(LocalDateTime orderDate, int prepTime) {
        return orderDate.minusMinutes(prepTime);
    }

    private int computeAvgStartTime(int assignedBatches, int unassignedBatches, int prepTime, int cookCount) {
        int assignedTime = assignedBatches * prepTime;
        int unassignedTime;
        
        if (cookCount > 0) {
            unassignedTime = (unassignedBatches * prepTime) / cookCount;
        } else {
            unassignedTime = JITConstants.INT_MAX_VAL; // No cooks available
        }
        
        return assignedTime + unassignedTime;
    }

    private boolean canStartOrder(LocalDateTime orderDate, int prepTime, int assignedBatches, 
                                int unassignedBatches, int cookCount, LocalDateTime currentTime) {
        LocalDateTime maxStartTime = computeMaxTimeLimitToStart(orderDate, prepTime);
        int avgStartTime = computeAvgStartTime(assignedBatches, unassignedBatches, prepTime, cookCount);

        // Current time + avg workload gives the estimated start time
        LocalDateTime estimatedStartTime = currentTime.plusMinutes(avgStartTime);

        LocalDateTime earliestAllowedStart = maxStartTime.minusMinutes(BUFFER_TIME_MINUTES);

        return !estimatedStartTime.isBefore(earliestAllowedStart) && 
               !estimatedStartTime.isAfter(maxStartTime);
    }

    private void handleOrderAssignment(OrderItem orderItem, BatchConfig batchConfig, 
                                     int assignedBatches, int unassignedBatches, 
                                     int cookCount, LocalDateTime currentTime) {
        int prepTime = batchConfig.getPreparationTime() != null ? batchConfig.getPreparationTime() : 30; // default 30 minutes

        if (canStartOrder(orderItem.getOrder().getOrderDate(), prepTime, 
                         assignedBatches, unassignedBatches, cookCount, currentTime)) {
            if (unassignedBatches > 0) {
                // Find an existing unassigned batch and assign to it
                Batch unassignedBatch = batchRepository.findByCookIdAndStatusAndBatchConfigNumbersAndRestaurantId(
                    orderItem.getMenuItem().getBatchConfig().getCooks().iterator().next().getId(),
                    "UNASSIGNED",
                    List.of(batchConfig.getBatchConfigNumber()),
                    orderItem.getMenuItem().getRestaurant().getId()
                ).stream().findFirst().orElse(null);

                if (unassignedBatch != null) {
                    BatchOrderItem batchOrderItem = new BatchOrderItem();
                    batchOrderItem.setBatch(unassignedBatch);
                    batchOrderItem.setOrderItem(orderItem);
                    batchOrderItemRepository.save(batchOrderItem);
                }
            } else {
                // Create new batch and assign
                Batch newBatch = new Batch();
                newBatch.setBatchConfig(batchConfig);
                newBatch.setStatus(BatchStatus.UNASSIGNED);
                newBatch.setQuantity(orderItem.getQuantity());
                newBatch = batchRepository.save(newBatch);

                BatchOrderItem batchOrderItem = new BatchOrderItem();
                batchOrderItem.setBatch(newBatch);
                batchOrderItem.setOrderItem(orderItem);
                batchOrderItemRepository.save(batchOrderItem);
            }
        } else {
            // Delay order assignment - just leave it as is
            // The order will be picked up in the next batch assignment cycle
        }
    }

    @Transactional
    public void startAssignedBatch(String batchId) {
        Batch batch = batchRepository.findById(Long.parseLong(batchId))
            .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (!BatchStatus.ASSIGNED.equals(batch.getStatus())) {
            throw new IllegalBatchStateException("Only ASSIGNED batches can be started.");
        }

        BatchConfig batchConfig = batch.getBatchConfig();
        if (batchConfig==null){
            throw new ResourceNotFoundException("No batch configurations found for this batch");
        }
        int maxQty = batchConfig.getMaxCount();

        int targetQty = batch.getQuantity() != 0 ? batch.getQuantity() : maxQty;

        List<OrderItem> eligibleItems = orderItemRepository.findAllPending().stream()
            .filter(oi -> oi.getBatchOrderItems().isEmpty())
            .filter(oi -> batchConfig.equals(oi.getMenuItem().getBatchConfig()))
            .collect(Collectors.toList());

        if (eligibleItems.isEmpty()) return;

        // Normalize values
        Instant now = Instant.now();
        int maxQtyInList = eligibleItems.stream().mapToInt(OrderItem::getQuantity).max().orElse(1);
        Instant earliestTime = eligibleItems.stream()
                .map(oi -> oi.getCreatedDttm().atZone(ZoneId.systemDefault()).toInstant())
                .min(Instant::compareTo)
                .orElse(Instant.now());
        long maxWait = Duration.between(earliestTime, now).toMinutes();

        Map<OrderType, Integer> orderTypePriority = Map.of(
            OrderType.ONLINE_TAKEAWAY, 3,
            OrderType.ONLINE, 2,
            OrderType.DINE_IN, 1
        );

        // Scoring
        List<ScoredOrderItem> scoredList = eligibleItems.stream()
            .map(oi -> {
                double waitScore = Duration.between(
                        oi.getCreatedDttm().atZone(ZoneId.systemDefault()).toInstant(),
                        now
                ).toMinutes() / (double) Math.max(maxWait, 1);
                double qtyScore = 1 - (oi.getQuantity() / (double) maxQtyInList);
                OrderType orderType = oi.getOrder().getOrderType();
                double orderTypeScore = orderTypePriority.getOrDefault(orderType, 0) / 3.0;
                
                double totalScore = (waitScore * 0.40)
                                  + (qtyScore * 0.35)
                                  + (orderTypeScore * 0.25);

                return new ScoredOrderItem(oi, totalScore);
            })
            .sorted(Comparator.comparingDouble(ScoredOrderItem::getScore).reversed())
            .collect(Collectors.toList());

        List<BatchOrderItem> batchOrderItems = new ArrayList<>();
        int totalQty = 0;

        for (ScoredOrderItem scored : scoredList) {
            if (totalQty + scored.oi.getQuantity() > targetQty) continue;
            
            BatchOrderItem batchOrderItem = new BatchOrderItem();
            batchOrderItem.setBatch(batch);
            batchOrderItem.setOrderItem(scored.oi);
            batchOrderItems.add(batchOrderItem);
            totalQty += scored.oi.getQuantity();
        }

        batchOrderItemRepository.saveAll(batchOrderItems);
        batch.setStatus(BatchStatus.STARTED);
        batchRepository.save(batch);
    }

    public List<BatchDTO> getBatchesForCook(String restaurantCode, String cookName) {
        // First get the restaurant to validate and get its ID
        Restaurant restaurant = restaurantRepository.findByRestaurantCode(restaurantCode)
            .orElseThrow(() -> new RuntimeException("Restaurant not found with code: " + restaurantCode));
        
        // Get the COOK role for this restaurant by roleType
        RestaurantRole cookRole = restaurantRoleRepository.findFirstByRoleTypeAndRestaurantCode(com.justintime.jit.entity.Enums.Role.COOK, restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("COOK role not found for restaurant: " + restaurantCode));
            
        // Get cook for this restaurant
        User cook = userRepository.findByRestaurantIdAndRoleAndUserName(restaurant.getId(), cookRole.getId(), cookName)
;
        if (null==cook){
            throw new ResourceNotFoundException("Cook not found for restaurant " + restaurantCode);
        }
        // Get all batch configs for this cook
        Set<BatchConfig> cookBatchConfigs = cook.getBatchConfigs();
        List<String> batchConfigNumbers = cookBatchConfigs.stream()
            .map(BatchConfig::getBatchConfigNumber)
            .collect(Collectors.toList());
            
        GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
        GenericMapper<BatchConfig, BatchConfigDTO> batchConfigMapper = MapperFactory.getMapper(BatchConfig.class, BatchConfigDTO.class);
        
        List<BatchDTO> result = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();

        // 3. Started Batches — LOCKED
        List<Batch> startedBatches = batchRepository.findByCookIdAndStatusAndBatchConfigNumbersAndRestaurantId(
            cook.getId(), "STARTED", batchConfigNumbers, restaurant.getId());
        for (Batch started : startedBatches) {
            List<OrderItem> items = orderItemRepository.findByBatchId(started.getId());
            List<OrderItemDTO> itemDTOs = items.stream()
                    .map(item -> mapOrderItemToDTO(item, orderItemMapper))
                    .collect(Collectors.toList());
                    
            BatchDTO batchDTO = new BatchDTO(
                batchConfigMapper.toDto(started.getBatchConfig()),
                itemDTOs,
                "STARTED",
                started.getBatchNumber(),
                started.getQuantity()
            );
            result.add(batchDTO);
        }

        // 2. Assigned Batches — Editable count
        List<Batch> assignedBatches = batchRepository.findByCookIdAndStatusAndBatchConfigNumbersAndRestaurantId(
            cook.getId(), "ASSIGNED", batchConfigNumbers, restaurant.getId());
        for (Batch assigned : assignedBatches) {
            BatchDTO batchDTO = new BatchDTO(
                batchConfigMapper.toDto(assigned.getBatchConfig()),
                new ArrayList<>(),
                "ASSIGNED",
                assigned.getBatchNumber(),
                assigned.getQuantity()
            );
            result.add(batchDTO);
        }

        // 1. Unassigned Batches — Derived from unassigned orderItems
        List<OrderItem> unassignedItems = orderItemRepository.findUnassignedOrderItemsByBatchConfigAndStatusAndRestaurantId(
            assignedBatches.stream()
                .map(Batch::getBatchConfig)
                .collect(Collectors.toList()),
            "UNASSIGNED",
            cook.getId(),
            currentTime,
            restaurant.getId()
        );

        // Group by BatchConfig
        Map<BatchConfig, List<OrderItem>> unassignedGrouped = unassignedItems.stream()
            .collect(Collectors.groupingBy(oi -> oi.getMenuItem().getBatchConfig()));

        for (Map.Entry<BatchConfig, List<OrderItem>> entry : unassignedGrouped.entrySet()) {
            BatchConfig batchConfig = entry.getKey();
            
            // Skip if batch config doesn't belong to this restaurant or cook
            if (!batchConfig.getRestaurant().getId().equals(restaurant.getId()) || 
                !cookBatchConfigs.contains(batchConfig)) {
                continue;
            }
            int maxCount = batchConfig.getMaxCount();

            List<OrderItemDTO> itemDTOs = entry.getValue().stream()
                    .map(item -> mapOrderItemToDTO(item, orderItemMapper))
                    .collect(Collectors.toList());

            int unassignedQty = entry.getValue().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            // Calculate available quantity based on time constraints
            Long assignedBatchesCount = orderItemRepository.countAssignedBatchesForCookAndRestaurant(
                batchConfig, "ASSIGNED", cook.getId(), restaurant.getId());
            Long unassignedBatchesCount = batchConfigRepository.countUnassignedBatchesForBatchConfigAndRestaurant(
                batchConfig, "UNASSIGNED", restaurant.getId());
            int cookCount = batchConfig.getCooks().size();

            // Process each order item for assignment
            for (OrderItem orderItem : entry.getValue()) {
                handleOrderAssignment(orderItem, batchConfig, 
                    assignedBatchesCount.intValue(), 
                    unassignedBatchesCount.intValue(), 
                    cookCount, currentTime);
            }

            // Form virtual batches for display
            int count = unassignedQty;
            while (count > 0) {
                int batchSize = Math.min(maxCount, count);
                BatchDTO batchDTO = new BatchDTO(
                    batchConfigMapper.toDto(batchConfig),
                    itemDTOs,
                    "UNASSIGNED",
                    null,
                    batchSize
                );
                result.add(batchDTO);
                count -= batchSize;
            }
        }

        return result;
    }

    private OrderItemDTO mapOrderItemToDTO(OrderItem orderItem, GenericMapper<OrderItem, OrderItemDTO> mapper) {
        OrderItemDTO dto = mapper.toDto(orderItem);
        dto.setItemName(orderItem.getMenuItem() != null ? 
                orderItem.getMenuItem().getMenuItemName() : 
                orderItem.getCombo().getComboName());
        dto.setFoodType(orderItem.getMenuItem() == null ? FoodType.COMBO.toString() : orderItem.getMenuItem().getFoodType().toString());
        return dto;
    }

    public List<BatchDTO> getBatchesByRestaurantCodeAndCookName(String restaurantCode, String cookName) {
        Optional<Restaurant> restaurant = restaurantRepository.findByRestaurantCode(restaurantCode);
        
        // Get the COOK role for this restaurant by roleType
        RestaurantRole cookRole = restaurantRoleRepository.findFirstByRoleTypeAndRestaurantCode(com.justintime.jit.entity.Enums.Role.COOK, restaurantCode)
                .orElseThrow(() -> new ResourceNotFoundException("COOK role not found for restaurant: " + restaurantCode));
        
        User cook = userRepository.findByRestaurantIdAndRoleAndUserName(restaurant.orElseThrow().getId(), cookRole.getId(), cookName);
        if (null==cook){
            throw new ResourceNotFoundException("Cook not found for restaurant " + restaurantCode);
        }
        Set<MenuItem> menuItems = userRepository.findMenuItemsByCookId(cook.getId());
        
        GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
        GenericMapper<BatchConfig, BatchConfigDTO> batchConfigMapper = MapperFactory.getMapper(BatchConfig.class, BatchConfigDTO.class);
        
        List<BatchDTO> batchDTOs = new ArrayList<>();
        
        for (MenuItem menuItem : menuItems) {
            Set<Batch> batches = findByMenuItemIdAndRestaurantId(menuItem.getId(), restaurant.orElseThrow().getId());
            batches.forEach(batch -> {
                BatchDTO batchDTO = new BatchDTO();
                
                // Map BatchConfig to BatchConfigDTO
                BatchConfig batchConfig = batch.getBatchConfig();
                BatchConfigDTO batchConfigDTO = batchConfigMapper.toDto(batchConfig);
                
                // Set menu item names
                batchConfigDTO.setMenuItemNames(batchConfig.getMenuItems().stream()
                        .map(MenuItem::getMenuItemName)
                        .collect(Collectors.toList()));

                
                batchDTO.setBatchConfigDTO(batchConfigDTO);
                batchDTO.setStatus(batch.getStatus().toString());
                
                List<OrderItemDTO> orderItemDTOs = batch.getBatchOrderItems().stream()
                        .map(batchOrderItem -> {
                            OrderItem orderItem = batchOrderItem.getOrderItem();
                            OrderItemDTO dto = orderItemMapper.toDto(orderItem);
                            dto.setItemName(orderItem.getMenuItem() != null ? 
                                    orderItem.getMenuItem().getMenuItemName() : 
                                    orderItem.getCombo().getComboName());
                            dto.setFoodType(orderItem.getMenuItem() == null ? FoodType.COMBO.toString() : orderItem.getMenuItem().getFoodType().toString());
                            return dto;
                        })
                        .collect(Collectors.toList());
                batchDTO.setOrderItemsDTO(orderItemDTOs);
                
                batchDTOs.add(batchDTO);
            });
        }
        return batchDTOs;
    }

    public Set<Batch> findByMenuItemIdAndRestaurantId(Long menuItemId, Long restaurantId) {
        Set<Batch> unassignedBatches = batchRepository.findByMenuItemIdAndRestaurantIdAndStatus(menuItemId, restaurantId, BatchStatus.UNASSIGNED);
        Set<Batch> assignedBatches = batchRepository.findByMenuItemIdAndRestaurantIdAndStatus(menuItemId, restaurantId, BatchStatus.ASSIGNED);
        Set<Batch> startedBatches = batchRepository.findByMenuItemIdAndRestaurantIdAndStatus(menuItemId, restaurantId, BatchStatus.STARTED);
        Set<Batch> allBatches = new HashSet<>();
        allBatches.addAll(unassignedBatches);
        allBatches.addAll(assignedBatches);
        allBatches.addAll(startedBatches);
        return allBatches;
    }
}
