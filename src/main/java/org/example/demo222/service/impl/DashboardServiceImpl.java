package org.example.demo222.service.impl;

import org.example.demo222.dto.response.*;
import org.example.demo222.mapper.*;
import org.example.demo222.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final SalesOrderMapper salesOrderMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final CustomerMapper customerMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;

    public DashboardServiceImpl(SalesOrderMapper salesOrderMapper,
                                 PurchaseOrderMapper purchaseOrderMapper,
                                 CustomerMapper customerMapper,
                                 InventoryMapper inventoryMapper,
                                 InventoryLogMapper inventoryLogMapper) {
        this.salesOrderMapper = salesOrderMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.customerMapper = customerMapper;
        this.inventoryMapper = inventoryMapper;
        this.inventoryLogMapper = inventoryLogMapper;
    }

    @Override
    public DashboardOverviewVO getOverview() {
        DashboardOverviewVO vo = new DashboardOverviewVO();
        LocalDate today = LocalDate.now();

        // 今日销售
        SalesStatItem todayStats = salesOrderMapper.selectSummaryByDate(today);
        vo.setTodaySalesAmount(todayStats != null && todayStats.getAmount() != null ? todayStats.getAmount() : BigDecimal.ZERO);
        vo.setTodaySalesCount(todayStats != null && todayStats.getCount() != null ? todayStats.getCount() : 0);

        // 本月销售
        SalesStatItem monthSalesStats = salesOrderMapper.selectSummaryByMonth(today.getYear(), today.getMonthValue());
        vo.setMonthSalesAmount(monthSalesStats != null && monthSalesStats.getAmount() != null ? monthSalesStats.getAmount() : BigDecimal.ZERO);
        vo.setMonthSalesCount(monthSalesStats != null && monthSalesStats.getCount() != null ? monthSalesStats.getCount() : 0);

        // 本年销售
        SalesStatItem yearSalesStats = salesOrderMapper.selectSummaryByYear(today.getYear());
        vo.setYearSalesAmount(yearSalesStats != null && yearSalesStats.getAmount() != null ? yearSalesStats.getAmount() : BigDecimal.ZERO);

        // 本月进货
        SalesStatItem monthPurchaseStats = purchaseOrderMapper.selectMonthPurchase();
        vo.setMonthPurchaseAmount(monthPurchaseStats != null && monthPurchaseStats.getAmount() != null ? monthPurchaseStats.getAmount() : BigDecimal.ZERO);
        vo.setMonthPurchaseCount(monthPurchaseStats != null && monthPurchaseStats.getCount() != null ? monthPurchaseStats.getCount() : 0);

        // 库存数据
        Integer totalProducts = inventoryMapper.selectActiveProductCount();
        vo.setTotalProducts(totalProducts != null ? totalProducts : 0);
        Integer warningCount = inventoryMapper.selectWarningCount();
        vo.setWarningCount(warningCount != null ? warningCount : 0);

        // 本月新增客户
        int monthNewCustomers = customerMapper.countMonthNew();
        vo.setMonthNewCustomers(monthNewCustomers);

        // 本月订单总数（进货单+销售单）
        int monthOrderCount = (vo.getMonthSalesCount() != null ? vo.getMonthSalesCount() : 0)
                + (vo.getMonthPurchaseCount() != null ? vo.getMonthPurchaseCount() : 0);
        vo.setMonthOrderCount(monthOrderCount);

        return vo;
    }

    @Override
    public SalesTrendVO getSalesTrend() {
        List<SalesStatItem> trendData = inventoryLogMapper.selectSalesTrend();

        SalesTrendVO vo = new SalesTrendVO();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (SalesStatItem item : trendData) {
            dates.add(item.getDate());
            amounts.add(item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO);
            counts.add(item.getCount() != null ? item.getCount() : 0);
        }

        vo.setDates(dates);
        vo.setAmounts(amounts);
        vo.setCounts(counts);
        return vo;
    }

    @Override
    public List<CategorySalesVO> getCategorySales() {
        List<CategorySalesVO> list = inventoryLogMapper.selectCategorySales();

        // 计算占比
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CategorySalesVO item : list) {
            if (item.getAmount() != null) {
                totalAmount = totalAmount.add(item.getAmount());
            }
        }

        for (CategorySalesVO item : list) {
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0 && item.getAmount() != null) {
                item.setPercentage(item.getAmount().multiply(BigDecimal.valueOf(100))
                        .divide(totalAmount, 2, java.math.RoundingMode.HALF_UP).doubleValue());
            } else {
                item.setPercentage(0.0);
            }
        }

        return list;
    }

    @Override
    public List<InventoryVO> getDashboardWarnings() {
        List<InventoryVO> warnings = inventoryMapper.selectWarningList(null);
        // 返回前10条
        if (warnings.size() > 10) {
            return warnings.subList(0, 10);
        }
        return warnings;
    }
}