package org.example.demo222.dto.request;

import lombok.Data;
import java.time.LocalDate;

/**
 * 销售单查询请求DTO
 */
@Data
public class SalesOrderQueryRequest {
    private String orderNo;
    private Long customerId;
    private Integer status;
    private Integer paymentMethod;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}