package org.example.demo222.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 进货单创建请求DTO
 */
@Data
public class PurchaseOrderRequest {
    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    @NotNull(message = "进货日期不能为空")
    private LocalDate orderDate;

    private String remark;

    @NotEmpty(message = "进货明细不能为空")
    @Valid
    private List<PurchaseItemRequest> items;
}