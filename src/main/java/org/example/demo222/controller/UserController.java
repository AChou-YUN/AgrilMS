package org.example.demo222.controller;

import jakarta.validation.Valid;
import org.example.demo222.common.PageResult;
import org.example.demo222.common.RequireRole;
import org.example.demo222.common.Result;
import org.example.demo222.dto.request.UserCreateRequest;
import org.example.demo222.dto.request.UserQueryRequest;
import org.example.demo222.dto.request.UserUpdateRequest;
import org.example.demo222.dto.response.UserVO;
import org.example.demo222.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器（管理员）
 */
@RestController
@RequestMapping("/api/users")
@RequireRole({"ADMIN"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户列表（分页+搜索）
     */
    @GetMapping
    public Result<PageResult<UserVO>> list(UserQueryRequest query) {
        PageResult<UserVO> result = userService.getUserList(query);
        return Result.success(result);
    }

    /**
     * 用户详情
     */
    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable Long id) {
        UserVO userVO = userService.getUserDetail(id);
        return Result.success(userVO);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return Result.success("用户创建成功", null);
    }

    /**
     * 编辑用户
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success("用户更新成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("用户删除成功", null);
    }

    /**
     * 启用/禁用用户
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.updateUserStatus(id, status);
        return Result.success("用户状态更新成功", null);
    }

    /**
     * 分配角色
     */
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> roleIds = body.get("roleIds");
        userService.assignRoles(id, roleIds);
        return Result.success("角色分配成功", null);
    }
}