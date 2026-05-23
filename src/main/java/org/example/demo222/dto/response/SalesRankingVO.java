package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 产品销售排行响应VO
 */
@Data
public class SalesRankingVO {
    private Long productId;
    private String productName;
    private String categoryName;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
}