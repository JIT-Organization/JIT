package com.justintime.jit.service;

import com.justintime.jit.dto.DashboardDTO;

public interface DashboardService{
    DashboardDTO getDashboardData(String restaurantCode);
}
