package org.example.demo222.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售单主表实体类
 */
@Data
public class SalesOrder {
    private Long id;
    private String orderNo;
    private Long customerId;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer paymentMethod;
    private Integer status;
    private LocalDate orderDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非数据库字段，用于关联查询
    private String customerName;
    private String customerPhone;
    private String operatorName;
}