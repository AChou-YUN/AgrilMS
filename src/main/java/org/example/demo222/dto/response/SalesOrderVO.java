package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售单响应VO
 */
@Data
public class SalesOrderVO {
    private Long id;
    private String orderNo;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long userId;
    private String operatorName;
    private BigDecimal totalAmount;
    private Integer paymentMethod;
    private String paymentMethodText; // 现金/微信/支付宝/赊账
    private Integer status;
    private String statusText;        // 已完成/已退货/部分退货
    private LocalDate orderDate;
    private String remark;
    private LocalDateTime createTime;
    private List<SalesItemVO> items;
}