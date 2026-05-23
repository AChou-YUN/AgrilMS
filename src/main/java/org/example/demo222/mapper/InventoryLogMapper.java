package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.dto.response.CategorySalesVO;
import org.example.demo222.dto.response.InventoryLogVO;
import org.example.demo222.dto.response.SalesStatItem;
import org.example.demo222.entity.InventoryLog;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存变动日志Mapper接口
 */
@Mapper
public interface InventoryLogMapper {
    int insert(InventoryLog log);
    List<InventoryLog> selectByProductId(@Param("productId") Long productId);

    /**
     * 分页查询库存日志
     */
    List<InventoryLogVO> selectByPage(@Param("productId") Long productId,
                                       @Param("changeType") String changeType,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       @Param("offset") int offset,
                                       @Param("pageSize") int pageSize);

    /**
     * 库存日志计数
     */
    long selectCount(@Param("productId") Long productId,
                     @Param("changeType") String changeType,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate);

    /**
     * 近30天销售趋势
     */
    List<SalesStatItem> selectSalesTrend();

    /**
     * 分类销售占比
     */
    List<CategorySalesVO> selectCategorySales();
}
