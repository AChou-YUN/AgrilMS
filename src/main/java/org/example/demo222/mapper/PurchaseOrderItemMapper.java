package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.PurchaseOrderItem;

import java.util.List;

/**
 * 进货单明细Mapper接口
 */
@Mapper
public interface PurchaseOrderItemMapper {
    int insert(PurchaseOrderItem item);
    int batchInsert(@Param("items") List<PurchaseOrderItem> items);
    List<PurchaseOrderItem> selectByOrderId(@Param("orderId") Long orderId);
    int deleteByOrderId(@Param("orderId") Long orderId);
}