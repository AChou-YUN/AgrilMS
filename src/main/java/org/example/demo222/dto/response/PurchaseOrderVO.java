package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 进货单响应VO
 */
@Data
public class PurchaseOrderVO {
    private Long id;
    private String orderNo;
    private Long supplierId;
    private String supplierName;
    private Long userId;
    private String operatorName;
    private BigDecimal totalAmount;
    private Integer status;
    private String statusText;
    private LocalDate orderDate;
    private String remark;
    private LocalDateTime createTime;
    private List<PurchaseItemVO> items;
}