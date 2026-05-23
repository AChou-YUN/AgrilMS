package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 库存列表响应VO
 */
@Data
public class InventoryVO {
    private Long productId;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private String specification;
    private String unitName;
    private Integer currentStock;
    private Integer safetyStock;
    private Integer frozenStock;
    private Boolean isWarning;       // 是否预警
    private Integer difference;      // 与安全库存的差额
    private BigDecimal stockValue;   // 库存价值（currentStock * purchasePrice）
}