package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.demo222.common.Result;
import org.example.demo222.dto.response.*;
import org.example.demo222.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "仪表盘", description = "首页仪表盘数据概览")
@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = "Bearer")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "概览数据", description = "获取今日/本月销售、进货、库存预警等汇总数据")
    @GetMapping("/overview")
    public Result<DashboardOverviewVO> overview() {
        return Result.success(dashboardService.getOverview());
    }

    @Operation(summary = "销售趋势", description = "获取近30天每日销售金额趋势")
    @GetMapping("/sales-trend")
    public Result<SalesTrendVO> salesTrend() {
        return Result.success(dashboardService.getSalesTrend());
    }

    @Operation(summary = "分类销售占比", description = "获取本月各分类的销售金额占比")
    @GetMapping("/category-sales")
    public Result<List<CategorySalesVO>> categorySales() {
        return Result.success(dashboardService.getCategorySales());
    }

    @Operation(summary = "预警列表", description = "获取库存预警产品列表")
    @GetMapping("/warnings")
    public Result<List<InventoryVO>> warnings() {
        return Result.success(dashboardService.getDashboardWarnings());
    }
}