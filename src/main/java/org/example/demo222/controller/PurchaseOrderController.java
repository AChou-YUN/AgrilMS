package org.example.demo222.controller;

import jakarta.validation.Valid;
import org.example.demo222.common.PageResult;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.PurchaseOrderQueryRequest;
import org.example.demo222.dto.request.PurchaseOrderRequest;
import org.example.demo222.dto.response.PurchaseOrderVO;
import org.example.demo222.dto.response.PurchaseStatsVO;
import org.example.demo222.service.PurchaseOrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 进货单控制器
 */
@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PageResult<PurchaseOrderVO>> list(PurchaseOrderQueryRequest query) {
        return Result.success(purchaseOrderService.getOrderList(query));
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PurchaseOrderVO> detail(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getOrderDetail(id));
    }

    @PostMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PurchaseOrderVO> create(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderVO order = purchaseOrderService.createOrder(request);
        return Result.success("进货单创建成功", order);
    }

    @PutMapping("/{id}/confirm")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> confirm(@PathVariable Long id) {
        purchaseOrderService.confirmOrder(id);
        return Result.success("确认入库成功", null);
    }

    @PutMapping("/{id}/cancel")
    @RequireRole({"ADMIN"})
    public Result<Void> cancel(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return Result.success("进货单已取消", null);
    }

    @GetMapping("/stats")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PurchaseStatsVO> stats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(purchaseOrderService.getStats(startDate, endDate));
    }
}