package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.PurchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 进货单Mapper接口
 */
@Mapper
public interface PurchaseOrderMapper {
    int insert(PurchaseOrder purchaseOrder);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    PurchaseOrder selectById(@Param("id") Long id);
    PurchaseOrder selectByOrderNo(@Param("orderNo") String orderNo);
    List<PurchaseOrder> selectByPage(@Param("orderNo") String orderNo,
                                     @Param("supplierId") Long supplierId,
                                     @Param("status") Integer status,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);
    long selectCount(@Param("orderNo") String orderNo,
                     @Param("supplierId") Long supplierId,
                     @Param("status") Integer status,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);
    int countBySupplierId(@Param("supplierId") Long supplierId);
    /**
     * 获取指定日期之后的最大序号，用于生成进货单号
     */
    Integer getMaxSequence(@Param("datePrefix") String datePrefix);
}