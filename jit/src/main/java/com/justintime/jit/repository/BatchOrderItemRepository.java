package com.justintime.jit.repository;

import com.justintime.jit.entity.BatchOrderItem;
import com.justintime.jit.entity.EmbeddableClasses.BatchOrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchOrderItemRepository extends JpaRepository<BatchOrderItem, BatchOrderItemId> {
} 