package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.Product;
import java.util.List;

/**
 * 农资产品Mapper接口
 */
@Mapper
public interface ProductMapper {
    int insert(Product product);
    int update(Product product);
    int deleteById(@Param("id") Long id);
    Product selectById(@Param("id") Long id);
    List<Product> selectByPage(@Param("name") String name,
                               @Param("categoryId") Long categoryId,
                               @Param("status") Integer status,
                               @Param("manufacturer") String manufacturer,
                               @Param("lowStock") Integer lowStock,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);
    long selectCount(@Param("name") String name,
                     @Param("categoryId") Long categoryId,
                     @Param("status") Integer status,
                     @Param("manufacturer") String manufacturer,
                     @Param("lowStock") Integer lowStock);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int updateSafetyStock(@Param("id") Long id, @Param("safetyStock") Integer safetyStock);
}