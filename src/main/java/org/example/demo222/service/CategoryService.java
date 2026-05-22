package org.example.demo222.service;

import org.example.demo222.dto.request.CategoryRequest;
import org.example.demo222.entity.ProductCategory;
import java.util.List;

public interface CategoryService {
    List<ProductCategory> getAllCategories();
    ProductCategory getCategoryById(Long id);
    void createCategory(CategoryRequest request);
    void updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
}