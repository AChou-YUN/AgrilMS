package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 进货统计响应VO
 */
@Data
public class PurchaseStatsVO {
    private BigDecimal totalAmount;
    private Integer totalCount;
    private List<CategoryStats> categoryStats;

    @Data
    public static class CategoryStats {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Integer quantity;
    }
}