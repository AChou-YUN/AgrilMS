@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set BASE=http://localhost:8080
set PASS=0
set FAIL=0
set TOTAL=0

echo ============================================
echo  农资进销存管理系统 - 全量接口测试
echo ============================================
echo.

REM ===== 步骤1: 注册测试用户 =====
echo [准备] 注册管理员测试账号...
curl -s -X POST %BASE%/api/auth/register -H "Content-Type: application/json" -d "{\"username\":\"testadmin\",\"password\":\"admin123\",\"realName\":\"测试管理员\",\"phone\":\"13900000001\",\"email\":\"test@agri.com\"}" >nul 2>&1

REM 为admin用户分配ADMIN角色
echo [准备] 为测试用户分配管理员角色...
mysql -u root -p050825 agricultural_ims -e "INSERT IGNORE INTO sys_user_role (user_id, role_id) SELECT u.id, 1 FROM sys_user u WHERE u.username='testadmin';" 2>nul

REM ===== 步骤2: 登录获取Token =====
echo [准备] 登录获取Token...
for /f "tokens=*" %%i in ('curl -s -X POST %BASE%/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"testadmin\",\"password\":\"admin123\"}"') do set LOGIN_RESP=%%i
echo 登录响应: %LOGIN_RESP%

REM 提取token (简单方式 - 从完整JSON中提取)
for /f "tokens=2 delims=:," %%a in ('echo %LOGIN_RESP% ^| findstr /C:"token"') do set TOKEN=%%a
set TOKEN=%TOKEN:"=%
set TOKEN=%TOKEN: =%
set AUTH=Authorization: Bearer %TOKEN%
echo Token: %AUTH%
echo.

REM ============================================
echo ============================================
echo  模块1: 认证模块 /api/auth
echo ============================================
echo.

REM 1.4 修改密码
echo [1.4] PUT /api/auth/password - 修改密码
curl -s -X PUT %BASE%/api/auth/password -H "Content-Type: application/json" -H "%AUTH%" -d "{\"oldPassword\":\"admin123\",\"newPassword\":\"newpass123\"}"
echo.
echo [1.4] 改回密码
curl -s -X PUT %BASE%/api/auth/password -H "Content-Type: application/json" -H "%AUTH%" -d "{\"oldPassword\":\"newpass123\",\"newPassword\":\"admin123\"}"
echo.

REM 1.5 修改个人信息
echo [1.5] PUT /api/auth/profile - 修改个人信息
curl -s -X PUT %BASE%/api/auth/profile -H "Content-Type: application/json" -H "%AUTH%" -d "{\"realName\":\"管理员测试\",\"phone\":\"13900000099\",\"email\":\"updated@agri.com\"}"
echo.

REM 1.6 未认证访问
echo [1.6] GET /api/auth/info (无Token) - 预期401
curl -s -X GET %BASE%/api/auth/info
echo.

echo.
echo ============================================
echo  模块2: 用户管理 /api/users
echo ============================================
echo.

REM 2.2 用户详情
echo [2.2] GET /api/users/1 - 用户详情
curl -s -X GET "%BASE%/api/users/1" -H "%AUTH%"
echo.

REM 2.3 创建用户
echo [2.3] POST /api/users - 创建用户(经销商)
curl -s -X POST %BASE%/api/users -H "Content-Type: application/json" -H "%AUTH%" -d "{\"username\":\"testdealer\",\"password\":\"dealer123\",\"realName\":\"测试经销商\",\"phone\":\"13900000002\",\"roleIds\":[2]}"
echo.

REM 2.4 编辑用户
echo [2.4] PUT /api/users/1 - 编辑用户
curl -s -X PUT "%BASE%/api/users/1" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"realName\":\"系统管理员\",\"phone\":\"13900000001\",\"email\":\"admin@agri.com\"}"
echo.

REM 2.6 启用/禁用用户
echo [2.6] PUT /api/users/1/status - 禁用再启用
curl -s -X PUT "%BASE%/api/users/1/status" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"status\":0}"
echo.
curl -s -X PUT "%BASE%/api/users/1/status" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"status\":1}"
echo.

REM 2.7 分配角色
echo [2.7] PUT /api/users/1/roles - 分配角色
curl -s -X PUT "%BASE%/api/users/1/roles" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"roleIds\":[1,3]}"
echo.

echo.
echo ============================================
echo  模块3: 分类管理 /api/categories
echo ============================================
echo.

REM 3.2 分类详情
echo [3.2] GET /api/categories/1 - 分类详情
curl -s -X GET "%BASE%/api/categories/1" -H "%AUTH%"
echo.

