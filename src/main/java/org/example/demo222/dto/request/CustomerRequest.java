package org.example.demo222.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 客户请求DTO
 */
@Data
public class CustomerRequest {
    @NotBlank(message = "客户姓名不能为空")
    private String name;
    private String phone;
    private String address;
    private String preference;
    private String remark;
}