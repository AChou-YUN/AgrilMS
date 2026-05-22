package org.example.demo222.service.impl;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.ProductQueryRequest;
import org.example.demo222.dto.request.ProductRequest;
import org.example.demo222.entity.Inventory;
import org.example.demo222.entity.Product;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.InventoryMapper;
import org.example.demo222.mapper.ProductMapper;
import org.example.demo222.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 产品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;

    public ProductServiceImpl(ProductMapper productMapper, InventoryMapper inventoryMapper) {
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public PageResult<Product> getProductList(ProductQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = productMapper.selectCount(query.getName(), query.getCategoryId(),
                query.getStatus(), query.getManufacturer(), query.getLowStock());
        List<Product> list = productMapper.selectByPage(query.getName(), query.getCategoryId(),
                query.getStatus(), query.getManufacturer(), query.getLowStock(), offset, pageSize);

        PageResult<Product> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(list);
        return result;
    }

    @Override
    public Product getProductDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "产品不存在");
        }
        return product;
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        ProductQueryRequest query = new ProductQueryRequest();
        query.setCategoryId(categoryId);
        query.setPageSize(1000);
        return productMapper.selectByPage(null, categoryId, null, null, null, 0, 1000);
    }

    @Override
    @Transactional
    public void createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCategoryId(request.getCategoryId());
        product.setSpecification(request.getSpecification());
        product.setUnitId(request.getUnitId());
        product.setSafetyStock(request.getSafetyStock() != null ? request.getSafetyStock() : 0);
        product.setManufacturer(request.getManufacturer());
        product.setBatchNumber(request.getBatchNumber());
        product.setProductionDate(request.getProductionDate());
        product.setExpiryDate(request.getExpiryDate());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDescription(request.getDescription());
        product.setStatus(1);
        productMapper.insert(product);

        // 创建对应的库存记录
        Inventory inventory = new Inventory();
        inventory.setProductId(product.getId());
        inventory.setCurrentStock(0);
        inventory.setFrozenStock(0);
        inventoryMapper.insert(inventory);
    }

    @Override
    public void updateProduct(Long productId, ProductRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "产品不存在");
        }
        product.setName(request.getName());
        product.setCategoryId(request.getCategoryId());
        product.setSpecification(request.getSpecification());
        product.setUnitId(request.getUnitId());
        product.setSafetyStock(request.getSafetyStock());
        product.setManufacturer(request.getManufacturer());
        product.setBatchNumber(request.getBatchNumber());
        product.setProductionDate(request.getProductionDate());
        product.setExpiryDate(request.getExpiryDate());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setDescription(request.getDescription());
        productMapper.update(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(404, "产品不存在");
        }
        Inventory inventory = inventoryMapper.selectByProductId(productId);
        if (inventory != null && inventory.getCurrentStock() > 0) {
            throw new BusinessException("该产品库存不为0，无法删除");
        }
        productMapper.deleteById(productId);
        if (inventory != null) {
            inventoryMapper.deleteByProductId(productId);
        }
    }

    @Override
    public void updateProductStatus(Long productId, Integer status) {
        productMapper.updateStatus(productId, status);
    }

    @Override
    public void updateSafetyStock(Long productId, Integer safetyStock) {
        productMapper.updateSafetyStock(productId, safetyStock);
    }
}