REM 3.3 创建分类
echo [3.3] POST /api/categories - 创建分类
curl -s -X POST %BASE%/api/categories -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"农具\",\"description\":\"各类农用工具\",\"sortOrder\":5}"
echo.

REM 3.4 更新分类
echo [3.4] PUT /api/categories/5 - 更新分类
curl -s -X PUT "%BASE%/api/categories/5" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"农用工具\",\"description\":\"各类农用工具及设备\",\"sortOrder\":5}"
echo.

echo.
echo ============================================
echo  模块4: 计量单位 /api/units
echo ============================================
echo.

REM 4.2 创建单位
echo [4.2] POST /api/units - 创建单位
curl -s -X POST %BASE%/api/units -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"片\",\"abbreviation\":\"片\"}"
echo.

echo.
echo ============================================
echo  模块5: 农资产品 /api/products
echo ============================================
echo.

REM 5.4 创建产品
echo [5.4] POST /api/products - 创建产品
curl -s -X POST %BASE%/api/products -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"杂交水稻种子\",\"categoryId\":1,\"specification\":\"50kg/袋\",\"unitId\":4,\"safetyStock\":100,\"manufacturer\":\"某种业公司\",\"purchasePrice\":80.00,\"sellingPrice\":100.00,\"description\":\"优质杂交水稻种子\"}"
echo.

REM 5.1 产品列表
echo [5.1] GET /api/products - 产品列表
curl -s -X GET "%BASE%/api/products?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

REM 5.2 产品详情
echo [5.2] GET /api/products/1 - 产品详情
curl -s -X GET "%BASE%/api/products/1" -H "%AUTH%"
echo.

REM 5.3 按分类查询
echo [5.3] GET /api/products/category/1 - 按分类查询
curl -s -X GET "%BASE%/api/products/category/1" -H "%AUTH%"
echo.

REM 5.5 更新产品
echo [5.5] PUT /api/products/1 - 更新产品
curl -s -X PUT "%BASE%/api/products/1" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"超级杂交水稻种子\",\"categoryId\":1,\"specification\":\"50kg/袋\",\"unitId\":4,\"safetyStock\":150,\"manufacturer\":\"某种业公司\",\"purchasePrice\":85.00,\"sellingPrice\":110.00,\"description\":\"高产杂交水稻种子\"}"
echo.

REM 5.7 产品上下架
echo [5.7] PUT /api/products/1/status - 产品下架
curl -s -X PUT "%BASE%/api/products/1/status" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"status\":0}"
echo.
echo [5.7] PUT /api/products/1/status - 产品上架
curl -s -X PUT "%BASE%/api/products/1/status" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"status\":1}"
echo.

REM 5.8 更新安全库存
echo [5.8] PUT /api/products/1/safety-stock - 更新安全库存
curl -s -X PUT "%BASE%/api/products/1/safety-stock" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"safetyStock\":200}"
echo.

echo.
echo ============================================
echo  模块6: 供应商管理 /api/suppliers
echo ============================================
echo.

REM 6.4 创建供应商
echo [6.4] POST /api/suppliers - 创建供应商
curl -s -X POST %BASE%/api/suppliers -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"金禾农资供应链\",\"contactPerson\":\"李经理\",\"phone\":\"0571-88888888\",\"address\":\"浙江省杭州市\",\"creditLevel\":5,\"remark\":\"优质供应商\"}"
echo.

REM 6.1 供应商列表
echo [6.1] GET /api/suppliers - 供应商列表
curl -s -X GET "%BASE%/api/suppliers?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

REM 6.2 供应商详情
echo [6.2] GET /api/suppliers/1 - 供应商详情
curl -s -X GET "%BASE%/api/suppliers/1" -H "%AUTH%"
echo.

REM 6.3 所有启用供应商
echo [6.3] GET /api/suppliers/all - 所有启用供应商
curl -s -X GET "%BASE%/api/suppliers/all" -H "%AUTH%"
echo.

REM 6.5 更新供应商
echo [6.5] PUT /api/suppliers/1 - 更新供应商
curl -s -X PUT "%BASE%/api/suppliers/1" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"金禾农资供应链(更新)\",\"contactPerson\":\"王经理\",\"phone\":\"0571-99999999\",\"address\":\"浙江省杭州市西湖区\",\"creditLevel\":4,\"remark\":\"长期合作伙伴\"}"
echo.

echo.
echo ============================================
echo  模块7: 客户管理 /api/customers
echo ============================================
echo.

REM 7.3 创建客户
echo [7.3] POST /api/customers - 创建客户
curl -s -X POST %BASE%/api/customers -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"王农户\",\"phone\":\"13800000010\",\"address\":\"浙江省某村\",\"preference\":\"水稻种子、化肥\",\"remark\":\"老客户\"}"
echo.

