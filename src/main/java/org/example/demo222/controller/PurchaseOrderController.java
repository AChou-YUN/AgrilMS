package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "进货管理", description = "进货单的创建、确认入库、取消及统计")
@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @Operation(summary = "进货单列表", description = "分页查询进货单列表")
    @GetMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PageResult<PurchaseOrderVO>> list(PurchaseOrderQueryRequest query) {
        return Result.success(purchaseOrderService.getOrderList(query));
    }

    @Operation(summary = "进货单详情", description = "根据ID获取进货单详情及明细")
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PurchaseOrderVO> detail(@PathVariable Long id) {
        return Result.success(purchaseOrderService.getOrderDetail(id));
    }

    @Operation(summary = "创建进货单", description = "新增进货单，自动计算总金额", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PurchaseOrderVO> create(@Valid @RequestBody PurchaseOrderRequest request) {
        PurchaseOrderVO order = purchaseOrderService.createOrder(request);
        return Result.success("进货单创建成功", order);
    }

    @Operation(summary = "确认入库", description = "确认进货单入库，自动增加库存并记录日志", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/confirm")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> confirm(@PathVariable Long id) {
        purchaseOrderService.confirmOrder(id);
        return Result.success("确认入库成功", null);
    }

    @Operation(summary = "取消进货单", description = "取消进货单（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/cancel")
    @RequireRole({"ADMIN"})
    public Result<Void> cancel(@PathVariable Long id) {
        purchaseOrderService.cancelOrder(id);
        return Result.success("进货单已取消", null);
    }

    @Operation(summary = "进货统计", description = "查询指定日期范围内的进货统计数据")
    @GetMapping("/stats")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PurchaseStatsVO> stats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(purchaseOrderService.getStats(startDate, endDate));
    }
}