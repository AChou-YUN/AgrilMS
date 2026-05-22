package org.example.demo222.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 农资产品实体类
 */
@Data
public class Product {
    private Long id;
    private String name;
    private Long categoryId;
    private String specification;
    private Long unitId;
    private Integer safetyStock;
    private String manufacturer;
    private String batchNumber;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非数据库字段，用于关联查询
    private String categoryName;
    private String unitName;
    private Integer currentStock;
}