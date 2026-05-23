package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.ReportQueryRequest;
import org.example.demo222.dto.response.*;
import org.example.demo222.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "统计报表", description = "销售、进货、库存、利润及客户报表")
@RestController
@RequestMapping("/api/reports")
@SecurityRequirement(name = "Bearer")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "销售报表", description = "查询指定日期范围内的销售月度统计报表")
    @GetMapping("/sales")
    public Result<List<SalesStatItem>> salesReport(ReportQueryRequest query) {
        return Result.success(reportService.getSalesReport(query));
    }

    @Operation(summary = "进货报表", description = "查询指定日期范围内的进货月度统计报表")
    @GetMapping("/purchase")
    public Result<List<SalesStatItem>> purchaseReport(ReportQueryRequest query) {
        return Result.success(reportService.getPurchaseReport(query));
    }

    @Operation(summary = "库存报表", description = "获取库存总览报表：产品数、总库存、库存价值、预警数")
    @GetMapping("/inventory")
    public Result<InventoryReportSummaryVO> inventoryReport() {
        return Result.success(reportService.getInventoryReport());
    }

    @Operation(summary = "利润报表", description = "查询指定日期范围内的利润统计报表")
    @GetMapping("/profit")
    public Result<List<ProfitReportVO>> profitReport(ReportQueryRequest query) {
        return Result.success(reportService.getProfitReport(query));
    }

    @Operation(summary = "客户报表", description = "查询指定日期范围内的客户消费排行")
    @GetMapping("/customer")
    public Result<List<SalesRankingVO>> customerReport(ReportQueryRequest query) {
        return Result.success(reportService.getCustomerReport(query));
    }
}