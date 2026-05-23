package org.example.demo222.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 退货明细请求DTO
 */
@Data
public class ReturnItemRequest {
    @NotNull(message = "销售明细ID不能为空")
    private Long itemId;        // 销售明细ID

    @NotNull(message = "退货数量不能为空")
    @Min(value = 1, message = "退货数量必须大于0")
    private Integer quantity;   // 退货数量（不能超过原销售数量）
}