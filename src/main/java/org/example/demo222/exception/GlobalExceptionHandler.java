package org.example.demo222.exception;

import org.example.demo222.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 数据库唯一键冲突异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e) {
        String message = "数据已存在，请勿重复添加";
        log.warn("数据重复: {}", e.getMessage());
        // 尝试提取重复的字段名
        String errorMsg = e.getMessage();
        if (errorMsg != null && errorMsg.contains("Duplicate entry")) {
            if (errorMsg.contains("unit.name")) {
                message = "该计量单位名称已存在";
            } else if (errorMsg.contains("product_category.name")) {
                message = "该分类名称已存在";
            } else if (errorMsg.contains("sys_user.username")) {
                message = "用户名已存在";
            } else if (errorMsg.contains("supplier.name")) {
                message = "该供应商名称已存在";
            }
        }
        return Result.error(400, message);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }

    /**
     * 其他未捕获异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("系统内部错误，请联系管理员");
    }
}