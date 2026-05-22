package org.example.demo222.controller;

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

/**
 * 农资产品控制器
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<PageResult<Product>> list(ProductQueryRequest query) {
        return Result.success(productService.getProductList(query));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> byCategory(@PathVariable Long categoryId) {
        return Result.success(productService.getProductsByCategory(categoryId));
    }

    @PostMapping
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> create(@Valid @RequestBody ProductRequest request) {
        productService.createProduct(request);
        return Result.success("产品创建成功", null);
    }

    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        productService.updateProduct(id, request);
        return Result.success("产品更新成功", null);
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("产品删除成功", null);
    }

    @PutMapping("/{id}/status")
    @RequireRole({"ADMIN", "DEALER"})
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateProductStatus(id, body.get("status"));
        return Result.success("产品状态更新成功", null);
    }

    @PutMapping("/{id}/safety-stock")
    @RequireRole({"ADMIN", "WAREHOUSE"})
    public Result<Void> updateSafetyStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateSafetyStock(id, body.get("safetyStock"));
        return Result.success("安全库存更新成功", null);
    }
}