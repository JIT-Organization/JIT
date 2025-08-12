package com.justintime.jit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardDTO {
    private long totalItems;
    private long totalMenuItems;
    private long totalCombos;
    private long totalOrders;
    private long totalCustomers;
    private long totalCooks;
    private long totalServers;
    private long totalCategories;
    private long totalAddOns;
    private long totalDiningTables;
    private double totalRevenue;
}
