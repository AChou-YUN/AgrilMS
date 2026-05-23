package org.example.demo222.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存变动日志响应VO
 */
@Data
public class InventoryLogVO {
    private Long id;
    private Long productId;
    private String productName;
    private String changeType;
    private String changeTypeText;    // 变动类型文本
    private Integer changeQuantity;
    private Integer beforeStock;
    private Integer afterStock;
    private String relatedOrderNo;
    private Long operatorId;
    private String operatorName;
    private String remark;
    private LocalDateTime createTime;
}