package org.example.demo222.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 报损请求DTO
 */
@Data
public class DamageRequest {
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @NotNull(message = "报损数量不能为空")
    @Min(value = 1, message = "报损数量必须大于0")
    private Integer quantity;

    @NotBlank(message = "报损原因不能为空")
    private String reason;
}