REM 7.1 客户列表
echo [7.1] GET /api/customers - 客户列表
curl -s -X GET "%BASE%/api/customers?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

REM 7.2 客户详情
echo [7.2] GET /api/customers/1 - 客户详情
curl -s -X GET "%BASE%/api/customers/1" -H "%AUTH%"
echo.

REM 7.2 全部客户
echo [7.2] GET /api/customers/all - 全部客户
curl -s -X GET "%BASE%/api/customers/all" -H "%AUTH%"
echo.

REM 7.2 客户订单
echo [7.2] GET /api/customers/1/orders - 客户订单
curl -s -X GET "%BASE%/api/customers/1/orders?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

REM 7.4 更新客户
echo [7.4] PUT /api/customers/1 - 更新客户
curl -s -X PUT "%BASE%/api/customers/1" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"name\":\"王农户(更新)\",\"phone\":\"13800000011\",\"address\":\"浙江省新村\",\"preference\":\"水稻种子、化肥、农药\"}"
echo.

echo.
echo ============================================
echo  模块8: 进货单 /api/purchase-orders
echo ============================================
echo.

REM 8.2 创建进货单
echo [8.2] POST /api/purchase-orders - 创建进货单
curl -s -X POST %BASE%/api/purchase-orders -H "Content-Type: application/json" -H "%AUTH%" -d "{\"supplierId\":1,\"orderDate\":\"2026-05-23\",\"remark\":\"首批进货测试\",\"items\":[{\"productId\":1,\"quantity\":100,\"unitPrice\":80.00}]}"
echo.

REM 8.1 进货单列表
echo [8.1] GET /api/purchase-orders - 进货单列表
curl -s -X GET "%BASE%/api/purchase-orders?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

REM 8.3 确认入库
echo [8.3] PUT /api/purchase-orders/1/confirm - 确认入库
curl -s -X PUT "%BASE%/api/purchase-orders/1/confirm" -H "%AUTH%"
echo.

REM 8.5 进货统计
echo [8.5] GET /api/purchase-orders/stats - 进货统计
curl -s -X GET "%BASE%/api/purchase-orders/stats?startDate=2026-01-01&endDate=2026-12-31" -H "%AUTH%"
echo.

echo.
echo ============================================
echo  模块9: 销售单 /api/sales-orders
echo ============================================
echo.

REM 9.2 创建销售单
echo [9.2] POST /api/sales-orders - 创建销售单
curl -s -X POST %BASE%/api/sales-orders -H "Content-Type: application/json" -H "%AUTH%" -d "{\"customerId\":1,\"paymentMethod\":1,\"orderDate\":\"2026-05-23\",\"remark\":\"测试销售\",\"items\":[{\"productId\":1,\"quantity\":5,\"unitPrice\":100.00}]}"
echo.

REM 9.1 销售单列表
echo [9.1] GET /api/sales-orders - 销售单列表
curl -s -X GET "%BASE%/api/sales-orders?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

REM 9.4 获取打印数据
echo [9.4] GET /api/sales-orders/1/print - 获取打印数据
curl -s -X GET "%BASE%/api/sales-orders/1/print" -H "%AUTH%"
echo.

REM 9.3 退货处理
echo [9.3] PUT /api/sales-orders/1/return - 退货处理
curl -s -X PUT "%BASE%/api/sales-orders/1/return" -H "Content-Type: application/json" -H "%AUTH%" -d "{\"items\":[{\"orderItemId\":1,\"returnQuantity\":1}],\"reason\":\"质量问题退货\"}"
echo.

echo.
echo ============================================
echo  模块10: 库存管理 /api/inventory
echo ============================================
echo.

REM 10.1 库存列表
echo [10.1] GET /api/inventory - 库存列表
curl -s -X GET "%BASE%/api/inventory?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

REM 10.2 产品库存详情
echo [10.2] GET /api/inventory/1 - 产品库存详情
curl -s -X GET "%BASE%/api/inventory/1" -H "%AUTH%"
echo.

REM 10.3 库存预警列表
echo [10.3] GET /api/inventory/warnings - 库存预警列表
curl -s -X GET "%BASE%/api/inventory/warnings" -H "%AUTH%"
echo.

REM 10.4 库存预警数量
echo [10.4] GET /api/inventory/warnings/count - 库存预警数量
curl -s -X GET "%BASE%/api/inventory/warnings/count" -H "%AUTH%"
echo.

REM 10.5 库存盘点
echo [10.5] POST /api/inventory/check - 库存盘点
curl -s -X POST %BASE%/api/inventory/check -H "Content-Type: application/json" -H "%AUTH%" -d "{\"productId\":1,\"actualStock\":93,\"remark\":\"盘点差异测试\"}"
echo.

