package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface SysUserRoleMapper {

    int insert(SysUserRole userRole);

    int deleteByUserId(@Param("userId") Long userId);

    List<SysUserRole> selectByUserId(@Param("userId") Long userId);

    int batchInsert(@Param("list") List<SysUserRole> list);
}