package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import org.example.demo222.dto.response.RoleVO;
import org.example.demo222.entity.SysRole;
import org.example.demo222.mapper.SysRoleMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "用户管理", description = "系统用户管理（需管理员权限）")
@RestController
@RequestMapping("/api/users")
@RequireRole({"ADMIN"})
@SecurityRequirement(name = "Bearer")
public class UserController {

    private final UserService userService;
    private final SysRoleMapper roleMapper;

    public UserController(UserService userService, SysRoleMapper roleMapper) {
        this.userService = userService;
        this.roleMapper = roleMapper;
    }

    @Operation(summary = "获取所有角色", description = "获取系统中所有可用角色列表，用于新增用户时选择角色")
    @GetMapping("/roles")
    public Result<List<RoleVO>> getAllRoles() {
        List<SysRole> roles = roleMapper.selectAll();
        List<RoleVO> roleVOs = roles.stream().map(role -> {
            RoleVO vo = new RoleVO();
            vo.setId(role.getId());
            vo.setRoleName(role.getRoleName());
            vo.setRoleKey(role.getRoleKey());
            vo.setDescription(role.getDescription());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(roleVOs);
    }

    @Operation(summary = "用户列表", description = "分页查询用户列表，支持按用户名、姓名、状态、角色搜索")
    @GetMapping
    public Result<PageResult<UserVO>> list(UserQueryRequest query) {
        PageResult<UserVO> result = userService.getUserList(query);
        return Result.success(result);
    }

    @Operation(summary = "用户详情", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable Long id) {
        UserVO userVO = userService.getUserDetail(id);
        return Result.success(userVO);
    }

    @Operation(summary = "新增用户", description = "创建新用户并分配角色")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserCreateRequest request) {
        userService.createUser(request);
        return Result.success("用户创建成功", null);
    }

    @Operation(summary = "编辑用户", description = "修改用户基本信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return Result.success("用户更新成功", null);
    }

    @Operation(summary = "删除用户", description = "删除用户及其角色关联")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("用户删除成功", null);
    }

    @Operation(summary = "启用/禁用用户", description = "设置用户状态：1启用 0禁用")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.updateUserStatus(id, status);
        return Result.success("用户状态更新成功", null);
    }

    @Operation(summary = "分配角色", description = "为用户重新分配角色（替换原有角色）")
    @PutMapping("/{id}/roles")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> body) {
        List<Long> roleIds = body.get("roleIds");
        userService.assignRoles(id, roleIds);
        return Result.success("角色分配成功", null);
    }
}