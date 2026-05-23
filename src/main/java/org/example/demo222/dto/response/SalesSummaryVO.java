package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售概况响应VO
 */
@Data
public class SalesSummaryVO {
    private BigDecimal todayAmount;
    private Integer todayCount;
    private BigDecimal monthAmount;
    private Integer monthCount;
    private BigDecimal yearAmount;
    private Integer yearCount;
}