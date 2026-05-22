package org.example.demo222.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存变动日志实体类
 */
@Data
public class InventoryLog {
    private Long id;
    private Long productId;
    private String changeType;
    private Integer changeQuantity;
    private Integer beforeStock;
    private Integer afterStock;
    private String relatedOrderNo;
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;

    // 非数据库字段，用于关联查询
    private String productName;
    private String operatorName;
}