package org.example.demo222.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售单明细表实体类
 */
@Data
public class SalesOrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Integer returnQuantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    // 非数据库字段，用于关联查询
    private String productName;
    private String specification;
    private String unitName;
}