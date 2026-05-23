package org.example.demo222.service;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.CustomerQueryRequest;
import org.example.demo222.dto.request.CustomerRequest;
import org.example.demo222.dto.response.CustomerVO;
import org.example.demo222.dto.response.SalesOrderVO;

import java.util.List;

/**
 * 客户服务接口
 */
public interface CustomerService {
    PageResult<CustomerVO> getCustomerList(CustomerQueryRequest query);
    CustomerVO getCustomerDetail(Long id);
    List<CustomerVO> getAllCustomers();
    List<SalesOrderVO> getCustomerOrders(Long customerId, int pageNum, int pageSize);
    void createCustomer(CustomerRequest request);
    void updateCustomer(Long id, CustomerRequest request);
    void deleteCustomer(Long id);
}