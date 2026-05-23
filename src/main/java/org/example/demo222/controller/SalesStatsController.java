package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.response.SalesRankingVO;
import org.example.demo222.dto.response.SalesStatItem;
import org.example.demo222.dto.response.SalesSummaryVO;
import org.example.demo222.service.SalesStatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "销售统计", description = "销售数据的日/月/年统计及排行榜")
@RestController
@RequestMapping("/api/sales-stats")
@SecurityRequirement(name = "Bearer")
public class SalesStatsController {

    private final SalesStatsService salesStatsService;

    public SalesStatsController(SalesStatsService salesStatsService) {
        this.salesStatsService = salesStatsService;
    }

    @Operation(summary = "销售概况", description = "获取今日/本月/本年的销售汇总")
    @GetMapping("/summary")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesSummaryVO> summary() {
        return Result.success(salesStatsService.getSummary());
    }

    @Operation(summary = "每日统计", description = "获取指定月份每日的销售金额和单数")
    @GetMapping("/daily")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesStatItem>> dailyStats(
            @RequestParam int year,
            @RequestParam int month) {
        return Result.success(salesStatsService.getDailyStats(year, month));
    }

    @Operation(summary = "月度统计", description = "获取指定年份每月的销售金额和单数")
    @GetMapping("/monthly")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesStatItem>> monthlyStats(@RequestParam int year) {
        return Result.success(salesStatsService.getMonthlyStats(year));
    }

    @Operation(summary = "年度统计", description = "获取每年的销售金额和单数")
    @GetMapping("/yearly")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesStatItem>> yearlyStats() {
        return Result.success(salesStatsService.getYearlyStats());
    }

    @Operation(summary = "产品销售排行", description = "获取指定日期范围内销售额最高的产品排行")
    @GetMapping("/ranking")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesRankingVO>> ranking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(salesStatsService.getProductRanking(startDate, endDate, limit));
    }
}