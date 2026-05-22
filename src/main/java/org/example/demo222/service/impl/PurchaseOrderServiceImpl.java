package org.example.demo222.service.impl;

import org.example.demo222.common.PageResult;
import org.example.demo222.common.UserContext;
import org.example.demo222.dto.request.PurchaseItemRequest;
import org.example.demo222.dto.request.PurchaseOrderQueryRequest;
import org.example.demo222.dto.request.PurchaseOrderRequest;
import org.example.demo222.dto.response.PurchaseItemVO;
import org.example.demo222.dto.response.PurchaseOrderVO;
import org.example.demo222.dto.response.PurchaseStatsVO;
import org.example.demo222.entity.*;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.*;
import org.example.demo222.service.PurchaseOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 进货单服务实现类
 */
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;
    private final SupplierMapper supplierMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;

    public PurchaseOrderServiceImpl(PurchaseOrderMapper purchaseOrderMapper,
                                     PurchaseOrderItemMapper purchaseOrderItemMapper,
                                     SupplierMapper supplierMapper,
                                     ProductMapper productMapper,
                                     InventoryMapper inventoryMapper,
                                     InventoryLogMapper inventoryLogMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderItemMapper = purchaseOrderItemMapper;
        this.supplierMapper = supplierMapper;
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
        this.inventoryLogMapper = inventoryLogMapper;
    }

    @Override
    public PageResult<PurchaseOrderVO> getOrderList(PurchaseOrderQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = purchaseOrderMapper.selectCount(query.getOrderNo(), query.getSupplierId(),
                query.getStatus(), query.getStartDate(), query.getEndDate());
        List<PurchaseOrder> list = purchaseOrderMapper.selectByPage(query.getOrderNo(), query.getSupplierId(),
                query.getStatus(), query.getStartDate(), query.getEndDate(), offset, pageSize);

        List<PurchaseOrderVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());

        PageResult<PurchaseOrderVO> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(voList);
        return result;
    }

    @Override
    public PurchaseOrderVO getOrderDetail(Long orderId) {
        PurchaseOrder order = purchaseOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "进货单不存在");
        }
        PurchaseOrderVO vo = toVO(order);
        // 加载明细
        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(orderId);
        List<PurchaseItemVO> itemVOs = items.stream().map(this::toItemVO).collect(Collectors.toList());
        vo.setItems(itemVOs);
        return vo;
    }

    @Override
    @Transactional
    public PurchaseOrderVO createOrder(PurchaseOrderRequest request) {
        // 1. 校验供应商是否存在且启用
        Supplier supplier = supplierMapper.selectById(request.getSupplierId());
        if (supplier == null || supplier.getStatus() != 1) {
            throw new BusinessException("供应商不存在或已禁用");
        }

        // 2. 校验所有产品是否存在且上架
        for (PurchaseItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("产品不存在或已下架: ID=" + item.getProductId());
            }
        }

        // 3. 生成唯一进货单号
        String orderNo = generateOrderNo();

        // 4. 计算每个明细的小计和总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        Long currentUserId = UserContext.getUserId();

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setOrderNo(orderNo);
        purchaseOrder.setSupplierId(request.getSupplierId());
        purchaseOrder.setUserId(currentUserId);
        purchaseOrder.setOrderDate(request.getOrderDate());
        purchaseOrder.setRemark(request.getRemark());
        purchaseOrder.setStatus(0); // 待确认

        List<PurchaseOrderItem> orderItems = new ArrayList<>();
        for (PurchaseItemRequest itemReq : request.getItems()) {
            BigDecimal subtotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setSubtotal(subtotal);
            orderItems.add(item);
        }

        purchaseOrder.setTotalAmount(totalAmount);

        // 5. 插入进货单主表
        purchaseOrderMapper.insert(purchaseOrder);

        // 6. 插入进货单明细表
        for (PurchaseOrderItem item : orderItems) {
            item.setOrderId(purchaseOrder.getId());
        }
        purchaseOrderItemMapper.batchInsert(orderItems);

        return getOrderDetail(purchaseOrder.getId());
    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "进货单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待确认的进货单才能确认入库");
        }

        // 更新状态为已入库
        purchaseOrderMapper.updateStatus(orderId, 1);

        // 遍历明细，增加库存
        List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(orderId);
        Long currentUserId = UserContext.getUserId();

        for (PurchaseOrderItem item : items) {
            Long productId = item.getProductId();
            int quantity = item.getQuantity();

            // 查询当前库存
            Inventory inventory = inventoryMapper.selectByProductId(productId);
            int beforeStock = (inventory != null) ? inventory.getCurrentStock() : 0;

            // 更新或插入库存表
            if (inventory == null) {
                // 如果库存记录不存在，创建新的
                inventory = new Inventory();
                inventory.setProductId(productId);
                inventory.setCurrentStock(quantity);
                inventory.setFrozenStock(0);
                inventoryMapper.insert(inventory);
            } else {
                inventoryMapper.updateStock(productId, quantity);
            }

            int afterStock = beforeStock + quantity;

            // 记录库存变动日志
            InventoryLog log = new InventoryLog();
            log.setProductId(productId);
            log.setChangeType("PURCHASE");
            log.setChangeQuantity(quantity);
            log.setBeforeStock(beforeStock);
            log.setAfterStock(afterStock);
            log.setRelatedOrderNo(order.getOrderNo());
            log.setOperatorId(currentUserId);
            log.setRemark("进货入库");
            inventoryLogMapper.insert(log);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "进货单不存在");
        }

        if (order.getStatus() == 0) {
            // 待确认：直接取消
            purchaseOrderMapper.updateStatus(orderId, 2);
        } else if (order.getStatus() == 1) {
            // 已入库：需回退库存
            List<PurchaseOrderItem> items = purchaseOrderItemMapper.selectByOrderId(orderId);
            Long currentUserId = UserContext.getUserId();

            for (PurchaseOrderItem item : items) {
                Long productId = item.getProductId();
                int quantity = item.getQuantity();

                // 查询当前库存
                Inventory inventory = inventoryMapper.selectByProductId(productId);
                if (inventory == null || inventory.getCurrentStock() < quantity) {
                    throw new BusinessException("库存不足，无法取消进货单");
                }

                int beforeStock = inventory.getCurrentStock();
                inventoryMapper.decreaseStock(productId, quantity);
                int afterStock = beforeStock - quantity;

                // 记录库存变动日志
                InventoryLog log = new InventoryLog();
                log.setProductId(productId);
                log.setChangeType("PURCHASE");
                log.setChangeQuantity(-quantity);
                log.setBeforeStock(beforeStock);
                log.setAfterStock(afterStock);
                log.setRelatedOrderNo(order.getOrderNo());
                log.setOperatorId(currentUserId);
                log.setRemark("取消进货单，回退库存");
                inventoryLogMapper.insert(log);
            }

            purchaseOrderMapper.updateStatus(orderId, 2);
        } else {
            throw new BusinessException("已取消的进货单无法再次取消");
        }
    }

    @Override
    public PurchaseStatsVO getStats(LocalDate startDate, LocalDate endDate) {
        PurchaseStatsVO stats = new PurchaseStatsVO();

        // 查询时间段内的进货单
        List<PurchaseOrder> orders = purchaseOrderMapper.selectByPage(null, null, 1, startDate, endDate, 0, Integer.MAX_VALUE);

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalCount = 0;

        for (PurchaseOrder order : orders) {
            totalAmount = totalAmount.add(order.getTotalAmount());
            totalCount++;
        }

        stats.setTotalAmount(totalAmount);
        stats.setTotalCount(totalCount);
        stats.setCategoryStats(new ArrayList<>());

        return stats;
    }

    /**
     * 生成进货单号：PO + yyyyMMdd + 3位序号
     */
    private String generateOrderNo() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Integer maxSeq = purchaseOrderMapper.getMaxSequence(datePrefix);
        int sequence = (maxSeq == null) ? 1 : maxSeq + 1;
        return "PO" + datePrefix + String.format("%03d", sequence);
    }

    private PurchaseOrderVO toVO(PurchaseOrder order) {
        PurchaseOrderVO vo = new PurchaseOrderVO();
        BeanUtils.copyProperties(order, vo);
        // 设置状态文本
        switch (order.getStatus()) {
            case 0: vo.setStatusText("待确认"); break;
            case 1: vo.setStatusText("已入库"); break;
            case 2: vo.setStatusText("已取消"); break;
            default: vo.setStatusText("未知");
        }
        return vo;
    }

    private PurchaseItemVO toItemVO(PurchaseOrderItem item) {
        PurchaseItemVO vo = new PurchaseItemVO();
        BeanUtils.copyProperties(item, vo);
        return vo;
    }
}