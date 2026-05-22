package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 进货单明细响应VO
 */
@Data
public class PurchaseItemVO {
    private Long id;
    private Long productId;
    private String productName;
    private String specification;
    private String unitName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}