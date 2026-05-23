package org.example.demo222.controller;

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

/**
 * 销售统计控制器
 */
@RestController
@RequestMapping("/api/sales-stats")
public class SalesStatsController {

    private final SalesStatsService salesStatsService;

    public SalesStatsController(SalesStatsService salesStatsService) {
        this.salesStatsService = salesStatsService;
    }

    @GetMapping("/summary")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesSummaryVO> summary() {
        return Result.success(salesStatsService.getSummary());
    }

    @GetMapping("/daily")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesStatItem>> dailyStats(
            @RequestParam int year,
            @RequestParam int month) {
        return Result.success(salesStatsService.getDailyStats(year, month));
    }

    @GetMapping("/monthly")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesStatItem>> monthlyStats(@RequestParam int year) {
        return Result.success(salesStatsService.getMonthlyStats(year));
    }

    @GetMapping("/yearly")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesStatItem>> yearlyStats() {
        return Result.success(salesStatsService.getYearlyStats());
    }

    @GetMapping("/ranking")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesRankingVO>> ranking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(salesStatsService.getProductRanking(startDate, endDate, limit));
    }
}