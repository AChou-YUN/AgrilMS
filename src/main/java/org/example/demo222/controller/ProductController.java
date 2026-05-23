package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demo222.common.PageResult;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.ProductQueryRequest;
import org.example.demo222.dto.request.ProductRequest;
import org.example.demo222.entity.Product;
import org.example.demo222.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "农资产品", description = "农资产品的增删改查管理")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "产品列表", description = "分页查询产品列表，支持按名称、分类、状态搜索")
    @GetMapping
    public Result<PageResult<Product>> list(ProductQueryRequest query) {
        return Result.success(productService.getProductList(query));
    }

    @Operation(summary = "产品详情", description = "根据产品ID获取详细信息")
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @Operation(summary = "按分类查询", description = "获取指定分类下的所有上架产品")
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> byCategory(@PathVariable Long categoryId) {
        return Result.success(productService.getProductsByCategory(categoryId));
    }

    @Operation(summary = "创建产品", description = "新增农资产品（需管理员或经销商权限）", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> create(@Valid @RequestBody ProductRequest request) {
        productService.createProduct(request);
        return Result.success("产品创建成功", null);
    }

    @Operation(summary = "更新产品", description = "修改产品信息（需管理员或经销商权限）", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        productService.updateProduct(id, request);
        return Result.success("产品更新成功", null);
    }

    @Operation(summary = "删除产品", description = "删除产品（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("产品删除成功", null);
    }

    @Operation(summary = "产品上下架", description = "设置产品状态：1上架 0下架", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/status")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateProductStatus(id, body.get("status"));
        return Result.success("产品状态更新成功", null);
    }

    @Operation(summary = "更新安全库存", description = "设置产品的安全库存阈值（需管理员或仓库管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}/safety-stock")
    @RequireRole({"ADMIN", "WAREHOUSE"})
    public Result<Void> updateSafetyStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateSafetyStock(id, body.get("safetyStock"));
        return Result.success("安全库存更新成功", null);
    }
}