package org.example.demo222.service;

import org.example.demo222.dto.response.SalesRankingVO;
import org.example.demo222.dto.response.SalesStatItem;
import org.example.demo222.dto.response.SalesSummaryVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 销售统计服务接口
 */
public interface SalesStatsService {
    /**
     * 销售概况（今日/本月/本年）
     */
    SalesSummaryVO getSummary();

    /**
     * 按日统计（指定月份）
     */
    List<SalesStatItem> getDailyStats(int year, int month);

    /**
     * 按月统计（指定年份）
     */
    List<SalesStatItem> getMonthlyStats(int year);

    /**
     * 按年统计
     */
    List<SalesStatItem> getYearlyStats();

    /**
     * 产品销售排行
     */
    List<SalesRankingVO> getProductRanking(LocalDate startDate, LocalDate endDate, int limit);
}