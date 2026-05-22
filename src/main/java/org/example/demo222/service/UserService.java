package org.example.demo222.service;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.*;
import org.example.demo222.dto.response.LoginResponse;
import org.example.demo222.dto.response.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 获取当前用户信息
     */
    UserVO getUserInfo(Long userId);

    /**
     * 用户列表（分页+搜索）
     */
    PageResult<UserVO> getUserList(UserQueryRequest query);

    /**
     * 获取用户详情
     */
    UserVO getUserDetail(Long userId);

    /**
     * 创建用户（管理员）
     */
    void createUser(UserCreateRequest request);

    /**
     * 编辑用户
     */
    void updateUser(Long userId, UserUpdateRequest request);

    /**
     * 删除用户
     */
    void deleteUser(Long userId);

    /**
     * 启用/禁用用户
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, PasswordRequest request);

    /**
     * 修改个人信息
     */
    void updateProfile(Long userId, UserUpdateRequest request);
}