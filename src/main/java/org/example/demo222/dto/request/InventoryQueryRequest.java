package org.example.demo222.dto.request;

import lombok.Data;

/**
 * 库存查询请求DTO
 */
@Data
public class InventoryQueryRequest {
    private String productName;     // 产品名称（模糊搜索）
    private Long categoryId;        // 分类ID
    private Integer warningOnly;    // 仅预警：1是
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}