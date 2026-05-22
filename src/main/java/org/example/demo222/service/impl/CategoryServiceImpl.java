package org.example.demo222.service.impl;

import org.example.demo222.dto.request.CategoryRequest;
import org.example.demo222.entity.ProductCategory;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.ProductCategoryMapper;
import org.example.demo222.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final ProductCategoryMapper categoryMapper;

    public CategoryServiceImpl(ProductCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<ProductCategory> getAllCategories() {
        return categoryMapper.selectAll();
    }

    @Override
    public ProductCategory getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public void createCategory(CategoryRequest request) {
        ProductCategory category = new ProductCategory();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Long id, CategoryRequest request) {
        ProductCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        categoryMapper.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
}