package org.example.demo222.config;

import org.example.demo222.interceptor.AuthInterceptor;
import org.example.demo222.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置 - 跨域 + 拦截器注册
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AuthInterceptor authInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor, AuthInterceptor authInterceptor) {
        this.loginInterceptor = loginInterceptor;
        this.authInterceptor = authInterceptor;
    }

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 拦截器注册
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**"
                )
                .order(1);

        // 权限拦截器（在登录拦截器之后执行）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .order(2);
    }
}