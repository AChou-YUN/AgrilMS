package org.example.demo222.controller;

import org.example.demo222.common.Result;
import org.example.demo222.dto.request.ReportQueryRequest;
import org.example.demo222.dto.response.*;
import org.example.demo222.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计报表控制器
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    public Result<List<SalesStatItem>> salesReport(ReportQueryRequest query) {
        return Result.success(reportService.getSalesReport(query));
    }

    @GetMapping("/purchase")
    public Result<List<SalesStatItem>> purchaseReport(ReportQueryRequest query) {
        return Result.success(reportService.getPurchaseReport(query));
    }

    @GetMapping("/inventory")
    public Result<InventoryReportSummaryVO> inventoryReport() {
        return Result.success(reportService.getInventoryReport());
    }

    @GetMapping("/profit")
    public Result<List<ProfitReportVO>> profitReport(ReportQueryRequest query) {
        return Result.success(reportService.getProfitReport(query));
    }

    @GetMapping("/customer")
    public Result<List<SalesRankingVO>> customerReport(ReportQueryRequest query) {
        return Result.success(reportService.getCustomerReport(query));
    }
}