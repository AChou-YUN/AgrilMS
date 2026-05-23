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

    /**
     * 本月进货统计（总额和笔数）
     */
    org.example.demo222.dto.response.SalesStatItem selectMonthPurchase();

    /**
     * 近30天进货趋势
     */
    java.util.List<org.example.demo222.dto.response.SalesStatItem> selectPurchaseTrend();

    /**
     * 按日统计进货
     */
    java.util.List<org.example.demo222.dto.response.SalesStatItem> selectDailyStats(@Param("year") int year, @Param("month") int month);

    /**
     * 按月统计进货
     */
    java.util.List<org.example.demo222.dto.response.SalesStatItem> selectMonthlyStats(@Param("year") int year);

    /**
     * 按年统计进货
     */
    java.util.List<org.example.demo222.dto.response.SalesStatItem> selectYearlyStats();
}
