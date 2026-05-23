package org.example.demo222.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存盘点请求DTO
 */
@Data
public class InventoryCheckRequest {
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "实际盘点数量不能为空")
    @Min(value = 0, message = "实际盘点数量不能为负数")
    private Integer actualStock;

    private String remark;
}