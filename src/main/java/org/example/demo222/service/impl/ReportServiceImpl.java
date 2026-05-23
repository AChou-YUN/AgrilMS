package org.example.demo222.service.impl;

import org.example.demo222.dto.request.ReportQueryRequest;
import org.example.demo222.dto.response.*;
import org.example.demo222.mapper.InventoryMapper;
import org.example.demo222.mapper.PurchaseOrderMapper;
import org.example.demo222.mapper.SalesOrderMapper;
import org.example.demo222.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表服务实现类
 */
@Service
public class ReportServiceImpl implements ReportService {

    private final SalesOrderMapper salesOrderMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final InventoryMapper inventoryMapper;

    public ReportServiceImpl(SalesOrderMapper salesOrderMapper,
                              PurchaseOrderMapper purchaseOrderMapper,
                              InventoryMapper inventoryMapper) {
        this.salesOrderMapper = salesOrderMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public List<SalesStatItem> getSalesReport(ReportQueryRequest query) {
        String type = query.getType();
        if ("daily".equals(type) && query.getYear() != null && query.getMonth() != null) {
            return salesOrderMapper.selectDailyStats(query.getYear(), query.getMonth());
        } else if ("monthly".equals(type) && query.getYear() != null) {
            return salesOrderMapper.selectMonthlyStats(query.getYear());
        } else if ("yearly".equals(type)) {
            return salesOrderMapper.selectYearlyStats();
        }
        // 默认返回按月统计
        int year = query.getYear() != null ? query.getYear() : LocalDate.now().getYear();
        return salesOrderMapper.selectMonthlyStats(year);
    }

    @Override
    public List<SalesStatItem> getPurchaseReport(ReportQueryRequest query) {
        String type = query.getType();
        if ("daily".equals(type) && query.getYear() != null && query.getMonth() != null) {
            return purchaseOrderMapper.selectDailyStats(query.getYear(), query.getMonth());
        } else if ("monthly".equals(type) && query.getYear() != null) {
            return purchaseOrderMapper.selectMonthlyStats(query.getYear());
        } else if ("yearly".equals(type)) {
            return purchaseOrderMapper.selectYearlyStats();
        }
        // 默认返回按月统计
        int year = query.getYear() != null ? query.getYear() : LocalDate.now().getYear();
        return purchaseOrderMapper.selectMonthlyStats(year);
    }

    @Override
    public InventoryReportSummaryVO getInventoryReport() {
        List<InventoryReportVO> details = inventoryMapper.selectInventoryReport();

        InventoryReportSummaryVO summary = new InventoryReportSummaryVO();
        summary.setTotalProducts(details.size());

        int totalStock = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        int warningCount = 0;

        for (InventoryReportVO item : details) {
            totalStock += (item.getCurrentStock() != null ? item.getCurrentStock() : 0);
            totalValue = totalValue.add(item.getStockValue() != null ? item.getStockValue() : BigDecimal.ZERO);
            if (Boolean.TRUE.equals(item.getIsWarning())) {
                warningCount++;
            }
        }

        summary.setTotalStock(totalStock);
        summary.setTotalValue(totalValue);
        summary.setWarningCount(warningCount);
        summary.setDetails(details);
        return summary;
    }

    @Override
    public List<ProfitReportVO> getProfitReport(ReportQueryRequest query) {
        List<SalesStatItem> salesData;
        List<SalesStatItem> purchaseData;

        String type = query.getType();
        int year = query.getYear() != null ? query.getYear() : LocalDate.now().getYear();

        if ("daily".equals(type) && query.getMonth() != null) {
            salesData = salesOrderMapper.selectDailyStats(year, query.getMonth());
            purchaseData = purchaseOrderMapper.selectDailyStats(year, query.getMonth());
        } else if ("monthly".equals(type)) {
            salesData = salesOrderMapper.selectMonthlyStats(year);
            purchaseData = purchaseOrderMapper.selectMonthlyStats(year);
        } else if ("yearly".equals(type)) {
            salesData = salesOrderMapper.selectYearlyStats();
            purchaseData = purchaseOrderMapper.selectYearlyStats();
        } else {
            salesData = salesOrderMapper.selectMonthlyStats(year);
            purchaseData = purchaseOrderMapper.selectMonthlyStats(year);
        }

        // 合并销售和进货数据
        List<ProfitReportVO> result = new ArrayList<>();
        for (SalesStatItem sales : salesData) {
            ProfitReportVO profit = new ProfitReportVO();
            profit.setPeriod(sales.getDate());
            profit.setSalesAmount(sales.getAmount() != null ? sales.getAmount() : BigDecimal.ZERO);

            // 查找匹配的进货数据
            BigDecimal purchaseAmount = BigDecimal.ZERO;
            for (SalesStatItem purchase : purchaseData) {
                if (sales.getDate().equals(purchase.getDate())) {
                    purchaseAmount = purchase.getAmount() != null ? purchase.getAmount() : BigDecimal.ZERO;
                    break;
                }
            }

            profit.setPurchaseAmount(purchaseAmount);
            BigDecimal grossProfit = profit.getSalesAmount().subtract(purchaseAmount);
            profit.setGrossProfit(grossProfit);

            // 计算利润率
            if (profit.getSalesAmount().compareTo(BigDecimal.ZERO) > 0) {
                profit.setProfitRate(grossProfit.multiply(BigDecimal.valueOf(100))
                        .divide(profit.getSalesAmount(), 2, RoundingMode.HALF_UP).doubleValue());
            } else {
                profit.setProfitRate(0.0);
            }

            result.add(profit);
        }

        return result;
    }

    @Override
    public List<SalesRankingVO> getCustomerReport(ReportQueryRequest query) {
        LocalDate startDate = query.getStartDate();
        LocalDate endDate = query.getEndDate();

        // 默认本月
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            startDate = today.withDayOfMonth(1);
            endDate = today;
        }

        return salesOrderMapper.selectProductRanking(startDate, endDate, 20);
    }
}