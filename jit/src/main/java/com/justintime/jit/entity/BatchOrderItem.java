//package com.justintime.jit.entity;
//
//import com.justintime.jit.entity.EmbeddableClasses.BatchOrderItemId;
//import com.justintime.jit.entity.OrderEntities.OrderItem;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.envers.Audited;
//
//
//@Getter
//@Setter
//@Entity
//@Audited
//@Table(name = "batch_order_item")
//@NoArgsConstructor
//@AllArgsConstructor
//public class BatchOrderItem {
//    @EmbeddedId
//    private BatchOrderItemId id = new BatchOrderItemId();
//
//    @ManyToOne
//    @MapsId("batchId")
//    @JoinColumn(name = "batch_id")
//    private Batch batch;
//
//    @ManyToOne
//    @MapsId("orderItemId")
//    @JoinColumn(name = "order_item_id")
//    private OrderItem orderItem;
////
////    @Column(name = "allocated_quantity", nullable = false)
////    private int allocatedQuantity;
//}