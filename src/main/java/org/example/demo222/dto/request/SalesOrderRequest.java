package org.example.demo222.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 销售单创建请求DTO
 */
@Data
public class SalesOrderRequest {
    private Long customerId;    // 可为空（散客）

    @NotNull(message = "销售日期不能为空")
    private LocalDate orderDate;

    @NotNull(message = "付款方式不能为空")
    private Integer paymentMethod;

    private String remark;

    @NotEmpty(message = "销售明细不能为空")
    @Valid
    private List<SalesItemRequest> items;
}