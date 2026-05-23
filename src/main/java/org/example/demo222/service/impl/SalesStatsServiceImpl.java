package org.example.demo222.service.impl;

import org.example.demo222.dto.response.SalesRankingVO;
import org.example.demo222.dto.response.SalesStatItem;
import org.example.demo222.dto.response.SalesSummaryVO;
import org.example.demo222.mapper.SalesOrderMapper;
import org.example.demo222.service.SalesStatsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 销售统计服务实现类
 */
@Service
public class SalesStatsServiceImpl implements SalesStatsService {

    private final SalesOrderMapper salesOrderMapper;

    public SalesStatsServiceImpl(SalesOrderMapper salesOrderMapper) {
        this.salesOrderMapper = salesOrderMapper;
    }

    @Override
    public SalesSummaryVO getSummary() {
        SalesSummaryVO summary = new SalesSummaryVO();
        LocalDate today = LocalDate.now();

        // 今日统计
        SalesStatItem todayStats = salesOrderMapper.selectSummaryByDate(today);
        summary.setTodayAmount(todayStats != null && todayStats.getAmount() != null ? todayStats.getAmount() : BigDecimal.ZERO);
        summary.setTodayCount(todayStats != null && todayStats.getCount() != null ? todayStats.getCount() : 0);

        // 本月统计
        SalesStatItem monthStats = salesOrderMapper.selectSummaryByMonth(today.getYear(), today.getMonthValue());
        summary.setMonthAmount(monthStats != null && monthStats.getAmount() != null ? monthStats.getAmount() : BigDecimal.ZERO);
        summary.setMonthCount(monthStats != null && monthStats.getCount() != null ? monthStats.getCount() : 0);

        // 本年统计
        SalesStatItem yearStats = salesOrderMapper.selectSummaryByYear(today.getYear());
        summary.setYearAmount(yearStats != null && yearStats.getAmount() != null ? yearStats.getAmount() : BigDecimal.ZERO);
        summary.setYearCount(yearStats != null && yearStats.getCount() != null ? yearStats.getCount() : 0);

        return summary;
    }

    @Override
    public List<SalesStatItem> getDailyStats(int year, int month) {
        return salesOrderMapper.selectDailyStats(year, month);
    }

    @Override
    public List<SalesStatItem> getMonthlyStats(int year) {
        return salesOrderMapper.selectMonthlyStats(year);
    }

    @Override
    public List<SalesStatItem> getYearlyStats() {
        return salesOrderMapper.selectYearlyStats();
    }

    @Override
    public List<SalesRankingVO> getProductRanking(LocalDate startDate, LocalDate endDate, int limit) {
        return salesOrderMapper.selectProductRanking(startDate, endDate, limit);
    }
}