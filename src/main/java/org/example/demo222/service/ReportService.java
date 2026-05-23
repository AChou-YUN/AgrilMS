package org.example.demo222.service;

import org.example.demo222.dto.request.ReportQueryRequest;
import org.example.demo222.dto.response.*;
import org.example.demo222.dto.response.SalesRankingVO;
import org.example.demo222.dto.response.SalesStatItem;

import java.util.List;

/**
 * 报表服务接口
 */
public interface ReportService {
    List<SalesStatItem> getSalesReport(ReportQueryRequest query);
    List<SalesStatItem> getPurchaseReport(ReportQueryRequest query);
    InventoryReportSummaryVO getInventoryReport();
    List<ProfitReportVO> getProfitReport(ReportQueryRequest query);
    List<SalesRankingVO> getCustomerReport(ReportQueryRequest query);
}