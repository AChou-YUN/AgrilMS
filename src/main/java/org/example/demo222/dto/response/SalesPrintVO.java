package org.example.demo222.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 销售单打印数据响应VO
 */
@Data
public class SalesPrintVO {
    private String orderNo;
    private LocalDate orderDate;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String operatorName;
    private List<SalesItemVO> items;
    private BigDecimal totalAmount;
    private String paymentMethodText;
    private String amountInChinese; // 大写金额
}