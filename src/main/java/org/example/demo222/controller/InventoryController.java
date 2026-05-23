package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "库存管理", description = "库存查询、预警、盘点、报损及变动日志")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "库存列表", description = "分页查询库存列表，支持按产品名、分类、预警筛选")
    @GetMapping
    public Result<PageResult<InventoryVO>> list(InventoryQueryRequest query) {
        return Result.success(inventoryService.getInventoryList(query));
    }

    @Operation(summary = "产品库存详情", description = "获取指定产品的库存信息")
    @GetMapping("/{productId}")
    public Result<InventoryVO> detail(@PathVariable Long productId) {
        return Result.success(inventoryService.getProductInventory(productId));
    }

    @Operation(summary = "库存预警列表", description = "获取低于安全库存的产品列表")
    @GetMapping("/warnings")
    public Result<List<InventoryVO>> warnings(
            @RequestParam(required = false) Long categoryId) {
        return Result.success(inventoryService.getWarningList(categoryId));
    }

    @Operation(summary = "库存预警数量", description = "获取预警产品数量")
    @GetMapping("/warnings/count")
    public Result<Integer> warningCount() {
        return Result.success(inventoryService.getWarningCount());
    }

    @Operation(summary = "库存盘点", description = "调整产品实际库存量（需管理员或仓库管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/check")
    @RequireRole({"ADMIN", "WAREHOUSE"})
    public Result<Void> checkInventory(@Valid @RequestBody InventoryCheckRequest request) {
        inventoryService.checkInventory(request);
        return Result.success("库存盘点成功", null);
    }

    @Operation(summary = "报损处理", description = "产品报损，自动扣减库存并记录日志", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping("/damage")
    @RequireRole({"ADMIN", "WAREHOUSE"})
    public Result<Void> processDamage(@Valid @RequestBody DamageRequest request) {
        inventoryService.processDamage(request);
        return Result.success("报损处理成功", null);
    }

    @Operation(summary = "库存变动日志", description = "查询库存变动记录，支持按产品、变动类型筛选")
    @GetMapping("/logs")
    public Result<PageResult<InventoryLogVO>> logs(InventoryLogQueryRequest query) {
        return Result.success(inventoryService.getLogList(query));
    }
}