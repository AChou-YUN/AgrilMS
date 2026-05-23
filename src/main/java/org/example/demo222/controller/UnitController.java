package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.entity.Unit;
import org.example.demo222.service.UnitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "计量单位", description = "计量单位管理")
@RestController
@RequestMapping("/api/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @Operation(summary = "单位列表", description = "获取所有计量单位")
    @GetMapping
    public Result<List<Unit>> list() {
        return Result.success(unitService.getAllUnits());
    }

    @Operation(summary = "创建单位", description = "新增计量单位（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @RequireRole({"ADMIN"})
    public Result<Void> create(@RequestBody Unit unit) {
        unitService.createUnit(unit);
        return Result.success("单位创建成功", null);
    }

    @Operation(summary = "删除单位", description = "删除计量单位（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return Result.success("单位删除成功", null);
    }
}