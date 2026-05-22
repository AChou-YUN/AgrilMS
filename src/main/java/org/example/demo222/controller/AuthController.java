package org.example.demo222.controller;

import jakarta.validation.Valid;
import org.example.demo222.common.Result;
import org.example.demo222.common.UserContext;
import org.example.demo222.dto.request.LoginRequest;
import org.example.demo222.dto.request.PasswordRequest;
import org.example.demo222.dto.request.RegisterRequest;
import org.example.demo222.dto.request.UserUpdateRequest;
import org.example.demo222.dto.response.LoginResponse;
import org.example.demo222.dto.response.UserVO;
import org.example.demo222.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器 - 登录、注册、个人信息
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> info() {
        Long userId = UserContext.getUserId();
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordRequest request) {
        Long userId = UserContext.getUserId();
        userService.updatePassword(userId, request);
        return Result.success("密码修改成功", null);
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserUpdateRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateProfile(userId, request);
        return Result.success("个人信息更新成功", null);
    }
}