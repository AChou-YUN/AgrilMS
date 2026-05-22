package org.example.demo222.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 进货单主表实体类
 */
@Data
public class PurchaseOrder {
    private Long id;
    private String orderNo;
    private Long supplierId;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer status;
    private LocalDate orderDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非数据库字段，用于关联查询
    private String supplierName;
    private String operatorName;
}