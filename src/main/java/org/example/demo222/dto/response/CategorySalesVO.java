package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 分类销售占比响应VO
 */
@Data
public class CategorySalesVO {
    private String categoryName;
    private BigDecimal amount;
    private Integer quantity;
    private Double percentage;           // 占比百分比
}