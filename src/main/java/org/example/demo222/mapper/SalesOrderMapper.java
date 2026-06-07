package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.SalesOrder;
import org.example.demo222.dto.response.SalesRankingVO;
import org.example.demo222.dto.response.SalesStatItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 销售单Mapper接口
 */
@Mapper
public interface SalesOrderMapper {
    int insert(SalesOrder salesOrder);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    SalesOrder selectById(@Param("id") Long id);
    SalesOrder selectByOrderNo(@Param("orderNo") String orderNo);
    List<SalesOrder> selectByPage(@Param("orderNo") String orderNo,
                                   @Param("customerId") Long customerId,
                                   @Param("status") Integer status,
                                   @Param("paymentMethod") Integer paymentMethod,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("offset") int offset,
                                   @Param("pageSize") int pageSize);
    long selectCount(@Param("orderNo") String orderNo,
                     @Param("customerId") Long customerId,
                     @Param("status") Integer status,
                     @Param("paymentMethod") Integer paymentMethod,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

    /**
     * 获取指定日期之后的最大序号，用于生成销售单号
     */
    Integer getMaxSequence(@Param("datePrefix") String datePrefix);

    /**
     * 按客户查询销售单（分页）
     */
    List<SalesOrder> selectByCustomerId(@Param("customerId") Long customerId,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);

    /**
     * 按客户统计销售单数量
     */
    long countByCustomerId(@Param("customerId") Long customerId);

    /**
     * 按日统计
     */
    List<SalesStatItem> selectDailyStats(@Param("year") int year, @Param("month") int month);

    /**
     * 按月统计
     */
    List<SalesStatItem> selectMonthlyStats(@Param("year") int year);

    /**
     * 按年统计
     */
    List<SalesStatItem> selectYearlyStats();

    /**
     * 产品销售排行
     */
    List<SalesRankingVO> selectProductRanking(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("limit") int limit);

    /**
     * 客户消费排行
     */
    List<SalesRankingVO> selectCustomerRanking(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("limit") int limit);

    /**
     * 统计指定日期的销售总额和笔数
     */
    SalesStatItem selectSummaryByDate(@Param("date") LocalDate date);

    /**
     * 统计指定月份的销售总额和笔数
     */
    SalesStatItem selectSummaryByMonth(@Param("year") int year, @Param("month") int month);

    /**
     * 统计指定年份的销售总额和笔数
     */
    SalesStatItem selectSummaryByYear(@Param("year") int year);
}