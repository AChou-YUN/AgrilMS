package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.InventoryLog;

import java.util.List;

/**
 * 库存变动日志Mapper接口
 */
@Mapper
public interface InventoryLogMapper {
    int insert(InventoryLog log);
    List<InventoryLog> selectByProductId(@Param("productId") Long productId);
}