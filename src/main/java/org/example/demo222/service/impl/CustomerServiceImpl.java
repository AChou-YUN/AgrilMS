package org.example.demo222.service.impl;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.CustomerQueryRequest;
import org.example.demo222.dto.request.CustomerRequest;
import org.example.demo222.dto.response.CustomerVO;
import org.example.demo222.dto.response.SalesItemVO;
import org.example.demo222.dto.response.SalesOrderVO;
import org.example.demo222.entity.Customer;
import org.example.demo222.entity.SalesOrder;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.CustomerMapper;
import org.example.demo222.mapper.SalesOrderItemMapper;
import org.example.demo222.mapper.SalesOrderMapper;
import org.example.demo222.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderItemMapper salesOrderItemMapper;

    public CustomerServiceImpl(CustomerMapper customerMapper,
                                SalesOrderMapper salesOrderMapper,
                                SalesOrderItemMapper salesOrderItemMapper) {
        this.customerMapper = customerMapper;
        this.salesOrderMapper = salesOrderMapper;
        this.salesOrderItemMapper = salesOrderItemMapper;
    }

    @Override
    public PageResult<CustomerVO> getCustomerList(CustomerQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = customerMapper.selectCount(query.getName(), query.getPhone());
        List<Customer> list = customerMapper.selectByPage(query.getName(), query.getPhone(), offset, pageSize);

        List<CustomerVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());

        PageResult<CustomerVO> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(voList);
        return result;
    }

    @Override
    public CustomerVO getCustomerDetail(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        return toVO(customer);
    }

    @Override
    public List<CustomerVO> getAllCustomers() {
        List<Customer> list = customerMapper.selectAll();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SalesOrderVO> getCustomerOrders(Long customerId, int pageNum, int pageSize) {
        // 校验客户是否存在
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }

        int offset = (pageNum - 1) * pageSize;
        List<SalesOrder> orders = salesOrderMapper.selectByCustomerId(customerId, offset, pageSize);

        return orders.stream().map(order -> {
            SalesOrderVO vo = toSalesOrderVO(order);
            // 加载明细
            vo.setItems(salesOrderItemMapper.selectByOrderId(order.getId()).stream()
                    .map(item -> {
                        SalesItemVO itemVO = new SalesItemVO();
                        BeanUtils.copyProperties(item, itemVO);
                        return itemVO;
                    }).collect(Collectors.toList()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void createCustomer(CustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setPreference(request.getPreference());
        customer.setRemark(request.getRemark());
        customerMapper.insert(customer);
    }

    @Override
    public void updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setPreference(request.getPreference());
        customer.setRemark(request.getRemark());
        customerMapper.update(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        // 检查是否有关联的销售单
        int orderCount = customerMapper.countByCustomerId(id);
        if (orderCount > 0) {
            throw new BusinessException("该客户存在关联的销售单，无法删除");
        }
        customerMapper.deleteById(id);
    }

    private CustomerVO toVO(Customer customer) {
        CustomerVO vo = new CustomerVO();
        BeanUtils.copyProperties(customer, vo);
        return vo;
    }

    private SalesOrderVO toSalesOrderVO(SalesOrder order) {
        SalesOrderVO vo = new SalesOrderVO();
        BeanUtils.copyProperties(order, vo);
        // 设置状态文本
        switch (order.getStatus()) {
            case 1: vo.setStatusText("已完成"); break;
            case 2: vo.setStatusText("已退货"); break;
            case 3: vo.setStatusText("部分退货"); break;
            default: vo.setStatusText("未知");
        }
        // 设置付款方式文本
        switch (order.getPaymentMethod()) {
            case 1: vo.setPaymentMethodText("现金"); break;
            case 2: vo.setPaymentMethodText("微信"); break;
            case 3: vo.setPaymentMethodText("支付宝"); break;
            case 4: vo.setPaymentMethodText("赊账"); break;
            default: vo.setPaymentMethodText("未知");
        }
        return vo;
    }
}