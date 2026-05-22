package org.example.demo222.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.demo222.entity.SysUser;

import java.util.List;

/**
 * 系统用户Mapper接口
 */
@Mapper
public interface SysUserMapper {

    int insert(SysUser user);

    int update(SysUser user);

    int deleteById(@Param("id") Long id);

    SysUser selectById(@Param("id") Long id);

    SysUser selectByUsername(@Param("username") String username);

    List<SysUser> selectByPage(@Param("username") String username,
                               @Param("realName") String realName,
                               @Param("status") Integer status,
                               @Param("roleId") Long roleId,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    long selectCount(@Param("username") String username,
                     @Param("realName") String realName,
                     @Param("status") Integer status,
                     @Param("roleId") Long roleId);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}