package org.example.demo222.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 产品创建/编辑请求DTO
 */
@Data
public class ProductRequest {
    @NotBlank(message = "产品名称不能为空")
    private String name;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private String specification;
    private Long unitId;
    private Integer safetyStock;
    private String manufacturer;
    private String batchNumber;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private String description;
}