package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 销售趋势响应VO
 */
@Data
public class SalesTrendVO {
    private List<String> dates;           // 日期列表
    private List<BigDecimal> amounts;     // 每日销售额
    private List<Integer> counts;         // 每日订单数
}