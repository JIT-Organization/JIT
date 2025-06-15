//package com.justintime.jit.repository;
//
//import com.justintime.jit.entity.BatchOrderItem;
//import com.justintime.jit.entity.EmbeddableClasses.BatchOrderItemId;
//import com.justintime.jit.entity.OrderEntities.OrderItem;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface BatchOrderItemRepository extends JpaRepository<BatchOrderItem, BatchOrderItemId> {
//
//    @Query(value = "Select b from batch_order_item b join orderItem oi on b.order_item_id=oi.id  where b.id = :batchId",nativeQuery = true)
//    List<OrderItem> findOrderItemsByBatchId(@Param("batchId") Long batchId);
//
//}