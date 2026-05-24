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

---

## 问题4：Vue3 `<style scoped>` 穿透 Element Plus 组件样式的正确写法

**发现阶段：** 前端UI优化阶段 — 侧边栏导航图标与文字对齐问题  
**表现：** Element Plus `el-menu` 的 `el-sub-menu`（有展开箭头的二级菜单）与 `el-menu-item`（一级菜单）的图标和文字没有水平对齐。子菜单的图标和文字整体偏移，与一级菜单不一致。

**问题分析：**  
1. **Element Plus 内部布局**：`.el-sub-menu__title` 内部使用 `display: flex` + `justify-content: space-between`，导致子元素（图标、文字、箭头）被分散排列。箭头图标在文档流中占据空间，使文字位置偏移。
2. **Vue3 `<style scoped>` 的 `:deep()` 选择器写法错误**：
   ```css
   /* ❌ 错误写法：scoped 的 .aside-menu 属性选择器无法匹配 Element Plus 子元素 */
   .aside-menu .el-sub-menu__title { ... }
   :deep(.aside-menu .el-sub-menu__icon-arrow) { ... }
   
   /* ✅ 正确写法：:deep() 应该放在父组件类名之前，让 scoped 属性正确穿透 */
   :deep(.aside-menu) .el-sub-menu__title { ... }
   :deep(.aside-menu) .el-sub-menu__icon-arrow { ... }
   ```

**解决方案：**  
1. 使用正确的 `:deep()` 穿透写法 `:deep(.aside-menu) .el-sub-menu__title`
2. 强制 `.el-sub-menu__title` 的 `justify-content: flex-start` 覆盖 Element Plus 默认的 `space-between`
3. 将展开箭头 `.el-sub-menu__icon-arrow` 改为 `position: absolute` 脱离文档流，固定到最右侧
4. 一级菜单和子菜单标题统一 `padding-left: 20px`，确保图标起点一致

**修改文件：** `frontend/src/layout/MainLayout.vue`

```css
/* ===== 正确的穿透写法 ===== */

/* 一级菜单项 */
:deep(.aside-menu) .el-menu-item {
  padding-left: 20px !important;
  height: 42px !important;
  line-height: 42px !important;
  margin: 2px 8px !important;
  border-radius: 4px;
}

/* 二级菜单标题 - 关键：覆盖 space-between 为 flex-start */
:deep(.aside-menu) .el-sub-menu__title {
  display: flex !important;
  justify-content: flex-start !important;
  align-items: center;
  padding-left: 20px !important;
  padding-right: 36px !important;
  height: 42px !important;
  line-height: 42px !important;
  margin: 2px 8px !important;
  border-radius: 4px;
  position: relative;
}

/* 图标统一样式 */
:deep(.aside-menu) .el-menu-item .el-icon,
:deep(.aside-menu) .el-sub-menu__title .el-icon {
  margin-right: 8px;
  font-size: 16px;
  width: 16px;
  flex-shrink: 0;
}

/* 展开箭头绝对定位到最右侧 */
:deep(.aside-menu) .el-sub-menu__icon-arrow {
  position: absolute !important;
  right: 14px !important;
  top: 50% !important;
  transform: translateY(-50%);
  font-size: 12px;
  width: auto !important;
  margin: 0 !important;
}

/* 子菜单子项缩进 */
:deep(.aside-menu) .el-sub-menu .el-menu-item {
  padding-left: 50px !important;
}
```

**经验总结：**  
- Vue3 `<style scoped>` 中 `:deep()` 的正确语法是 `:deep(.parent) .child`，而非 `:deep(.parent .child)`。前者让 `.parent` 上的 scoped 属性选择器作用于父组件，然后 `.child` 可以匹配到子组件的内部元素。
- Element Plus 的 `el-sub-menu__title` 默认使用 `justify-content: space-between`，如果需要让内容左对齐（与其他菜单项一致），必须显式覆盖为 `flex-start`。
- 当子组件有内部布局（如 flex space-between）影响外部对齐时，最可靠的方案是将干扰元素（如箭头）用 `position: absolute` 脱离文档流。