REM 10.6 报损处理
echo [10.6] POST /api/inventory/damage - 报损处理
curl -s -X POST %BASE%/api/inventory/damage -H "Content-Type: application/json" -H "%AUTH%" -d "{\"productId\":1,\"damageQuantity\":2,\"reason\":\"运输破损\",\"remark\":\"测试报损\"}"
echo.

REM 10.7 库存变动日志
echo [10.7] GET /api/inventory/logs - 库存变动日志
curl -s -X GET "%BASE%/api/inventory/logs?pageNum=1&pageSize=10" -H "%AUTH%"
echo.

echo.
echo ============================================
echo  模块11: 仪表盘 /api/dashboard
echo ============================================
echo.

REM 11.2 销售趋势
echo [11.2] GET /api/dashboard/sales-trend - 销售趋势
curl -s -X GET "%BASE%/api/dashboard/sales-trend" -H "%AUTH%"
echo.

REM 11.3 分类销售占比
echo [11.3] GET /api/dashboard/category-sales - 分类销售占比
curl -s -X GET "%BASE%/api/dashboard/category-sales" -H "%AUTH%"
echo.

REM 11.4 预警列表
echo [11.4] GET /api/dashboard/warnings - 预警列表
curl -s -X GET "%BASE%/api/dashboard/warnings" -H "%AUTH%"
echo.

echo.
echo ============================================
echo  模块12: 销售统计 /api/sales-stats
echo ============================================
echo.

REM 12.1 销售概况
echo [12.1] GET /api/sales-stats/summary - 销售概况
curl -s -X GET "%BASE%/api/sales-stats/summary" -H "%AUTH%"
echo.

REM 12.2 每日统计
echo [12.2] GET /api/sales-stats/daily - 每日统计
curl -s -X GET "%BASE%/api/sales-stats/daily?year=2026&month=5" -H "%AUTH%"
echo.

REM 12.3 月度统计
echo [12.3] GET /api/sales-stats/monthly - 月度统计
curl -s -X GET "%BASE%/api/sales-stats/monthly?year=2026" -H "%AUTH%"
echo.

REM 12.4 年度统计
echo [12.4] GET /api/sales-stats/yearly - 年度统计
curl -s -X GET "%BASE%/api/sales-stats/yearly" -H "%AUTH%"
echo.

REM 12.5 产品销售排行
echo [12.5] GET /api/sales-stats/ranking - 产品销售排行
curl -s -X GET "%BASE%/api/sales-stats/ranking?startDate=2026-01-01&endDate=2026-12-31&limit=10" -H "%AUTH%"
echo.

echo.
echo ============================================
echo  模块13: 统计报表 /api/reports
echo ============================================
echo.

REM 13.1 销售报表
echo [13.1] GET /api/reports/sales - 销售报表
curl -s -X GET "%BASE%/api/reports/sales?startDate=2026-01-01&endDate=2026-12-31" -H "%AUTH%"
echo.

REM 13.2 进货报表
echo [13.2] GET /api/reports/purchase - 进货报表
curl -s -X GET "%BASE%/api/reports/purchase?startDate=2026-01-01&endDate=2026-12-31" -H "%AUTH%"
echo.

REM 13.3 库存报表
echo [13.3] GET /api/reports/inventory - 库存报表
curl -s -X GET "%BASE%/api/reports/inventory" -H "%AUTH%"
echo.

REM 13.4 利润报表
echo [13.4] GET /api/reports/profit - 利润报表
curl -s -X GET "%BASE%/api/reports/profit?startDate=2026-01-01&endDate=2026-12-31" -H "%AUTH%"
echo.

REM 13.5 客户报表
echo [13.5] GET /api/reports/customer - 客户报表
curl -s -X GET "%BASE%/api/reports/customer?startDate=2026-01-01&endDate=2026-12-31" -H "%AUTH%"
echo.

echo.
echo ============================================
echo  清理: 删除测试创建的分类和单位
echo ============================================
echo.

echo [清理] DELETE /api/categories/5 - 删除测试分类
curl -s -X DELETE "%BASE%/api/categories/5" -H "%AUTH%"
echo.

echo [清理] DELETE /api/users/2 - 删除测试经销商用户 (需要先查ID)
curl -s -X GET "%BASE%/api/users?pageNum=1&pageSize=20" -H "%AUTH%"
echo.

echo.
echo ============================================
echo  全量接口测试完成!
echo ============================================
echo 请检查以上输出，确认每个接口的响应是否符合预期。
echo.
pause