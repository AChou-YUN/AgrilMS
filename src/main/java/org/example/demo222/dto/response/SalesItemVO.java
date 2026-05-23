package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售单明细响应VO
 */
@Data
public class SalesItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String specification;
    private String unitName;
    private Integer quantity;
    private Integer returnQuantity;  // 已退货数量
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}