# 阶段1：数据库设计与MyBatis集成

## 一、阶段目标

完成数据库所有表的设计与创建，编写对应的实体类、Mapper接口及XML映射文件，实现基础的CRUD操作。

**预计工期：2天**

**前置条件：** 阶段0完成，项目可正常编译并连接数据库。

---

## 二、数据库设计

### 2.1 创建数据库

```sql
CREATE DATABASE agricultural_ims DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE agricultural_ims;
```

### 2.2 表结构设计

#### （1）系统用户表 `sys_user`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码（BCrypt加密） |
| real_name | VARCHAR(50) | | 真实姓名 |
| phone | VARCHAR(20) | | 手机号 |
| email | VARCHAR(100) | | 邮箱 |
| avatar | VARCHAR(255) | | 头像URL |
| status | TINYINT | DEFAULT 1 | 状态：1启用，0禁用 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

```sql
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

#### （2）角色表 `sys_role`

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 角色ID |
| role_name | VARCHAR(50) | UNIQUE, NOT NULL | 角色名称 |
| role_key | VARCHAR(50) | UNIQUE, NOT NULL | 角色标识（ADMIN/DEALER/RETAILER/WAREHOUSE） |
| description | VARCHAR(200) | | 角色描述 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

```sql
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色标识',
    description VARCHAR(200) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 初始化角色数据
INSERT INTO sys_role (role_name, role_key, description) VALUES
('系统管理员', 'ADMIN', '负责用户管理、基础数据配置'),
('经销商', 'DEALER', '管理进货与供应商'),
('零售商', 'RETAILER', '进行销售开单与客户管理'),
('仓库管理员', 'WAREHOUSE', '查看库存、处理出入库');
```

#### （3）用户角色关联表 `sys_user_role`

```sql
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

#### （4）农资分类表 `product_category`

```sql
CREATE TABLE product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称（种子/化肥/农药/农膜）',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资分类表';

-- 初始化分类数据
INSERT INTO product_category (name, description, sort_order) VALUES
('种子', '各类农作物种子', 1),
('化肥', '氮肥、磷肥、钾肥、复合肥等', 2),
('农药', '杀虫剂、除草剂、杀菌剂等', 3),
('农膜', '地膜、棚膜等', 4);
```

#### （5）计量单位表 `unit`

```sql
CREATE TABLE unit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE COMMENT '单位名称',
    abbreviation VARCHAR(10) COMMENT '缩写',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量单位表';

-- 初始化单位数据
INSERT INTO unit (name, abbreviation) VALUES
('千克', 'kg'), ('克', 'g'), ('吨', 't'),
('袋', '袋'), ('瓶', '瓶'), ('桶', '桶'),
('包', '包'), ('卷', '卷'), ('箱', '箱');
```

#### （6）农资产品表 `product`

```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '产品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    specification VARCHAR(100) COMMENT '规格（如50kg/袋）',
    unit_id BIGINT COMMENT '计量单位ID',
    safety_stock INT DEFAULT 0 COMMENT '安全库存阈值',
    manufacturer VARCHAR(100) COMMENT '生产厂家',
    batch_number VARCHAR(50) COMMENT '批号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '保质期至',
    purchase_price DECIMAL(10,2) DEFAULT 0 COMMENT '参考进价',
    selling_price DECIMAL(10,2) DEFAULT 0 COMMENT '参考售价',
    description TEXT COMMENT '产品描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1上架 0下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资产品表';
```

#### （7）供应商表 `supplier`

```sql
CREATE TABLE supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址',
    credit_level TINYINT DEFAULT 3 COMMENT '信用等级：1-5，5最高',
    bank_account VARCHAR(50) COMMENT '银行账号',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';
```

#### （8）客户（农户）表 `customer`

```sql
CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '农户姓名',
    phone VARCHAR(20) COMMENT '联系方式',
    address VARCHAR(200) COMMENT '地址',
    preference VARCHAR(500) COMMENT '购买偏好',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户（农户）表';
```

