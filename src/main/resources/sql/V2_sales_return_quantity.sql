-- ============================================
-- 阶段6：销售与客户管理 - 数据库变更脚本
-- 为 sales_order_item 表添加 return_quantity 字段
-- ============================================

USE agricultural_ims;

-- 给 sales_order_item 表添加退货数量字段
ALTER TABLE sales_order_item 
ADD COLUMN return_quantity INT DEFAULT 0 COMMENT '已退货数量' AFTER quantity;