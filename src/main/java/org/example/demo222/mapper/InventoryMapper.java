package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.dto.response.InventoryReportVO;
import org.example.demo222.dto.response.InventoryVO;
import org.example.demo222.entity.Inventory;

import java.util.List;

/**
 * 库存Mapper接口
 */
@Mapper
public interface InventoryMapper {
    int insert(Inventory inventory);
    int updateStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    int adjustStock(@Param("productId") Long productId, @Param("actualStock") Integer actualStock);
    Inventory selectByProductId(@Param("productId") Long productId);
    int deleteByProductId(@Param("productId") Long productId);

    /**
     * 库存列表查询（含预警判断）
     */
    List<InventoryVO> selectInventoryList(@Param("productName") String productName,
                                           @Param("categoryId") Long categoryId,
                                           @Param("warningOnly") Integer warningOnly,
                                           @Param("offset") int offset,
                                           @Param("pageSize") int pageSize);

    /**
     * 库存列表计数
     */
    long selectInventoryCount(@Param("productName") String productName,
                              @Param("categoryId") Long categoryId,
                              @Param("warningOnly") Integer warningOnly);

    /**
     * 库存预警列表
     */
    List<InventoryVO> selectWarningList(@Param("categoryId") Long categoryId);

    /**
     * 库存预警数量
     */
    Integer selectWarningCount();

    /**
     * 库存报表数据
     */
    List<InventoryReportVO> selectInventoryReport();

    /**
     * 上架产品总数
     */
    Integer selectActiveProductCount();
}
