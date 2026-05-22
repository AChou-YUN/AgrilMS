package org.example.demo222.controller;

import jakarta.validation.Valid;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.CategoryRequest;
import org.example.demo222.entity.ProductCategory;
import org.example.demo222.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 农资分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Result<List<ProductCategory>> list() {
        return Result.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public Result<ProductCategory> detail(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @PostMapping
    @RequireRole({"ADMIN"})
    public Result<Void> create(@Valid @RequestBody CategoryRequest request) {
        categoryService.createCategory(request);
        return Result.success("分类创建成功", null);
    }

    @PutMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return Result.success("分类更新成功", null);
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("分类删除成功", null);
    }
}