package org.example.demo222.service.impl;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.SupplierQueryRequest;
import org.example.demo222.dto.request.SupplierRequest;
import org.example.demo222.dto.response.SupplierVO;
import org.example.demo222.entity.Supplier;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.PurchaseOrderMapper;
import org.example.demo222.mapper.SupplierMapper;
import org.example.demo222.service.SupplierService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商服务实现类
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;
    private final PurchaseOrderMapper purchaseOrderMapper;

    public SupplierServiceImpl(SupplierMapper supplierMapper, PurchaseOrderMapper purchaseOrderMapper) {
        this.supplierMapper = supplierMapper;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    @Override
    public PageResult<SupplierVO> getSupplierList(SupplierQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = supplierMapper.selectCount(query.getName(), query.getCreditLevel(), query.getStatus());
        List<Supplier> list = supplierMapper.selectByPage(query.getName(), query.getCreditLevel(),
                query.getStatus(), offset, pageSize);

        List<SupplierVO> voList = list.stream().map(this::toVO).collect(Collectors.toList());

        PageResult<SupplierVO> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(voList);
        return result;
    }

    @Override
    public SupplierVO getSupplierDetail(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(404, "供应商不存在");
        }
        return toVO(supplier);
    }

    @Override
    public List<SupplierVO> getAllActiveSuppliers() {
        List<Supplier> list = supplierMapper.selectAllActive();
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void createSupplier(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setCreditLevel(request.getCreditLevel() != null ? request.getCreditLevel() : 3);
        supplier.setBankAccount(request.getBankAccount());
        supplier.setRemark(request.getRemark());
        supplierMapper.insert(supplier);
    }

    @Override
    public void updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(404, "供应商不存在");
        }
        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        supplier.setCreditLevel(request.getCreditLevel());
        supplier.setBankAccount(request.getBankAccount());
        supplier.setRemark(request.getRemark());
        supplierMapper.update(supplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(404, "供应商不存在");
        }
        // 检查是否有关联的进货单
        int orderCount = purchaseOrderMapper.countBySupplierId(id);
        if (orderCount > 0) {
            throw new BusinessException("该供应商存在关联的进货单，无法删除");
        }
        supplierMapper.deleteById(id);
    }

    @Override
    public void updateSupplierStatus(Long id, Integer status) {
        Supplier supplier = supplierMapper.selectById(id);
        if (supplier == null) {
            throw new BusinessException(404, "供应商不存在");
        }
        supplierMapper.updateStatus(id, status);
    }

    private SupplierVO toVO(Supplier supplier) {
        SupplierVO vo = new SupplierVO();
        BeanUtils.copyProperties(supplier, vo);
        return vo;
    }
}