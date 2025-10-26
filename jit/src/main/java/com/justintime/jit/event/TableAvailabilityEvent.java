package com.justintime.jit.event;

import com.justintime.jit.entity.DiningTable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class TableAvailabilityEvent extends ApplicationEvent {
    private final List<DiningTable> diningTables;
    public TableAvailabilityEvent(Object source, List<DiningTable> diningTables) {
        super(source);
        this.diningTables = diningTables;
    }
}
