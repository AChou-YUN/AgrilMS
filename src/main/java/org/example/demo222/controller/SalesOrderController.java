package org.example.demo222.controller;

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

/**
 * 销售单控制器
 */
@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @GetMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<PageResult<SalesOrderVO>> list(SalesOrderQueryRequest query) {
        return Result.success(salesOrderService.getOrderList(query));
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesOrderVO> detail(@PathVariable Long id) {
        return Result.success(salesOrderService.getOrderDetail(id));
    }

    @PostMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesOrderVO> create(@Valid @RequestBody SalesOrderRequest request) {
        SalesOrderVO order = salesOrderService.createOrder(request);
        return Result.success("销售单创建成功", order);
    }

    @PutMapping("/{id}/return")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<Void> processReturn(@PathVariable Long id, @Valid @RequestBody ReturnRequest request) {
        salesOrderService.processReturn(id, request);
        return Result.success("退货处理成功", null);
    }

    @GetMapping("/{id}/print")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<SalesPrintVO> printData(@PathVariable Long id) {
        return Result.success(salesOrderService.getPrintData(id));
    }
}