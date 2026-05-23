package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.SalesOrderItem;

import java.util.List;

/**
 * 销售单明细Mapper接口
 */
@Mapper
public interface SalesOrderItemMapper {
    int insert(SalesOrderItem item);
    int batchInsert(@Param("items") List<SalesOrderItem> items);
    List<SalesOrderItem> selectByOrderId(@Param("orderId") Long orderId);
    SalesOrderItem selectById(@Param("id") Long id);
    int updateReturnQuantity(@Param("id") Long id, @Param("returnQuantity") Integer returnQuantity);
    int deleteByOrderId(@Param("orderId") Long orderId);
}