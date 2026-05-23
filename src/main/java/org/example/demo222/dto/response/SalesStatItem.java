package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售统计项
 */
@Data
public class SalesStatItem {
    private String date;           // 日期/月份标签
    private BigDecimal amount;     // 销售额
    private Integer count;         // 销售笔数
    private Integer quantity;      // 销售数量
}