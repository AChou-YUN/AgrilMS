package org.example.demo222.dto.request;

import lombok.Data;
import java.time.LocalDate;

/**
 * 进货单查询请求DTO
 */
@Data
public class PurchaseOrderQueryRequest {
    private String orderNo;
    private Long supplierId;
    private Integer status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}