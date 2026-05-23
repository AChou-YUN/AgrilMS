package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 库存报表详情响应VO
 */
@Data
public class InventoryReportVO {
    private Long productId;
    private String productName;
    private String categoryName;
    private String unitName;
    private Integer currentStock;
    private BigDecimal purchasePrice;
    private BigDecimal stockValue;       // 库存价值
    private Integer safetyStock;
    private Boolean isWarning;
}