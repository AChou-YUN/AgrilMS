package org.example.demo222.controller;

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

/**
 * 供应商控制器
 */
@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<PageResult<SupplierVO>> list(SupplierQueryRequest query) {
        return Result.success(supplierService.getSupplierList(query));
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<SupplierVO> detail(@PathVariable Long id) {
        return Result.success(supplierService.getSupplierDetail(id));
    }

    @GetMapping("/all")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<List<SupplierVO>> allActive() {
        return Result.success(supplierService.getAllActiveSuppliers());
    }

    @PostMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> create(@Valid @RequestBody SupplierRequest request) {
        supplierService.createSupplier(request);
        return Result.success("供应商创建成功", null);
    }

    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        supplierService.updateSupplier(id, request);
        return Result.success("供应商更新成功", null);
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return Result.success("供应商删除成功", null);
    }

    @PutMapping("/{id}/status")
    @RequireRole({"ADMIN"})
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        supplierService.updateSupplierStatus(id, body.get("status"));
        return Result.success("供应商状态更新成功", null);
    }
}