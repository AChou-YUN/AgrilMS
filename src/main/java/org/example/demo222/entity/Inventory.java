package org.example.demo222.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存实体类
 */
@Data
public class Inventory {
    private Long id;
    private Long productId;
    private Integer currentStock;
    private Integer frozenStock;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}