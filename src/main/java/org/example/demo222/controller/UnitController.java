package org.example.demo222.controller;

import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.entity.Unit;
import org.example.demo222.service.UnitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 计量单位控制器
 */
@RestController
@RequestMapping("/api/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public Result<List<Unit>> list() {
        return Result.success(unitService.getAllUnits());
    }

    @PostMapping
    @RequireRole({"ADMIN"})
    public Result<Void> create(@RequestBody Unit unit) {
        unitService.createUnit(unit);
        return Result.success("单位创建成功", null);
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return Result.success("单位删除成功", null);
    }
}