#### （9）进货单主表 `purchase_order`

```sql
CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '进货单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    user_id BIGINT NOT NULL COMMENT '操作用户ID（经销商）',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0待确认 1已入库 2已取消',
    order_date DATE NOT NULL COMMENT '进货日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进货单主表';
```

#### （10）进货单明细表 `purchase_order_item`

```sql
CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '进货单ID',
    product_id BIGINT NOT NULL COMMENT '农资产品ID',
    quantity INT NOT NULL COMMENT '进货数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '进价',
    subtotal DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    production_date DATE COMMENT '生产日期',
    batch_number VARCHAR(50) COMMENT '批号',
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进货单明细表';
```

#### （11）销售单主表 `sales_order`

```sql
CREATE TABLE sales_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '销售单号',
    customer_id BIGINT COMMENT '客户ID（农户）',
    user_id BIGINT NOT NULL COMMENT '操作用户ID（零售商）',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    payment_method TINYINT DEFAULT 1 COMMENT '付款方式：1现金 2微信 3支付宝 4赊账',
    status TINYINT DEFAULT 1 COMMENT '状态：1已完成 2已退货 3部分退货',
    order_date DATE NOT NULL COMMENT '销售日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售单主表';
```

#### （12）销售单明细表 `sales_order_item`

```sql
CREATE TABLE sales_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '销售单ID',
    product_id BIGINT NOT NULL COMMENT '农资产品ID',
    quantity INT NOT NULL COMMENT '销售数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '售价',
    subtotal DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售单明细表';
```

#### （13）库存表 `inventory`

```sql
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL UNIQUE COMMENT '农资产品ID',
    current_stock INT DEFAULT 0 COMMENT '当前库存量',
    frozen_stock INT DEFAULT 0 COMMENT '冻结库存（退货中等）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';
```

#### （14）库存变动日志表 `inventory_log`

```sql
CREATE TABLE inventory_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '农资产品ID',
    change_type VARCHAR(20) NOT NULL COMMENT '变动类型：PURCHASE入库/SALE出库/ADJUST盘点调整/DAMAGE报损/RETURN退货',
    change_quantity INT NOT NULL COMMENT '变动数量（正为入库，负为出库）',
    before_stock INT NOT NULL COMMENT '变动前库存',
    after_stock INT NOT NULL COMMENT '变动后库存',
    related_order_no VARCHAR(50) COMMENT '关联单号',
    operator_id BIGINT COMMENT '操作人ID',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product (product_id),
    INDEX idx_change_type (change_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动日志表';
```

---

## 三、实体类编写

为每张数据库表编写对应的Java实体类，放在 `entity` 包下。使用Lombok注解简化代码。

### 3.1 实体类清单

| 实体类 | 对应表 | 关键字段 |
|--------|--------|----------|
| `SysUser` | sys_user | id, username, password, realName, phone, email, status |
| `SysRole` | sys_role | id, roleName, roleKey, description |
| `SysUserRole` | sys_user_role | id, userId, roleId |
| `ProductCategory` | product_category | id, name, description, sortOrder |
| `Unit` | unit | id, name, abbreviation |
| `Product` | product | id, name, categoryId, specification, unitId, safetyStock, manufacturer |
| `Supplier` | supplier | id, name, contactPerson, phone, address, creditLevel |
| `Customer` | customer | id, name, phone, address, preference |
| `PurchaseOrder` | purchase_order | id, orderNo, supplierId, userId, totalAmount, status, orderDate |
| `PurchaseOrderItem` | purchase_order_item | id, orderId, productId, quantity, unitPrice, subtotal |
| `SalesOrder` | sales_order | id, orderNo, customerId, userId, totalAmount, paymentMethod, status |
| `SalesOrderItem` | sales_order_item | id, orderId, productId, quantity, unitPrice, subtotal |
| `Inventory` | inventory | id, productId, currentStock, frozenStock |
| `InventoryLog` | inventory_log | id, productId, changeType, changeQuantity, beforeStock, afterStock |

