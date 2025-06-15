package com.justintime.jit.entity.EmbeddableClasses;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
public class BatchOrderItemId implements Serializable {

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "order_item_id")
    private Long orderItemId;

    public BatchOrderItemId() {}

    public BatchOrderItemId(Long batchId, Long orderItemId) {
        this.batchId = batchId;
        this.orderItemId = orderItemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchOrderItemId that)) return false;
        return Objects.equals(batchId, that.batchId) &&
                Objects.equals(orderItemId, that.orderItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchId, orderItemId);
    }
}

