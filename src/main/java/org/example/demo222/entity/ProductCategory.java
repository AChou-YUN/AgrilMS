package org.example.demo222.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 农资分类实体类
 */
@Data
public class ProductCategory {
    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createTime;
}