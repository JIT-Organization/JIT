package com.justintime.jit.controller;

import com.justintime.jit.dto.DashboardDTO;
import com.justintime.jit.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public DashboardDTO getDashboardData() {
        return dashboardService.getDashboardData();
    }
}
