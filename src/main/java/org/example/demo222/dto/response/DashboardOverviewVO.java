package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 经营概况响应VO
 */
@Data
public class DashboardOverviewVO {
    // 销售数据
    private BigDecimal todaySalesAmount;
    private Integer todaySalesCount;
    private BigDecimal monthSalesAmount;
    private Integer monthSalesCount;
    private BigDecimal yearSalesAmount;

    // 进货数据
    private BigDecimal monthPurchaseAmount;
    private Integer monthPurchaseCount;

    // 库存数据
    private Integer totalProducts;
    private Integer warningCount;
    private Integer monthNewCustomers;

    // 订单数据
    private Integer monthOrderCount;
}