package org.example.demo222.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 进货单明细请求DTO
 */
@Data
public class PurchaseItemRequest {
    @NotNull(message = "产品不能为空")
    private Long productId;
    @NotNull(message = "进货数量不能为空")
    @Min(1)
    private Integer quantity;
    @NotNull(message = "进价不能为空")
    @DecimalMin(value = "0.01", message = "进价必须大于0")
    private BigDecimal unitPrice;
}