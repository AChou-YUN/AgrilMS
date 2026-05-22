package org.example.demo222.service;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.PurchaseOrderQueryRequest;
import org.example.demo222.dto.request.PurchaseOrderRequest;
import org.example.demo222.dto.response.PurchaseOrderVO;
import org.example.demo222.dto.response.PurchaseStatsVO;

import java.time.LocalDate;

/**
 * 进货单服务接口
 */
public interface PurchaseOrderService {
    /**
     * 分页查询进货单
     */
    PageResult<PurchaseOrderVO> getOrderList(PurchaseOrderQueryRequest query);

    /**
     * 获取进货单详情（含明细）
     */
    PurchaseOrderVO getOrderDetail(Long orderId);

    /**
     * 新增进货单（状态为待确认，不自动增库存）
     */
    PurchaseOrderVO createOrder(PurchaseOrderRequest request);

    /**
     * 确认入库（状态变为已入库，自动增加库存）
     */
    void confirmOrder(Long orderId);

    /**
     * 取消进货单（已入库的需回退库存）
     */
    void cancelOrder(Long orderId);

    /**
     * 进货统计
     */
    PurchaseStatsVO getStats(LocalDate startDate, LocalDate endDate);
}