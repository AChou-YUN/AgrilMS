package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.CategoryRequest;
import org.example.demo222.entity.ProductCategory;
import org.example.demo222.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "农资分类", description = "农资产品分类管理")
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "分类列表", description = "获取所有农资分类")
    @GetMapping
    public Result<List<ProductCategory>> list() {
        return Result.success(categoryService.getAllCategories());
    }

    @Operation(summary = "分类详情", description = "根据ID获取分类详情")
    @GetMapping("/{id}")
    public Result<ProductCategory> detail(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @Operation(summary = "创建分类", description = "新增农资分类（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @RequireRole({"ADMIN"})
    public Result<Void> create(@Valid @RequestBody CategoryRequest request) {
        categoryService.createCategory(request);
        return Result.success("分类创建成功", null);
    }

    @Operation(summary = "更新分类", description = "修改分类信息（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return Result.success("分类更新成功", null);
    }

    @Operation(summary = "删除分类", description = "删除指定分类（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("分类删除成功", null);
    }
}