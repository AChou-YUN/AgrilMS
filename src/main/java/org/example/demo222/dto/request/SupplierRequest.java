package org.example.demo222.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 供应商请求DTO
 */
@Data
public class SupplierRequest {
    @NotBlank(message = "供应商名称不能为空")
    private String name;
    private String contactPerson;
    private String phone;
    private String address;
    @Min(1)
    @Max(5)
    private Integer creditLevel = 3;
    private String bankAccount;
    private String remark;
}