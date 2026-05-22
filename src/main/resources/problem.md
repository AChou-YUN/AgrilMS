# 项目问题记录

> 记录开发过程中遇到的问题及解决方案，供后续参考。

---

## 问题1：Spring Security BCryptPasswordEncoder 找不到

**发现阶段：** 阶段2 — 用户与权限管理  
**错误信息：** `PasswordUtil` 使用了 `org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder`，但项目中没有引入 Spring Security 依赖。

**问题分析：**  
初始设计中 `PasswordUtil` 使用了 Spring Security 的 `BCryptPasswordEncoder` 来进行密码加密，但项目的 pom.xml 中并未引入 `spring-boot-starter-security` 依赖，导致编译失败。

**解决方案：**  
改用已引入的 Hutool 工具库中的 `cn.hutool.crypto.digest.BCrypt` 实现 BCrypt 加密，功能完全一致且无需额外依赖。

**修改文件：** `util/PasswordUtil.java`

```java
// 修改前（使用Spring Security）
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 修改后（使用Hutool）
import cn.hutool.crypto.digest.BCrypt;

public static String encode(String rawPassword) {
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
}

public static boolean matches(String rawPassword, String encodedPassword) {
    return BCrypt.checkpw(rawPassword, encodedPassword);
}
```

---

## 问题2：Jackson ObjectMapper Bean 未找到（Spring Boot 4.0.6 兼容性问题）

**发现阶段：** 阶段2 — 用户与权限管理  
**错误信息：**
```
Parameter 1 of constructor in org.example.demo222.interceptor.LoginInterceptor 
required a bean of type 'com.fasterxml.jackson.databind.ObjectMapper' that could not be found.
```

**问题分析：**  
`LoginInterceptor` 和 `AuthInterceptor` 通过构造函数注入了 `ObjectMapper` 来序列化错误响应 JSON。在 Spring Boot 3.x 中，`spring-boot-starter-web` 会传递依赖 `spring-boot-starter-json`，从而自动注册 `ObjectMapper` Bean。但 Spring Boot 4.0.6 的 `spring-boot-starter-webmvc` 不再自动配置 `ObjectMapper` Bean，导致注入失败。

**解决方案：**  
1. 新建 `config/JacksonConfig.java` 配置类，手动注册 `ObjectMapper` Bean
2. 在 pom.xml 中添加 `jackson-datatype-jsr310` 依赖，支持 `LocalDateTime` 等 Java 8 时间类型的序列化

**修改文件：**
- `config/JacksonConfig.java`（新建）
- `pom.xml`（添加 `jackson-datatype-jsr310` 依赖）

```java
@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

**经验总结：**  
Spring Boot 4.0.x 相比 3.x 有较多 breaking changes，许多以前自动配置的 Bean 需要手动注册。在使用新版本时需特别注意依赖的自动配置行为变化。

---

## 问题3：MyBatis SqlSessionFactory 自动配置失效（Spring Boot 4.0.6 兼容性问题）

**发现阶段：** 阶段2 — 用户与权限管理（启动阶段）  
**错误信息：**
```
Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required
```

**问题分析：**  
`mybatis-spring-boot-starter` 3.0.3 的自动配置类 `MybatisAutoConfiguration` 在 Spring Boot 4.0.6 下无法正常生效。MyBatis Mapper 接口（如 `SysUserMapper`）被 `@MapperScan` 扫描后需要 `SqlSessionFactory` Bean，但自动配置未能创建它。Spring Boot 4.0.6 内部使用了 Spring Framework 7.0.7，与 MyBatis Starter 3.0.3 的自动配置存在兼容性问题。

**解决方案：**  
手动创建 `MyBatisConfig.java` 配置类，显式注册 `SqlSessionFactory` 和 `SqlSessionTemplate` Bean：

```java
@Configuration
public class MyBatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath:mapper/*.xml")
        );
        factory.setTypeAliasesPackage("org.example.demo222.entity");

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(configuration);

        return factory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

**修改文件：** `config/MyBatisConfig.java`（新建）

**经验总结：**  
Spring Boot 4.0.6 与 mybatis-spring-boot-starter 3.0.3 的自动配置不兼容。当遇到 `SqlSessionFactory` 或 `sqlSessionTemplate` 缺失的错误时，需手动在配置类中创建这两个 Bean。这也属于 Spring Boot 4.0.x breaking changes 的范畴。
