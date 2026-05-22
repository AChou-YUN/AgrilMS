package org.example.demo222.dto.request;

import lombok.Data;

/**
 * 供应商查询请求DTO
 */
@Data
public class SupplierQueryRequest {
    private String name;
    private Integer creditLevel;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}