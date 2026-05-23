package org.example.demo222.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 销售单明细请求DTO
 */
@Data
public class SalesItemRequest {
    @NotNull(message = "产品不能为空")
    private Long productId;

    @NotNull(message = "销售数量不能为空")
    @Min(value = 1, message = "销售数量必须大于0")
    private Integer quantity;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价必须大于0")
    private BigDecimal unitPrice;
}