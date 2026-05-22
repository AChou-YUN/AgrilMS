-- ============================================
-- 农资进销存管理系统 - 数据库初始化脚本
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS agricultural_ims 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_general_ci;

USE agricultural_ims;

-- ============================================
-- 1. 系统用户表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
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

-- ============================================
-- 2. 角色表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色标识',
    description VARCHAR(200) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 初始化角色数据
INSERT IGNORE INTO sys_role (role_name, role_key, description) VALUES
('系统管理员', 'ADMIN', '负责用户管理、基础数据配置'),
('经销商', 'DEALER', '管理进货与供应商'),
('零售商', 'RETAILER', '进行销售开单与客户管理'),
('仓库管理员', 'WAREHOUSE', '查看库存、处理出入库');

-- ============================================
-- 3. 用户角色关联表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ============================================
-- 4. 农资分类表
-- ============================================
CREATE TABLE IF NOT EXISTS product_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农资分类表';

-- 初始化分类数据
INSERT IGNORE INTO product_category (name, description, sort_order) VALUES
('种子', '各类农作物种子', 1),
('化肥', '氮肥、磷肥、钾肥、复合肥等', 2),
('农药', '杀虫剂、除草剂、杀菌剂等', 3),
('农膜', '地膜、棚膜等', 4);

-- ============================================
-- 5. 计量单位表
-- ============================================
CREATE TABLE IF NOT EXISTS unit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE COMMENT '单位名称',
    abbreviation VARCHAR(10) COMMENT '缩写',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量单位表';

-- 初始化单位数据
INSERT IGNORE INTO unit (name, abbreviation) VALUES
('千克', 'kg'), ('克', 'g'), ('吨', 't'),
('袋', '袋'), ('瓶', '瓶'), ('桶', '桶'),
('包', '包'), ('卷', '卷'), ('箱', '箱');

-- ============================================
-- 6. 农资产品表
-- ============================================
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '产品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    specification VARCHAR(100) COMMENT '规格',
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

-- ============================================
-- 7. 供应商表
-- ============================================
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址',
    credit_level TINYINT DEFAULT 3 COMMENT '信用等级：1-5',
    bank_account VARCHAR(50) COMMENT '银行账号',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- ============================================
-- 8. 客户（农户）表
-- ============================================
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '农户姓名',
    phone VARCHAR(20) COMMENT '联系方式',
    address VARCHAR(200) COMMENT '地址',
    preference VARCHAR(500) COMMENT '购买偏好',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户（农户）表';

-- ============================================
-- 9. 进货单主表
-- ============================================
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '进货单号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0待确认 1已入库 2已取消',
    order_date DATE NOT NULL COMMENT '进货日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进货单主表';

-- ============================================
-- 10. 进货单明细表
-- ============================================
CREATE TABLE IF NOT EXISTS purchase_order_item (
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

-- ============================================
-- 11. 销售单主表
-- ============================================
CREATE TABLE IF NOT EXISTS sales_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '销售单号',
    customer_id BIGINT COMMENT '客户ID',
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
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

-- ============================================
-- 12. 销售单明细表
-- ============================================
CREATE TABLE IF NOT EXISTS sales_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '销售单ID',
    product_id BIGINT NOT NULL COMMENT '农资产品ID',
    quantity INT NOT NULL COMMENT '销售数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '售价',
    subtotal DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售单明细表';

-- ============================================
-- 13. 库存表
-- ============================================
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL UNIQUE COMMENT '农资产品ID',
    current_stock INT DEFAULT 0 COMMENT '当前库存量',
    frozen_stock INT DEFAULT 0 COMMENT '冻结库存',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- ============================================
-- 14. 库存变动日志表
-- ============================================
CREATE TABLE IF NOT EXISTS inventory_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT '农资产品ID',
    change_type VARCHAR(20) NOT NULL COMMENT '变动类型：PURCHASE/SALE/ADJUST/DAMAGE/RETURN',
    change_quantity INT NOT NULL COMMENT '变动数量',
    before_stock INT NOT NULL COMMENT '变动前库存',
    after_stock INT NOT NULL COMMENT '变动后库存',
    related_order_no VARCHAR(50) COMMENT '关联单号',
    operator_id BIGINT COMMENT '操作人ID',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product (product_id),
    INDEX idx_change_type (change_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动日志表';