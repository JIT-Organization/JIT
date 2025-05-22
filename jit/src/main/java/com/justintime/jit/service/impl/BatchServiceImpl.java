package com.justintime.jit.service.impl;

import com.justintime.jit.dto.BatchConfigDTO;
import com.justintime.jit.dto.BatchDTO;
import com.justintime.jit.dto.OrderItemDTO;
import com.justintime.jit.entity.*;
import com.justintime.jit.entity.Enums.BatchStatus;
import com.justintime.jit.entity.OrderEntities.OrderItem;
import com.justintime.jit.repository.BatchOrderItemRepository;
import com.justintime.jit.repository.BatchRepository;
import com.justintime.jit.repository.CookRepository;
import com.justintime.jit.repository.OrderRepo.OrderItemRepository;
import com.justintime.jit.repository.RestaurantRepository;
import com.justintime.jit.service.BatchService;
import com.justintime.jit.util.mapper.GenericMapper;
import com.justintime.jit.util.mapper.MapperFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BatchServiceImpl extends BaseServiceImpl<Batch, Long> implements BatchService {
    
    private static class ScoredOrderItem {
        OrderItem oi;
        double score;
        
        public ScoredOrderItem(OrderItem oi, double score) {
            this.oi = oi;
            this.score = score;
        }
        
        public double getScore() { 
            return score; 
        }
    }

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CookRepository cookRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private BatchOrderItemRepository batchOrderItemRepository;

    @Transactional
    public void startAssignedBatch(String batchId) {
        Batch batch = batchRepository.findById(Long.parseLong(batchId))
            .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (!BatchStatus.ACCEPTED.equals(batch.getStatus())) {
            throw new IllegalStateException("Only ACCEPTED batches can be started.");
        }

        BatchConfig batchConfig = batch.getBatchConfig();
        int maxQty = batchConfig != null && batchConfig.getMaxCount() != null
            ? Integer.parseInt(batchConfig.getMaxCount())
            : Integer.MAX_VALUE;

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
            .map(oi -> oi.getCreatedDttm().toInstant())
            .min(Instant::compareTo)
            .get();
        long maxWait = Duration.between(earliestTime, now).toMinutes();
        
        // Calculate MAX_BOOKING_WINDOW based on preparation time and unaccepted batches
        long preparationTime = batch.getBatchConfig().getPreparationTime() != null ? 
            Long.parseLong(batch.getBatchConfig().getPreparationTime()) : 30; // default 30 minutes if not set
        long unacceptedBatchesCount = batchRepository.countByBatchConfigAndStatus(
            batch.getBatchConfig(), 
            BatchStatus.NEW
        );
        final long MAX_BOOKING_WINDOW = preparationTime * unacceptedBatchesCount;

        Map<String, Integer> orderTypePriority = Map.of(
            "ONLINE_TAKEAWAY", 3,
            "ONLINE", 2,
            "DINE_IN", 1
        );

        // Scoring
        List<ScoredOrderItem> scoredList = eligibleItems.stream()
            .map(oi -> {
                double waitScore = Duration.between(oi.getCreatedDttm().toInstant(), now).toMinutes() / (double) Math.max(maxWait, 1);
                double qtyScore = 1 - (oi.getQuantity() / (double) maxQtyInList);
                double orderTypeScore = orderTypePriority.getOrDefault(oi.getOrder().getOrderType().toString().toUpperCase(), 0) / 3.0;
                
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

    public List<BatchDTO> getBatchesForCook(String cookName) {
        List<OrderItem> allPending = orderItemRepository.findAllPending();
        GenericMapper<OrderItem, OrderItemDTO> orderItemMapper = MapperFactory.getMapper(OrderItem.class, OrderItemDTO.class);
        GenericMapper<BatchConfig, BatchConfigDTO> batchConfigMapper = MapperFactory.getMapper(BatchConfig.class, BatchConfigDTO.class);
        
        List<BatchDTO> result = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();

        // 3. Started Batches — LOCKED
        List<Batch> startedBatches = batchRepository.findByCookNameAndStatusAndBatchConfigNumber(cookName, "STARTED", null);
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
        List<Batch> assignedBatches = batchRepository.findByCookNameAndStatusAndBatchConfigNumber(cookName, "ACCEPTED", null);
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
        List<OrderItem> unassignedItems = orderItemRepository.findUnassignedOrderItemsByBatchConfigAndStatus(
            assignedBatches.stream()
                .map(Batch::getBatchConfig)
                .collect(Collectors.toList()),
            "ACCEPTED",
            cookName,
            currentTime
        );

        // Group by BatchConfig
        Map<BatchConfig, List<OrderItem>> unassignedGrouped = unassignedItems.stream()
            .collect(Collectors.groupingBy(oi -> oi.getMenuItem().getBatchConfig()));

        for (Map.Entry<BatchConfig, List<OrderItem>> entry : unassignedGrouped.entrySet()) {
            BatchConfig batchConfig = entry.getKey();
            
            int maxCount = batchConfig != null && batchConfig.getMaxCount() != null
                    ? Integer.parseInt(batchConfig.getMaxCount())
                    : Integer.MAX_VALUE;

            List<OrderItemDTO> itemDTOs = entry.getValue().stream()
                    .map(item -> mapOrderItemToDTO(item, orderItemMapper))
                    .collect(Collectors.toList());

            int unassignedQty = entry.getValue().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            // Calculate available quantity based on time constraints
            Long assignedBatchesCount = orderItemRepository.countAssignedBatchesForCook(batchConfig, "ACCEPTED", cookName);
            Long unassignedBatchesCount = orderItemRepository.countUnassignedBatchesForBatchConfig(batchConfig, "ACCEPTED");
            int cookCount = batchConfig.getCooks().size();
            
            int avgStartTime = (assignedBatchesCount.intValue() * batchConfig.getPreparationTime()) + 
                             (unassignedBatchesCount.intValue() * batchConfig.getPreparationTime() / cookCount);
            
            int availableQty = unassignedQty;
            for (OrderItem oi : entry.getValue()) {
                if (oi.getMaxTimeLimitToStart() != null && 
                    oi.getMaxTimeLimitToStart().isBefore(currentTime.plusMinutes(avgStartTime))) {
                    availableQty -= oi.getQuantity();
                }
            }

            if (availableQty <= 0) continue;

            // Form virtual batches
            int count = availableQty;
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
        dto.setIsCombo(orderItem.getMenuItem() == null);
        return dto;
    }

    public List<BatchDTO> getBatchesByRestaurantCodeAndCookName(String restaurantCode, String cookName) {
        Optional<Restaurant> restaurant = restaurantRepository.findByRestaurantCode(restaurantCode);
        Optional<Cook> cook = cookRepository.findByRestaurantIdAndName(restaurant.orElseThrow().getId(), cookName);
        Set<MenuItem> menuItems = cookRepository.findMenuItemsByCookId(cook.orElseThrow().getId());
        
        GenericMapper<Batch, BatchDTO> batchMapper = MapperFactory.getMapper(Batch.class, BatchDTO.class);
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
                
                // Set batch numbers
                batchConfigDTO.setBatchNumbers(batchConfig.getBatches().stream()
                        .map(Batch::getBatchNumber)
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
                            dto.setIsCombo(orderItem.getMenuItem() == null);
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
        Set<Batch> unassignedBatches = batchRepository.findByMenuItemIdAndRestaurantIdAndStatus(menuItemId, restaurantId, BatchStatus.NEW);
        Set<Batch> assignedBatches = batchRepository.findByMenuItemIdAndRestaurantIdAndStatus(menuItemId, restaurantId, BatchStatus.ACCEPTED);
        Set<Batch> startedBatches = batchRepository.findByMenuItemIdAndRestaurantIdAndStatus(menuItemId, restaurantId, BatchStatus.STARTED);
        Set<Batch> allBatches = new HashSet<>();
        allBatches.addAll(unassignedBatches);
        allBatches.addAll(assignedBatches);
        allBatches.addAll(startedBatches);
        return allBatches;
    }
}
