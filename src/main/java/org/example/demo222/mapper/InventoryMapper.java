package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.Inventory;

/**
 * 库存Mapper接口
 */
@Mapper
public interface InventoryMapper {
    int insert(Inventory inventory);
    int updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    int adjustStock(@Param("productId") Long productId, @Param("actualStock") Integer actualStock);
    Inventory selectByProductId(@Param("productId") Long productId);
    int deleteByProductId(@Param("productId") Long productId);
}