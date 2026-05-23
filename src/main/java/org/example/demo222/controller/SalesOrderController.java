package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demo222.common.PageResult;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.ReturnRequest;
import org.example.demo222.dto.request.SalesOrderQueryRequest;
import org.example.demo222.dto.request.SalesOrderRequest;
import org.example.demo222.dto.response.SalesOrderVO;
import org.example.demo222.dto.response.SalesPrintVO;
import org.example.demo222.service.SalesOrderService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "销售管理", description = "销售单的创建、退货及打印")
@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @Operation(summary = "销售单列表", description = "分页查询销售单列表")
    @GetMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<PageResult<SalesOrderVO>> list(SalesOrderQueryRequest query) {
        return Result.success(salesOrderService.getOrderList(query));
    }

    @Operation(summary = "销售单详情", description = "根据ID获取销售单详情及明细")
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesOrderVO> detail(@PathVariable Long id) {
        return Result.success(salesOrderService.getOrderDetail(id));
    }

    @Operation(summary = "创建销售单", description = "新增销售单，自动扣减库存", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesOrderVO> create(@Valid @RequestBody SalesOrderRequest request) {
        SalesOrderVO order = salesOrderService.createOrder(request);
        return Result.success("销售单创建成功", order);
    }

    @Operation(summary = "退货处理", description = "对销售单进行退货处理，自动恢复库存", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/return")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<Void> processReturn(@PathVariable Long id, @Valid @RequestBody ReturnRequest request) {
        salesOrderService.processReturn(id, request);
        return Result.success("退货处理成功", null);
    }

    @Operation(summary = "获取打印数据", description = "获取销售单的打印格式数据")
    @GetMapping("/{id}/print")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesPrintVO> printData(@PathVariable Long id) {
        return Result.success(salesOrderService.getPrintData(id));
    }
}