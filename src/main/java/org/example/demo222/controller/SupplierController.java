package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demo222.common.PageResult;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.SupplierQueryRequest;
import org.example.demo222.dto.request.SupplierRequest;
import org.example.demo222.dto.response.SupplierVO;
import org.example.demo222.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "供应商管理", description = "供应商信息管理")
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Operation(summary = "供应商列表", description = "分页查询供应商列表")
    @GetMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PageResult<SupplierVO>> list(SupplierQueryRequest query) {
        return Result.success(supplierService.getSupplierList(query));
    }

    @Operation(summary = "供应商详情", description = "根据ID获取供应商详情")
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<SupplierVO> detail(@PathVariable Long id) {
        return Result.success(supplierService.getSupplierDetail(id));
    }

    @Operation(summary = "所有启用供应商", description = "获取所有状态为启用的供应商列表")
    @GetMapping("/all")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<List<SupplierVO>> allActive() {
        return Result.success(supplierService.getAllActiveSuppliers());
    }

    @Operation(summary = "创建供应商", description = "新增供应商信息", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> create(@Valid @RequestBody SupplierRequest request) {
        supplierService.createSupplier(request);
        return Result.success("供应商创建成功", null);
    }

    @Operation(summary = "更新供应商", description = "修改供应商信息", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        supplierService.updateSupplier(id, request);
        return Result.success("供应商更新成功", null);
    }

    @Operation(summary = "删除供应商", description = "删除供应商（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return Result.success("供应商删除成功", null);
    }

    @Operation(summary = "供应商状态变更", description = "启用/禁用供应商", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/status")
    @RequireRole({"ADMIN"})
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        supplierService.updateSupplierStatus(id, body.get("status"));
        return Result.success("供应商状态更新成功", null);
    }
}