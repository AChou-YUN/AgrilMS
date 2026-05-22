package org.example.demo222.dto.request;

import lombok.Data;

/**
 * 产品查询请求DTO
 */
@Data
public class ProductQueryRequest {
    private String name;
    private Long categoryId;
    private Integer status;
    private String manufacturer;
    private Integer lowStock;      // 是否低库存：1是
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}