### 3.2 实体类示例

```java
@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long categoryId;
    private String specification;
    private Long unitId;
    private Integer safetyStock;
    private String manufacturer;
    private String batchNumber;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private String description;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private String unitName;
    @TableField(exist = false)
    private Integer currentStock;
}
```

---

## 四、Mapper接口与XML映射

### 4.1 Mapper接口清单

| Mapper接口 | 对应表 | 主要方法 |
|------------|--------|----------|
| `SysUserMapper` | sys_user | insert, update, deleteById, selectById, selectByUsername, selectByPage |
| `SysRoleMapper` | sys_role | selectAll, selectByUserId |
| `SysUserRoleMapper` | sys_user_role | insert, deleteByUserId, selectByUserId |
| `ProductCategoryMapper` | product_category | insert, update, deleteById, selectAll, selectById |
| `UnitMapper` | unit | selectAll, insert, deleteById |
| `ProductMapper` | product | insert, update, deleteById, selectById, selectByPage, selectByCategoryId |
| `SupplierMapper` | supplier | insert, update, deleteById, selectById, selectByPage |
| `CustomerMapper` | customer | insert, update, deleteById, selectById, selectByPage |
| `PurchaseOrderMapper` | purchase_order | insert, update, selectById, selectByPage, selectByDateRange |
| `PurchaseOrderItemMapper` | purchase_order_item | insert, selectByOrderId, deleteByOrderId |
| `SalesOrderMapper` | sales_order | insert, update, selectById, selectByPage, selectByDateRange |
| `SalesOrderItemMapper` | sales_order_item | insert, selectByOrderId, deleteByOrderId |
| `InventoryMapper` | inventory | insert, update, selectByProductId, selectAll, selectBelowSafetyStock |
| `InventoryLogMapper` | inventory_log | insert, selectByProductId, selectByPage |

### 4.2 XML映射文件

每个Mapper对应一个XML文件，放在 `src/main/resources/mapper/` 目录下。需要编写复杂的关联查询和动态SQL。

### 4.3 MyBatis配置

在启动类或配置类上添加 `@MapperScan("org.example.demo222.mapper")` 注解。

---

## 五、验证标准

| 验证项 | 验证方法 |
|--------|----------|
| 所有表创建成功 | MySQL中执行 `SHOW TABLES`，确认14张表存在 |
| 初始化数据正确 | 角色表、分类表、单位表有初始数据 |
| 实体类编译通过 | `mvn clean compile` 通过 |
| Mapper接口注入成功 | 启动应用后Spring容器中可获取Mapper |
| 基础CRUD正常 | 通过单元测试验证每个Mapper的增删改查 |

---

## 六、产出文件清单

| 文件类型 | 数量 | 路径 |
|----------|------|------|
| SQL脚本 | 1个 | `src/main/resources/sql/init.sql` |
| 实体类 | 14个 | `src/main/java/.../entity/*.java` |
| Mapper接口 | 14个 | `src/main/java/.../mapper/*.java` |
| XML映射文件 | 14个 | `src/main/resources/mapper/*.xml` |
| MapperScan配置 | 1个 | 修改启动类或新建配置类 |

---

## 七、注意事项

1. 所有表统一使用 `InnoDB` 引擎，支持事务。
2. 字段命名使用下划线风格，实体类属性使用驼峰命名，通过 MyBatis 的 `map-underscore-to-camel-case` 自动映射。
3. 涉及金额的字段使用 `DECIMAL(10,2)` 或 `DECIMAL(12,2)`，Java中对应 `BigDecimal`。
4. 日期字段使用 `DATE` 或 `DATETIME`，Java中对应 `LocalDate` 或 `LocalDateTime`。
5. 外键关系通过应用层维护，不在数据库层面设置外键约束（提升性能）。
6. 编写 `init.sql` 统一管理建表和初始数据。