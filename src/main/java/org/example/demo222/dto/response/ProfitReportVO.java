package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 利润报表响应VO
 */
@Data
public class ProfitReportVO {
    private String period;               // 时间段标签
    private BigDecimal salesAmount;      // 销售额
    private BigDecimal purchaseAmount;   // 进货成本
    private BigDecimal grossProfit;      // 毛利润
    private Double profitRate;           // 利润率
}