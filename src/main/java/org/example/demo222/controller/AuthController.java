package org.example.demo222.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "认证管理", description = "用户登录、注册、个人信息管理")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户登录", description = "用户名密码登录，返回JWT Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success("登录成功", response);
    }

    @Operation(summary = "用户注册", description = "注册新用户，默认分配零售商角色")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "用户名已存在")
    })
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    @Operation(summary = "获取当前用户信息", description = "根据Token获取当前登录用户的详细信息", security = @SecurityRequirement(name = "Bearer"))
    @GetMapping("/info")
    public Result<UserVO> info() {
        Long userId = UserContext.getUserId();
        UserVO userVO = userService.getUserInfo(userId);
        return Result.success(userVO);
    }

    @Operation(summary = "修改密码", description = "修改当前用户密码", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordRequest request) {
        Long userId = UserContext.getUserId();
        userService.updatePassword(userId, request);
        return Result.success("密码修改成功", null);
    }

    @Operation(summary = "修改个人信息", description = "修改当前用户的姓名、手机、邮箱等信息", security = @SecurityRequirement(name = "Bearer"))
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UserUpdateRequest request) {
        Long userId = UserContext.getUserId();
        userService.updateProfile(userId, request);
        return Result.success("个人信息更新成功", null);
    }
}