package org.example.demo222.dto.request;

import lombok.Data;
import java.time.LocalDate;

/**
 * 库存日志查询请求DTO
 */
@Data
public class InventoryLogQueryRequest {
    private Long productId;
    private String changeType;       // PURCHASE/SALE/ADJUST/DAMAGE/RETURN
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}