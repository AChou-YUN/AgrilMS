package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 库存报表汇总响应VO
 */
@Data
public class InventoryReportSummaryVO {
    private Integer totalProducts;
    private Integer totalStock;          // 总库存数量
    private BigDecimal totalValue;       // 总库存价值
    private Integer warningCount;
    private List<InventoryReportVO> details;
}