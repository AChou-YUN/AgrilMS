package org.example.demo222.service;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.ProductQueryRequest;
import org.example.demo222.dto.request.ProductRequest;
import org.example.demo222.entity.Product;

import java.util.List;

/**
 * 产品服务接口
 */
public interface ProductService {
    PageResult<Product> getProductList(ProductQueryRequest query);
    Product getProductDetail(Long productId);
    List<Product> getProductsByCategory(Long categoryId);
    void createProduct(ProductRequest request);
    void updateProduct(Long productId, ProductRequest request);
    void deleteProduct(Long productId);
    void updateProductStatus(Long productId, Integer status);
    void updateSafetyStock(Long productId, Integer safetyStock);
}