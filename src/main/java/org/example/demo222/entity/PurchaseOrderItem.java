package org.example.demo222.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 进货单明细表实体类
 */
@Data
public class PurchaseOrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private LocalDate productionDate;
    private String batchNumber;

    // 非数据库字段，用于关联查询
    private String productName;
    private String specification;
    private String unitName;
}