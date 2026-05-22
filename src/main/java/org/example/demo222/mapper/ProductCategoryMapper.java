package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.ProductCategory;
import java.util.List;

/**
 * 农资分类Mapper接口
 */
@Mapper
public interface ProductCategoryMapper {
    List<ProductCategory> selectAll();
    ProductCategory selectById(@Param("id") Long id);
    int insert(ProductCategory category);
    int update(ProductCategory category);
    int deleteById(@Param("id") Long id);
}