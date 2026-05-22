package org.example.demo222.service;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.SupplierQueryRequest;
import org.example.demo222.dto.request.SupplierRequest;
import org.example.demo222.dto.response.SupplierVO;

import java.util.List;

/**
 * 供应商服务接口
 */
public interface SupplierService {
    PageResult<SupplierVO> getSupplierList(SupplierQueryRequest query);
    SupplierVO getSupplierDetail(Long id);
    List<SupplierVO> getAllActiveSuppliers();
    void createSupplier(SupplierRequest request);
    void updateSupplier(Long id, SupplierRequest request);
    void deleteSupplier(Long id);
    void updateSupplierStatus(Long id, Integer status);
}