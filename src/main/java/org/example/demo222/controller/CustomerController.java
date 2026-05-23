package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.demo222.common.PageResult;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.CustomerQueryRequest;
import org.example.demo222.dto.request.CustomerRequest;
import org.example.demo222.dto.response.CustomerVO;
import org.example.demo222.dto.response.SalesOrderVO;
import org.example.demo222.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客户管理", description = "农户/客户信息管理")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "客户列表", description = "分页查询客户列表")
    @GetMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<PageResult<CustomerVO>> list(CustomerQueryRequest query) {
        return Result.success(customerService.getCustomerList(query));
    }

    @Operation(summary = "客户详情", description = "根据ID获取客户详情")
    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<CustomerVO> detail(@PathVariable Long id) {
        return Result.success(customerService.getCustomerDetail(id));
    }

    @Operation(summary = "全部客户", description = "获取所有客户列表（不分页）")
    @GetMapping("/all")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<CustomerVO>> allCustomers() {
        return Result.success(customerService.getAllCustomers());
    }

    @Operation(summary = "客户订单", description = "获取指定客户的销售订单列表")
    @GetMapping("/{id}/orders")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesOrderVO>> customerOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(customerService.getCustomerOrders(id, pageNum, pageSize));
    }

    @Operation(summary = "创建客户", description = "新增客户信息", security = @SecurityRequirement(name = "Bearer"))
    @PostMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<Void> create(@Valid @RequestBody CustomerRequest request) {
        customerService.createCustomer(request);
        return Result.success("客户创建成功", null);
    }

    @Operation(summary = "更新客户", description = "修改客户信息", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        customerService.updateCustomer(id, request);
        return Result.success("客户更新成功", null);
    }

    @Operation(summary = "删除客户", description = "删除客户（需管理员权限）", security = @SecurityRequirement(name = "Bearer"))
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success("客户删除成功", null);
    }
}