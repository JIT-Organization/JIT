package com.justintime.jit.controller;

import com.justintime.jit.dto.ApiResponse;
import com.justintime.jit.dto.DashboardDTO;
import com.justintime.jit.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jit-api/dashboard")
public class DashboardController extends BaseController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{restaurantCode}")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboardData(@PathVariable String restaurantCode) {
        DashboardDTO dashboardData = dashboardService.getDashboardData(restaurantCode);
        return success(dashboardData, "Dashboard data fetched successfully");
    }
}
