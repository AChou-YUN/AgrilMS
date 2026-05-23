package org.example.demo222.controller;

import jakarta.validation.Valid;
import org.example.demo222.common.PageResult;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.DamageRequest;
import org.example.demo222.dto.request.InventoryCheckRequest;
import org.example.demo222.dto.request.InventoryLogQueryRequest;
import org.example.demo222.dto.request.InventoryQueryRequest;
import org.example.demo222.dto.response.InventoryLogVO;
import org.example.demo222.dto.response.InventoryVO;
import org.example.demo222.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存管理控制器
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public Result<PageResult<InventoryVO>> list(InventoryQueryRequest query) {
        return Result.success(inventoryService.getInventoryList(query));
    }

    @GetMapping("/{productId}")
    public Result<InventoryVO> detail(@PathVariable Long productId) {
        return Result.success(inventoryService.getProductInventory(productId));
    }

    @GetMapping("/warnings")
    public Result<List<InventoryVO>> warnings(
            @RequestParam(required = false) Long categoryId) {
        return Result.success(inventoryService.getWarningList(categoryId));
    }

    @GetMapping("/warnings/count")
    public Result<Integer> warningCount() {
        return Result.success(inventoryService.getWarningCount());
    }

    @PostMapping("/check")
    @RequireRole({"ADMIN", "WAREHOUSE"})
    public Result<Void> checkInventory(@Valid @RequestBody InventoryCheckRequest request) {
        inventoryService.checkInventory(request);
        return Result.success("库存盘点成功", null);
    }

    @PostMapping("/damage")
    @RequireRole({"ADMIN", "WAREHOUSE"})
    public Result<Void> processDamage(@Valid @RequestBody DamageRequest request) {
        inventoryService.processDamage(request);
        return Result.success("报损处理成功", null);
    }

    @GetMapping("/logs")
    public Result<PageResult<InventoryLogVO>> logs(InventoryLogQueryRequest query) {
        return Result.success(inventoryService.getLogList(query));
    }
}