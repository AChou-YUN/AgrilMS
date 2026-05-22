package org.example.demo222.service.impl;

import org.example.demo222.common.PageResult;
import org.example.demo222.dto.request.*;
import org.example.demo222.dto.response.LoginResponse;
import org.example.demo222.dto.response.RoleVO;
import org.example.demo222.dto.response.UserVO;
import org.example.demo222.entity.SysRole;
import org.example.demo222.entity.SysUser;
import org.example.demo222.entity.SysUserRole;
import org.example.demo222.exception.BusinessException;
import org.example.demo222.mapper.SysRoleMapper;
import org.example.demo222.mapper.SysUserMapper;
import org.example.demo222.mapper.SysUserRoleMapper;
import org.example.demo222.service.UserService;
import org.example.demo222.util.JwtUtil;
import org.example.demo222.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(SysUserMapper userMapper, SysRoleMapper roleMapper,
                           SysUserRoleMapper userRoleMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 根据用户名查询用户
        SysUser user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 2. 校验用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用，请联系管理员");
        }

        // 3. 校验密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 4. 查询用户角色
        List<SysRole> roles = roleMapper.selectByUserId(user.getId());
        List<String> roleKeys = roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toList());

        // 5. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roleKeys);

        // 6. 构建响应
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setToken(token);
        response.setRoles(roleKeys);
        return response;
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        // 1. 校验用户名唯一性
        SysUser existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        userMapper.insert(user);

        // 3. 分配默认角色（零售商）
        SysRole defaultRole = roleMapper.selectAll().stream()
                .filter(r -> "RETAILER".equals(r.getRoleKey()))
                .findFirst()
                .orElse(null);
        if (defaultRole != null) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(defaultRole.getId());
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public PageResult<UserVO> getUserList(UserQueryRequest query) {
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int offset = (pageNum - 1) * pageSize;

        long total = userMapper.selectCount(query.getUsername(), query.getRealName(),
                query.getStatus(), query.getRoleId());
        List<SysUser> users = userMapper.selectByPage(query.getUsername(), query.getRealName(),
                query.getStatus(), query.getRoleId(), offset, pageSize);

        List<UserVO> voList = users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageResult<UserVO> result = new PageResult<>();
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(voList);
        return result;
    }

    @Override
    public UserVO getUserDetail(Long userId) {
        return getUserInfo(userId);
    }

    @Override
    @Transactional
    public void createUser(UserCreateRequest request) {
        // 1. 校验用户名唯一性
        SysUser existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 创建用户
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        userMapper.insert(user);

        // 3. 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<SysUserRole> userRoles = new ArrayList<>();
            for (Long roleId : request.getRoleIds()) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                userRoles.add(userRole);
            }
            userRoleMapper.batchInsert(userRoles);
        }
    }

    @Override
    public void updateUser(Long userId, UserUpdateRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        userMapper.update(user);
    }

    @Override
    public void deleteUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userMapper.deleteById(userId);
        userRoleMapper.deleteByUserId(userId);
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userMapper.updateStatus(userId, status);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 先删除原有角色
        userRoleMapper.deleteByUserId(userId);
        // 再分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoles.add(userRole);
            }
            userRoleMapper.batchInsert(userRoles);
        }
    }

    @Override
    public void updatePassword(Long userId, PasswordRequest request) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!PasswordUtil.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        user.setPassword(PasswordUtil.encode(request.getNewPassword()));
        userMapper.update(user);
    }

    @Override
    public void updateProfile(Long userId, UserUpdateRequest request) {
        updateUser(userId, request);
    }

    /**
     * 将实体转换为VO
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());

        // 查询角色
        List<SysRole> roles = roleMapper.selectByUserId(user.getId());
        List<RoleVO> roleVOs = roles.stream().map(role -> {
            RoleVO roleVO = new RoleVO();
            roleVO.setId(role.getId());
            roleVO.setRoleName(role.getRoleName());
            roleVO.setRoleKey(role.getRoleKey());
            roleVO.setDescription(role.getDescription());
            return roleVO;
        }).collect(Collectors.toList());
        vo.setRoles(roleVOs);

        return vo;
    }
}