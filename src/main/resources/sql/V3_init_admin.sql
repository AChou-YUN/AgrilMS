-- ============================================
-- 初始化管理员账号和测试数据
-- 密码经过 Hutool BCrypt 加密（所有账号统一密码：123456）
-- Hutool BCrypt Hash: $2a$10$.VPHBylO7vZ8rr2aNvNzludHnN7hAGwW/x05zM0bL9LM8.GxAkuhG
-- 已验证：BCrypt.checkpw("123456", hash) = true
-- ============================================

USE agricultural_ims;

-- ============================================
-- 1. 插入测试用户（密码统一为 123456）
-- ============================================

-- 管理员账号 (密码: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, email, status) VALUES
(1, 'admin', '$2a$10$.VPHBylO7vZ8rr2aNvNzludHnN7hAGwW/x05zM0bL9LM8.GxAkuhG', '系统管理员', '13800000001', 'admin@agri.com', 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), real_name = VALUES(real_name), status = VALUES(status);

-- 经销商账号 (密码: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, email, status) VALUES
(2, 'dealer', '$2a$10$.VPHBylO7vZ8rr2aNvNzludHnN7hAGwW/x05zM0bL9LM8.GxAkuhG', '张经销商', '13800000002', 'dealer@agri.com', 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), real_name = VALUES(real_name), status = VALUES(status);

-- 零售商账号 (密码: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, email, status) VALUES
(3, 'retailer', '$2a$10$.VPHBylO7vZ8rr2aNvNzludHnN7hAGwW/x05zM0bL9LM8.GxAkuhG', '李零售商', '13800000003', 'retailer@agri.com', 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), real_name = VALUES(real_name), status = VALUES(status);

-- 仓库管理员账号 (密码: 123456)
INSERT INTO sys_user (id, username, password, real_name, phone, email, status) VALUES
(4, 'warehouse', '$2a$10$.VPHBylO7vZ8rr2aNvNzludHnN7hAGwW/x05zM0bL9LM8.GxAkuhG', '王仓管', '13800000004', 'warehouse@agri.com', 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), real_name = VALUES(real_name), status = VALUES(status);

-- ============================================
-- 2. 分配角色
-- admin  -> ADMIN (角色ID=1)
-- dealer -> DEALER (角色ID=2)
-- retailer -> RETAILER (角色ID=3)
-- warehouse -> WAREHOUSE (角色ID=4)
-- ============================================

INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),  -- admin -> 系统管理员
(2, 2),  -- dealer -> 经销商
(3, 3),  -- retailer -> 零售商
(4, 4)   -- warehouse -> 仓库管理员
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- ============================================
-- 3. 插入测试供应商
-- ============================================

INSERT IGNORE INTO supplier (id, name, contact_person, phone, address, credit_level, bank_account, status) VALUES
(1, '山东农资集团有限公司', '刘经理', '13900001111', '山东省济南市历下区经十路100号', 5, '6222021234567890001', 1),
(2, '河北化肥厂', '王主任', '13900002222', '河北省石家庄市裕华区建设大街200号', 4, '6222021234567890002', 1),
(3, '河南种子科技公司', '赵总', '13900003333', '河南省郑州市金水区农业路300号', 4, '6222021234567890003', 1),
(4, '江苏农药化工有限公司', '孙工', '13900004444', '江苏省南京市江宁区科学园400号', 3, '6222021234567890004', 1);

-- ============================================
-- 4. 插入测试客户
-- ============================================

INSERT IGNORE INTO customer (id, name, phone, address, preference, remark) VALUES
(1, '张三农户', '13700001111', '山东省临沂市沂南县张庄镇', '化肥、种子', '老客户'),
(2, '李四合作社', '13700002222', '山东省潍坊市寿光市稻田镇', '种子、农药、农膜', '合作社采购'),
(3, '王五农场', '13700003333', '山东省德州市齐河县', '化肥、农药', '大规模种植');

-- ============================================
-- 5. 插入测试产品
-- ============================================

INSERT IGNORE INTO product (id, name, category_id, specification, unit_id, safety_stock, purchase_price, selling_price, status) VALUES
(1, '尿素（氮肥）', 2, '50kg/袋', 4, 100, 85.00, 120.00, 1),
(2, '复合肥（氮磷钾）', 2, '40kg/袋', 4, 80, 120.00, 180.00, 1),
(3, '磷酸二铵', 2, '50kg/袋', 4, 60, 150.00, 220.00, 1),
(4, '杂交玉米种子', 1, '4500粒/袋', 10, 200, 25.00, 45.00, 1),
(5, '小麦种子', 1, '25kg/袋', 4, 150, 3.50, 6.00, 1),
(6, '草甘膦除草剂', 3, '1L/瓶', 5, 100, 12.00, 22.00, 1),
(7, '吡虫啉杀虫剂', 3, '500ml/瓶', 5, 80, 18.00, 32.00, 1),
(8, '农用地膜', 4, '宽1.2m 厚0.008mm', 9, 50, 5.00, 9.00, 1);

-- ============================================
-- 6. 插入初始库存
-- ============================================

INSERT IGNORE INTO inventory (product_id, current_stock, frozen_stock) VALUES
(1, 200, 0),   -- 尿素
(2, 150, 0),   -- 复合肥
(3, 80, 0),    -- 磷酸二铵
(4, 500, 0),   -- 玉米种子
(5, 300, 0),   -- 小麦种子
(6, 200, 0),   -- 草甘膦
(7, 100, 0),   -- 吡虫啉
(8, 500, 0);   -- 农用地膜