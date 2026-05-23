package org.example.demo222.service;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.DamageRequest;
import org.example.demo222.dto.request.InventoryCheckRequest;
import org.example.demo222.dto.request.InventoryLogQueryRequest;
import org.example.demo222.dto.request.InventoryQueryRequest;
import org.example.demo222.dto.response.InventoryLogVO;
import org.example.demo222.dto.response.InventoryVO;

import java.util.List;

/**
 * 库存服务接口
 */
public interface InventoryService {
    PageResult<InventoryVO> getInventoryList(InventoryQueryRequest query);
    InventoryVO getProductInventory(Long productId);
    List<InventoryVO> getWarningList(Long categoryId);
    Integer getWarningCount();
    void checkInventory(InventoryCheckRequest request);
    void processDamage(DamageRequest request);
    PageResult<InventoryLogVO> getLogList(InventoryLogQueryRequest query);
}