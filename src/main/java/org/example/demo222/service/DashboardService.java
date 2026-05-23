package org.example.demo222.service;

import org.example.demo222.dto.response.*;

import java.util.List;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    DashboardOverviewVO getOverview();
    SalesTrendVO getSalesTrend();
    List<CategorySalesVO> getCategorySales();
    List<InventoryVO> getDashboardWarnings();
}