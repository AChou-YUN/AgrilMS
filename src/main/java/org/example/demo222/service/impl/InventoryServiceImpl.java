package org.example.demo222.service.impl;

import org.example.demo222.common.PageResult;
import org.example.demo222.common.UserContext;
import org.example.demo222.dto.request.DamageRequest;
import org.example.demo222.dto.request.InventoryCheckRequest;
import org.example.demo222.dto.request.InventoryLogQueryRequest;
import org.example.demo222.dto.request.InventoryQueryRequest;
import org.example.demo222.dto.response.InventoryLogVO;
import org.example.demo222.dto.response.InventoryVO;
import org.example.demo222.entity.Inventory;
import org.example.demo222.entity.InventoryLog;
import org.example.demo222.entity.Product;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.InventoryLogMapper;
import org.example.demo222.mapper.InventoryMapper;
import org.example.demo222.mapper.ProductMapper;
import org.example.demo222.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存服务实现类
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final ProductMapper productMapper;

    public InventoryServiceImpl(InventoryMapper inventoryMapper,
                                 InventoryLogMapper inventoryLogMapper,
                                 ProductMapper productMapper) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryLogMapper = inventoryLogMapper;
        this.productMapper = productMapper;
    }

    @Override
    public PageResult<InventoryVO> getInventoryList(InventoryQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = inventoryMapper.selectInventoryCount(query.getProductName(), query.getCategoryId(), query.getWarningOnly());
        List<InventoryVO> list = inventoryMapper.selectInventoryList(query.getProductName(), query.getCategoryId(),
                query.getWarningOnly(), offset, pageSize);

        PageResult<InventoryVO> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(list);
        return result;
    }

    @Override
    public InventoryVO getProductInventory(Long productId) {
        // 使用单条查询获取指定产品的库存信息
        List<InventoryVO> list = inventoryMapper.selectInventoryList(null, null, null, 0, Integer.MAX_VALUE);
        for (InventoryVO vo : list) {
            if (vo.getProductId().equals(productId)) {
                return vo;
            }
        }
        // 产品可能不在库存列表中（无库存记录），查产品信息构造返回
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "产品不存在");
        }
        InventoryVO vo = new InventoryVO();
        vo.setProductId(product.getId());
        vo.setProductName(product.getName());
        vo.setSpecification(product.getSpecification());
        vo.setSafetyStock(product.getSafetyStock());
        vo.setCurrentStock(0);
        vo.setFrozenStock(0);
        vo.setStockValue(BigDecimal.ZERO);
        vo.setIsWarning(product.getSafetyStock() != null && product.getSafetyStock() > 0);
        vo.setDifference(-(product.getSafetyStock() != null ? product.getSafetyStock() : 0));
        return vo;
    }

    @Override
    public List<InventoryVO> getWarningList(Long categoryId) {
        return inventoryMapper.selectWarningList(categoryId);
    }

    @Override
    public Integer getWarningCount() {
        Integer count = inventoryMapper.selectWarningCount();
        return count != null ? count : 0;
    }

    @Override
    @Transactional
    public void checkInventory(InventoryCheckRequest request) {
        Long productId = request.getProductId();
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("产品不存在或已下架");
        }

        Inventory inventory = inventoryMapper.selectByProductId(productId);
        int currentStock = (inventory != null) ? inventory.getCurrentStock() : 0;
        int actualStock = request.getActualStock();
        int difference = actualStock - currentStock;

        // 更新库存
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setCurrentStock(actualStock);
            inventory.setFrozenStock(0);
            inventoryMapper.insert(inventory);
        } else {
            inventoryMapper.adjustStock(productId, actualStock);
        }

        // 记录库存变动日志
        Long currentUserId = UserContext.getUserId();
        InventoryLog log = new InventoryLog();
        log.setProductId(productId);
        log.setChangeType("ADJUST");
        log.setChangeQuantity(difference);
        log.setBeforeStock(currentStock);
        log.setAfterStock(actualStock);
        log.setRelatedOrderNo(null);
        log.setOperatorId(currentUserId);
        log.setRemark(request.getRemark() != null ? request.getRemark() : "库存盘点");
        inventoryLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void processDamage(DamageRequest request) {
        Long productId = request.getProductId();
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 1) {
            throw new BusinessException("产品不存在或已下架");
        }

        Inventory inventory = inventoryMapper.selectByProductId(productId);
        int currentStock = (inventory != null) ? inventory.getCurrentStock() : 0;
        if (currentStock < request.getQuantity()) {
            throw new BusinessException("库存不足，当前库存: " + currentStock + "，报损数量: " + request.getQuantity());
        }

        // 扣减库存
        inventoryMapper.decreaseStock(productId, request.getQuantity());
        int afterStock = currentStock - request.getQuantity();

        // 记录库存变动日志
        Long currentUserId = UserContext.getUserId();
        InventoryLog log = new InventoryLog();
        log.setProductId(productId);
        log.setChangeType("DAMAGE");
        log.setChangeQuantity(-request.getQuantity());
        log.setBeforeStock(currentStock);
        log.setAfterStock(afterStock);
        log.setRelatedOrderNo(null);
        log.setOperatorId(currentUserId);
        log.setRemark("报损: " + request.getReason());
        inventoryLogMapper.insert(log);
    }

    @Override
    public PageResult<InventoryLogVO> getLogList(InventoryLogQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = inventoryLogMapper.selectCount(query.getProductId(), query.getChangeType(),
                query.getStartDate(), query.getEndDate());
        List<InventoryLogVO> list = inventoryLogMapper.selectByPage(query.getProductId(), query.getChangeType(),
                query.getStartDate(), query.getEndDate(), offset, pageSize);

        // 补充变动类型文本
        for (InventoryLogVO vo : list) {
            vo.setChangeTypeText(getChangeTypeText(vo.getChangeType()));
        }

        PageResult<InventoryLogVO> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(list);
        return result;
    }

    private String getChangeTypeText(String changeType) {
        if (changeType == null) return "未知";
        switch (changeType) {
            case "PURCHASE": return "进货入库";
            case "SALE": return "销售出库";
            case "ADJUST": return "库存盘点";
            case "DAMAGE": return "报损";
            case "RETURN": return "退货入库";
            default: return changeType;
        }
    }
}