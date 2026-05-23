package org.example.demo222.service.impl;

import org.example.demo222.common.PageResult;
import org.example.demo222.common.UserContext;
import org.example.demo222.dto.request.ReturnItemRequest;
import org.example.demo222.dto.request.ReturnRequest;
import org.example.demo222.dto.request.SalesItemRequest;
import org.example.demo222.dto.request.SalesOrderQueryRequest;
import org.example.demo222.dto.request.SalesOrderRequest;
import org.example.demo222.dto.response.SalesItemVO;
import org.example.demo222.dto.response.SalesOrderVO;
import org.example.demo222.dto.response.SalesPrintVO;
import org.example.demo222.entity.*;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.*;
import org.example.demo222.service.SalesOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 销售单服务实现类
 */
@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderItemMapper salesOrderItemMapper;
    private final CustomerMapper customerMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final SysUserMapper sysUserMapper;

    public SalesOrderServiceImpl(SalesOrderMapper salesOrderMapper,
                                  SalesOrderItemMapper salesOrderItemMapper,
                                  CustomerMapper customerMapper,
                                  ProductMapper productMapper,
                                  InventoryMapper inventoryMapper,
                                  InventoryLogMapper inventoryLogMapper,
                                  SysUserMapper sysUserMapper) {
        this.salesOrderMapper = salesOrderMapper;
        this.salesOrderItemMapper = salesOrderItemMapper;
        this.customerMapper = customerMapper;
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
        this.inventoryLogMapper = inventoryLogMapper;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public PageResult<SalesOrderVO> getOrderList(SalesOrderQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = salesOrderMapper.selectCount(query.getOrderNo(), query.getCustomerId(),
                query.getStatus(), query.getPaymentMethod(), query.getStartDate(), query.getEndDate());
        List<SalesOrder> list = salesOrderMapper.selectByPage(query.getOrderNo(), query.getCustomerId(),
                query.getStatus(), query.getPaymentMethod(), query.getStartDate(), query.getEndDate(),
                offset, pageSize);

        List<SalesOrderVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());

        PageResult<SalesOrderVO> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(voList);
        return result;
    }

    @Override
    public SalesOrderVO getOrderDetail(Long orderId) {
        SalesOrder order = salesOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "销售单不存在");
        }
        SalesOrderVO vo = toVO(order);
        // 加载明细
        List<SalesOrderItem> items = salesOrderItemMapper.selectByOrderId(orderId);
        List<SalesItemVO> itemVOs = items.stream().map(this::toItemVO).collect(Collectors.toList());
        vo.setItems(itemVOs);
        return vo;
    }

    @Override
    @Transactional
    public SalesOrderVO createOrder(SalesOrderRequest request) {
        // 1. 校验客户是否存在（如有指定客户）
        if (request.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(request.getCustomerId());
            if (customer == null) {
                throw new BusinessException("客户不存在: ID=" + request.getCustomerId());
            }
        }

        // 2. 校验所有产品是否存在且上架，同时校验库存
        Map<Long, Product> productMap = new java.util.HashMap<>();
        for (SalesItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("产品不存在或已下架: ID=" + item.getProductId());
            }
            productMap.put(item.getProductId(), product);

            // 校验库存是否充足
            Inventory inventory = inventoryMapper.selectByProductId(item.getProductId());
            int currentStock = (inventory != null) ? inventory.getCurrentStock() : 0;
            if (currentStock < item.getQuantity()) {
                throw new BusinessException("产品[" + product.getName() + "]库存不足，当前库存: " + currentStock + "，需要: " + item.getQuantity());
            }
        }

        // 3. 生成唯一销售单号
        String orderNo = generateOrderNo();

        // 4. 计算每个明细的小计和总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long currentUserId = UserContext.getUserId();

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setOrderNo(orderNo);
        salesOrder.setCustomerId(request.getCustomerId());
        salesOrder.setUserId(currentUserId);
        salesOrder.setOrderDate(request.getOrderDate());
        salesOrder.setRemark(request.getRemark());
        salesOrder.setPaymentMethod(request.getPaymentMethod());
        salesOrder.setStatus(1); // 已完成

        List<SalesOrderItem> orderItems = new ArrayList<>();
        for (SalesItemRequest itemReq : request.getItems()) {
            BigDecimal subtotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            SalesOrderItem item = new SalesOrderItem();
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setSubtotal(subtotal);
            orderItems.add(item);
        }

        salesOrder.setTotalAmount(totalAmount);

        // 5. 插入销售单主表
        salesOrderMapper.insert(salesOrder);

        // 6. 插入销售单明细表
        for (SalesOrderItem item : orderItems) {
            item.setOrderId(salesOrder.getId());
        }
        salesOrderItemMapper.batchInsert(orderItems);

        // 7. 遍历明细，对每个产品扣减库存并记录日志
        for (SalesItemRequest itemReq : request.getItems()) {
            Long productId = itemReq.getProductId();
            int quantity = itemReq.getQuantity();

            Inventory inventory = inventoryMapper.selectByProductId(productId);
            int beforeStock = inventory.getCurrentStock();

            // 扣减库存
            int rows = inventoryMapper.decreaseStock(productId, quantity);
            if (rows == 0) {
                throw new BusinessException("扣减库存失败，库存不足");
            }
            int afterStock = beforeStock - quantity;

            // 记录库存变动日志
            InventoryLog log = new InventoryLog();
            log.setProductId(productId);
            log.setChangeType("SALE");
            log.setChangeQuantity(-quantity);
            log.setBeforeStock(beforeStock);
            log.setAfterStock(afterStock);
            log.setRelatedOrderNo(orderNo);
            log.setOperatorId(currentUserId);
            log.setRemark("销售出库");
            inventoryLogMapper.insert(log);
        }

        return getOrderDetail(salesOrder.getId());
    }

    @Override
    @Transactional
    public void processReturn(Long orderId, ReturnRequest request) {
        SalesOrder order = salesOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "销售单不存在");
        }
        if (order.getStatus() == 2) {
            throw new BusinessException("该销售单已全部退货");
        }

        List<SalesOrderItem> items = salesOrderItemMapper.selectByOrderId(orderId);
        Long currentUserId = UserContext.getUserId();

        if (request.getItems() == null || request.getItems().isEmpty()) {
            // 整单退货
            for (SalesOrderItem item : items) {
                int returnableQty = item.getQuantity() - (item.getReturnQuantity() != null ? item.getReturnQuantity() : 0);
                if (returnableQty > 0) {
                    // 增加库存
                    Inventory inventory = inventoryMapper.selectByProductId(item.getProductId());
                    int beforeStock = (inventory != null) ? inventory.getCurrentStock() : 0;
                    inventoryMapper.updateStock(item.getProductId(), returnableQty);
                    int afterStock = beforeStock + returnableQty;

                    // 更新退货数量
                    salesOrderItemMapper.updateReturnQuantity(item.getId(), item.getQuantity());

                    // 记录库存变动日志
                    InventoryLog log = new InventoryLog();
                    log.setProductId(item.getProductId());
                    log.setChangeType("RETURN");
                    log.setChangeQuantity(returnableQty);
                    log.setBeforeStock(beforeStock);
                    log.setAfterStock(afterStock);
                    log.setRelatedOrderNo(order.getOrderNo());
                    log.setOperatorId(currentUserId);
                    log.setRemark("整单退货");
                    inventoryLogMapper.insert(log);
                }
            }
            // 更新销售单状态为已退货
            salesOrderMapper.updateStatus(orderId, 2);
        } else {
            // 部分退货
            // 构建明细ID到明细的映射
            Map<Long, SalesOrderItem> itemMap = items.stream()
                    .collect(Collectors.toMap(SalesOrderItem::getId, item -> item));

            for (ReturnItemRequest returnItem : request.getItems()) {
                SalesOrderItem item = itemMap.get(returnItem.getItemId());
                if (item == null) {
                    throw new BusinessException("销售明细不存在: ID=" + returnItem.getItemId());
                }

                int returnableQty = item.getQuantity() - (item.getReturnQuantity() != null ? item.getReturnQuantity() : 0);
                if (returnItem.getQuantity() > returnableQty) {
                    throw new BusinessException("退货数量超过可退数量，明细ID=" + returnItem.getItemId() + "，可退: " + returnableQty);
                }

                // 增加库存
                Inventory inventory = inventoryMapper.selectByProductId(item.getProductId());
                int beforeStock = (inventory != null) ? inventory.getCurrentStock() : 0;
                inventoryMapper.updateStock(item.getProductId(), returnItem.getQuantity());
                int afterStock = beforeStock + returnItem.getQuantity();

                // 更新退货数量
                int newReturnQty = (item.getReturnQuantity() != null ? item.getReturnQuantity() : 0) + returnItem.getQuantity();
                salesOrderItemMapper.updateReturnQuantity(item.getId(), newReturnQty);

                // 记录库存变动日志
                InventoryLog log = new InventoryLog();
                log.setProductId(item.getProductId());
                log.setChangeType("RETURN");
                log.setChangeQuantity(returnItem.getQuantity());
                log.setBeforeStock(beforeStock);
                log.setAfterStock(afterStock);
                log.setRelatedOrderNo(order.getOrderNo());
                log.setOperatorId(currentUserId);
                log.setRemark("部分退货");
                inventoryLogMapper.insert(log);
            }

            // 判断是否全部明细都已退完
            List<SalesOrderItem> updatedItems = salesOrderItemMapper.selectByOrderId(orderId);
            boolean allReturned = updatedItems.stream().allMatch(item ->
                    item.getReturnQuantity() != null && item.getReturnQuantity().equals(item.getQuantity()));

            if (allReturned) {
                salesOrderMapper.updateStatus(orderId, 2); // 已退货
            } else {
                salesOrderMapper.updateStatus(orderId, 3); // 部分退货
            }
        }
    }

    @Override
    public SalesPrintVO getPrintData(Long orderId) {
        SalesOrder order = salesOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "销售单不存在");
        }

        SalesPrintVO printVO = new SalesPrintVO();
        printVO.setOrderNo(order.getOrderNo());
        printVO.setOrderDate(order.getOrderDate());
        printVO.setTotalAmount(order.getTotalAmount());
        printVO.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
        printVO.setOperatorName(order.getOperatorName());

        // 客户信息
        if (order.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(order.getCustomerId());
            if (customer != null) {
                printVO.setCustomerName(customer.getName());
                printVO.setCustomerPhone(customer.getPhone());
                printVO.setCustomerAddress(customer.getAddress());
            }
        } else {
            printVO.setCustomerName("散客");
        }

        // 明细
        List<SalesOrderItem> items = salesOrderItemMapper.selectByOrderId(orderId);
        List<SalesItemVO> itemVOs = items.stream().map(this::toItemVO).collect(Collectors.toList());
        printVO.setItems(itemVOs);

        // 大写金额
        try {
            printVO.setAmountInChinese(cn.hutool.core.convert.NumberChineseFormatter.format(order.getTotalAmount().doubleValue(), false));
        } catch (Exception e) {
            printVO.setAmountInChinese("");
        }

        return printVO;
    }

    /**
     * 生成销售单号：SO + yyyyMMdd + 3位序号
     */
    private String generateOrderNo() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Integer maxSeq = salesOrderMapper.getMaxSequence(datePrefix);
        int sequence = (maxSeq == null) ? 1 : maxSeq + 1;
        return "SO" + datePrefix + String.format("%03d", sequence);
    }

    private SalesOrderVO toVO(SalesOrder order) {
        SalesOrderVO vo = new SalesOrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
        return vo;
    }

    private SalesItemVO toItemVO(SalesOrderItem item) {
        SalesItemVO vo = new SalesItemVO();
        BeanUtils.copyProperties(item, vo);
        return vo;
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "已完成";
            case 2: return "已退货";
            case 3: return "部分退货";
            default: return "未知";
        }
    }

    private String getPaymentMethodText(Integer paymentMethod) {
        if (paymentMethod == null) return "未知";
        switch (paymentMethod) {
            case 1: return "现金";
            case 2: return "微信";
            case 3: return "支付宝";
            case 4: return "赊账";
            default: return "未知";
        }
    }
}