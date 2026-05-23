package org.example.demo222.controller;

import org.example.demo222.common.Result;
import org.example.demo222.dto.response.*;
import org.example.demo222.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public Result<DashboardOverviewVO> overview() {
        return Result.success(dashboardService.getOverview());
    }

    @GetMapping("/sales-trend")
    public Result<SalesTrendVO> salesTrend() {
        return Result.success(dashboardService.getSalesTrend());
    }

    @GetMapping("/category-sales")
    public Result<List<CategorySalesVO>> categorySales() {
        return Result.success(dashboardService.getCategorySales());
    }

    @GetMapping("/warnings")
    public Result<List<InventoryVO>> warnings() {
        return Result.success(dashboardService.getDashboardWarnings());
    }
}