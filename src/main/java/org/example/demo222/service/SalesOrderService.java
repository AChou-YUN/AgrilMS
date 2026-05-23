package org.example.demo222.service;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.ReturnRequest;
import org.example.demo222.dto.request.SalesOrderQueryRequest;
import org.example.demo222.dto.request.SalesOrderRequest;
import org.example.demo222.dto.response.SalesOrderVO;
import org.example.demo222.dto.response.SalesPrintVO;

/**
 * 销售单服务接口
 */
public interface SalesOrderService {
    /**
     * 分页查询销售单
     */
    PageResult<SalesOrderVO> getOrderList(SalesOrderQueryRequest query);

    /**
     * 获取销售单详情（含明细）
     */
    SalesOrderVO getOrderDetail(Long orderId);

    /**
     * 新增销售单（销售开单，自动扣减库存）
     */
    SalesOrderVO createOrder(SalesOrderRequest request);

    /**
     * 退货处理
     */
    void processReturn(Long orderId, ReturnRequest request);

    /**
     * 获取打印数据
     */
    SalesPrintVO getPrintData(Long orderId);
}