package org.example.demo222.dto.request;

import lombok.Data;

/**
 * 客户查询请求DTO
 */
@Data
public class CustomerQueryRequest {
    private String name;
    private String phone;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}