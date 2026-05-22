package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.Supplier;

import java.util.List;

/**
 * 供应商Mapper接口
 */
@Mapper
public interface SupplierMapper {
    int insert(Supplier supplier);
    int update(Supplier supplier);
    int deleteById(@Param("id") Long id);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    Supplier selectById(@Param("id") Long id);
    List<Supplier> selectByPage(@Param("name") String name,
                                @Param("creditLevel") Integer creditLevel,
                                @Param("status") Integer status,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);
    long selectCount(@Param("name") String name,
                     @Param("creditLevel") Integer creditLevel,
                     @Param("status") Integer status);
    List<Supplier> selectAllActive();
}