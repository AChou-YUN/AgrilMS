package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.SysRole;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface SysRoleMapper {

    List<SysRole> selectAll();

    SysRole selectById(@Param("id") Long id);

    List<SysRole> selectByUserId(@Param("userId") Long userId);
}