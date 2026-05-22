package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.Unit;
import java.util.List;

/**
 * 计量单位Mapper接口
 */
@Mapper
public interface UnitMapper {
    List<Unit> selectAll();
    int insert(Unit unit);
    int deleteById(@Param("id") Long id);
}