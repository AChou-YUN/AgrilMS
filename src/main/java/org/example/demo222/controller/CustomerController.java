package org.example.demo222.controller;

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

/**
 * 客户（农户）控制器
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<PageResult<CustomerVO>> list(CustomerQueryRequest query) {
        return Result.success(customerService.getCustomerList(query));
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<CustomerVO> detail(@PathVariable Long id) {
        return Result.success(customerService.getCustomerDetail(id));
    }

    @GetMapping("/all")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<CustomerVO>> allCustomers() {
        return Result.success(customerService.getAllCustomers());
    }

    @GetMapping("/{id}/orders")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<List<SalesOrderVO>> customerOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(customerService.getCustomerOrders(id, pageNum, pageSize));
    }

    @PostMapping
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<Void> create(@Valid @RequestBody CustomerRequest request) {
        customerService.createCustomer(request);
        return Result.success("客户创建成功", null);
    }

    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "RETAILER"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        customerService.updateCustomer(id, request);
        return Result.success("客户更新成功", null);
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN"})
    public Result<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return Result.success("客户删除成功", null);
    }
}