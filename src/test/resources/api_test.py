#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""农资进销存管理系统 - 全量接口自动化测试"""

import urllib.request
import urllib.error
import json
import sys

BASE = "http://localhost:8080"
TOKEN = ""
PASS_COUNT = 0
FAIL_COUNT = 0
ERRORS = []

def api(method, path, data=None, token=True, expect_code=200):
    global PASS_COUNT, FAIL_COUNT, ERRORS
    url = BASE + path
    headers = {"Content-Type": "application/json"}
    if token and TOKEN:
        headers["Authorization"] = f"Bearer {TOKEN}"
    
    body = json.dumps(data, ensure_ascii=False).encode("utf-8") if data else None
    req = urllib.request.Request(url, data=body, headers=headers, method=method)
    
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            resp_text = resp.read().decode("utf-8")
            result = json.loads(resp_text)
            code = result.get("code", 0)
            status = "✅ PASS" if code == expect_code else f"❌ FAIL (code={code}, expect={expect_code})"
            if code == expect_code:
                PASS_COUNT += 1
            else:
                FAIL_COUNT += 1
                ERRORS.append(f"{method} {path}: code={code}, expect={expect_code}, msg={result.get('message','')}")
            return result
    except Exception as e:
        status = f"❌ ERROR: {e}"
        FAIL_COUNT += 1
        ERRORS.append(f"{method} {path}: {e}")
        return None

def header(title):
    print(f"\n{'='*50}")
    print(f"  {title}")
    print(f"{'='*50}")

def test(name, method, path, data=None, expect_code=200, token=True):
    result = api(method, path, data, token=token, expect_code=expect_code)
    status = "✅" if result and result.get("code") == expect_code else "❌"
    print(f"  {status} {method} {path}")
    if result:
        msg = result.get("message", "")
        if result.get("code") != expect_code:
            print(f"      → code={result.get('code')}, msg={msg}")
    return result

# ============================================
print("=" * 50)
print("  农资进销存管理系统 - 全量接口测试")
print("=" * 50)

# ===== 准备: 登录 =====
header("准备: 登录获取Token")
resp = api("POST", "/api/auth/login", {"username": "admin", "password": "admin123"}, token=False)
if resp and resp.get("code") == 200:
    TOKEN = resp["data"]["token"]
    print(f"  ✅ 登录成功, Token: {TOKEN[:30]}...")
else:
    print("  ❌ 登录失败，终止测试")
    sys.exit(1)

# ===== 模块1: 认证模块 =====
header("模块1: 认证模块 /api/auth")
test("1.4 修改密码", "PUT", "/api/auth/password", {"oldPassword": "admin123", "newPassword": "newpass123"})
test("1.4 改回密码", "PUT", "/api/auth/password", {"oldPassword": "newpass123", "newPassword": "admin123"})
test("1.5 修改个人信息", "PUT", "/api/auth/profile", {"realName": "管理员", "phone": "13900000001", "email": "admin@agri.com"})
test("1.6 未认证访问", "GET", "/api/auth/info", token=False, expect_code=401)

# ===== 模块2: 用户管理 =====
header("模块2: 用户管理 /api/users")
test("2.2 用户详情", "GET", "/api/users/1")
import time
unique_suffix = str(int(time.time()))[-6:]
test("2.3 创建用户", "POST", "/api/users", {"username": f"dealer_{unique_suffix}", "password": "dealer123", "realName": "测试经销商", "phone": "13900000002", "roleIds": [2]})
test("2.4 编辑用户", "PUT", "/api/users/1", {"realName": "系统管理员", "phone": "13900000001", "email": "admin@agri.com"})
test("2.6 禁用用户", "PUT", "/api/users/1/status", {"status": 0})
test("2.6 启用用户", "PUT", "/api/users/1/status", {"status": 1})
test("2.7 分配角色", "PUT", "/api/users/1/roles", {"roleIds": [1, 3]})
test("2.8 权限测试(需admin)", "GET", "/api/users")

# ===== 模块3: 分类管理 =====
header("模块3: 分类管理 /api/categories")
test("3.2 分类详情", "GET", "/api/categories/1")
cat_resp = test("3.3 创建分类", "POST", "/api/categories", {"name": f"农具{unique_suffix}", "description": "各类农用工具", "sortOrder": 5})
# 获取新创建的分类ID（从列表中找最新的）
cat_list = api("GET", "/api/categories")
new_cat_id = cat_list["data"][-1]["id"] if cat_list and cat_list.get("data") else 5
test("3.4 更新分类", "PUT", f"/api/categories/{new_cat_id}", {"name": "农用工具", "description": "各类农用工具及设备", "sortOrder": 5})
test("3.5 删除分类", "DELETE", f"/api/categories/{new_cat_id}")

# ===== 模块4: 计量单位 =====
header("模块4: 计量单位 /api/units")
test("4.2 创建单位", "POST", "/api/units", {"name": f"测试单位{unique_suffix}", "abbreviation": "测"})
# 获取新创建的单位ID
unit_list = api("GET", "/api/units")
new_unit_id = unit_list["data"][-1]["id"] if unit_list and unit_list.get("data") else 10
test("4.3 删除单位", "DELETE", f"/api/units/{new_unit_id}")

# ===== 模块5: 农资产品 =====
header("模块5: 农资产品 /api/products")
test("5.4 创建产品", "POST", "/api/products", {
    "name": "杂交水稻种子", "categoryId": 1, "specification": "50kg/袋",
    "unitId": 4, "safetyStock": 100, "manufacturer": "某种业公司",
    "purchasePrice": 80.00, "sellingPrice": 100.00, "description": "优质杂交水稻种子"
})
test("5.1 产品列表", "GET", "/api/products?pageNum=1&pageSize=10")
test("5.2 产品详情", "GET", "/api/products/1")
test("5.3 按分类查询", "GET", "/api/products/category/1")
test("5.5 更新产品", "PUT", "/api/products/1", {
    "name": "超级杂交水稻种子", "categoryId": 1, "specification": "50kg/袋",
    "unitId": 4, "safetyStock": 150, "manufacturer": "某种业公司",
    "purchasePrice": 85.00, "sellingPrice": 110.00, "description": "高产杂交水稻种子"
})
test("5.7 产品下架", "PUT", "/api/products/1/status", {"status": 0})
test("5.7 产品上架", "PUT", "/api/products/1/status", {"status": 1})
test("5.8 更新安全库存", "PUT", "/api/products/1/safety-stock", {"safetyStock": 200})

# ===== 模块6: 供应商管理 =====
header("模块6: 供应商管理 /api/suppliers")
test("6.4 创建供应商", "POST", "/api/suppliers", {
    "name": "金禾农资供应链", "contactPerson": "李经理", "phone": "0571-88888888",
    "address": "浙江省杭州市", "creditLevel": 5, "remark": "优质供应商"
})
test("6.1 供应商列表", "GET", "/api/suppliers?pageNum=1&pageSize=10")
test("6.2 供应商详情", "GET", "/api/suppliers/1")
test("6.3 所有启用供应商", "GET", "/api/suppliers/all")
test("6.5 更新供应商", "PUT", "/api/suppliers/1", {
    "name": "金禾农资供应链(更新)", "contactPerson": "王经理", "phone": "0571-99999999",
    "address": "浙江省杭州市西湖区", "creditLevel": 4, "remark": "长期合作伙伴"
})
test("6.6 供应商状态变更", "PUT", "/api/suppliers/1/status", {"status": 0})

# ===== 模块7: 客户管理 =====
header("模块7: 客户管理 /api/customers")
test("7.3 创建客户", "POST", "/api/customers", {
    "name": "王农户", "phone": "13800000010", "address": "浙江省某村",
    "preference": "水稻种子、化肥", "remark": "老客户"
})
test("7.1 客户列表", "GET", "/api/customers?pageNum=1&pageSize=10")
test("7.2 客户详情", "GET", "/api/customers/1")
test("7.2 全部客户", "GET", "/api/customers/all")
test("7.2 客户订单", "GET", "/api/customers/1/orders?pageNum=1&pageSize=10")
test("7.4 更新客户", "PUT", "/api/customers/1", {
    "name": "王农户(更新)", "phone": "13800000011", "address": "浙江省新村",
    "preference": "水稻种子、化肥、农药"
})

# 先恢复供应商状态以便后续进货单测试
api("PUT", "/api/suppliers/1/status", {"status": 1})

# ===== 模块8: 进货单 =====
header("模块8: 进货单 /api/purchase-orders")
test("8.2 创建进货单", "POST", "/api/purchase-orders", {
    "supplierId": 1, "orderDate": "2026-05-23", "remark": "首批进货测试",
    "items": [{"productId": 1, "quantity": 100, "unitPrice": 80.00}]
})
test("8.1 进货单列表", "GET", "/api/purchase-orders?pageNum=1&pageSize=10")
# 查找最新的进货单ID
po_list = api("GET", "/api/purchase-orders?pageNum=1&pageSize=1")
po_id = po_list["data"]["list"][0]["id"] if po_list and po_list.get("data") else 1
test("8.3 进货单详情", "GET", f"/api/purchase-orders/{po_id}")
test("8.3 确认入库", "PUT", f"/api/purchase-orders/{po_id}/confirm")
test("8.5 进货统计", "GET", "/api/purchase-orders/stats?startDate=2026-01-01&endDate=2026-12-31")

# ===== 模块9: 销售单 =====
header("模块9: 销售单 /api/sales-orders")
test("9.2 创建销售单", "POST", "/api/sales-orders", {
    "customerId": 1, "paymentMethod": 1, "orderDate": "2026-05-23", "remark": "测试销售",
    "items": [{"productId": 1, "quantity": 5, "unitPrice": 100.00}]
})
test("9.1 销售单列表", "GET", "/api/sales-orders?pageNum=1&pageSize=10")
# 查找最新的销售单ID
so_list = api("GET", "/api/sales-orders?pageNum=1&pageSize=1")
so_id = so_list["data"]["list"][0]["id"] if so_list and so_list.get("data") else 1
test("9.4 获取打印数据", "GET", f"/api/sales-orders/{so_id}/print")
# 获取销售明细ID用于退货
so_detail = api("GET", f"/api/sales-orders/{so_id}")
item_id = so_detail["data"]["items"][0]["id"] if so_detail and so_detail.get("data", {}).get("items") else 1
test("9.3 退货处理", "PUT", f"/api/sales-orders/{so_id}/return", {
    "items": [{"itemId": item_id, "quantity": 1}], "reason": "质量问题退货"
})

# ===== 模块10: 库存管理 =====
header("模块10: 库存管理 /api/inventory")
test("10.1 库存列表", "GET", "/api/inventory?pageNum=1&pageSize=10")
test("10.2 产品库存详情", "GET", "/api/inventory/1")
test("10.3 库存预警列表", "GET", "/api/inventory/warnings")
test("10.3 按分类预警", "GET", "/api/inventory/warnings?categoryId=1")
test("10.4 库存预警数量", "GET", "/api/inventory/warnings/count")
test("10.5 库存盘点", "POST", "/api/inventory/check", {"productId": 1, "actualStock": 93, "remark": "盘点差异测试"})
test("10.6 报损处理", "POST", "/api/inventory/damage", {"productId": 1, "quantity": 2, "reason": "运输破损"})
test("10.7 库存变动日志", "GET", "/api/inventory/logs?pageNum=1&pageSize=10")
test("10.7 按产品查询日志", "GET", "/api/inventory/logs?productId=1&changeType=PURCHASE")

# ===== 模块11: 仪表盘 =====
header("模块11: 仪表盘 /api/dashboard")
test("11.1 概览数据", "GET", "/api/dashboard/overview")
test("11.2 销售趋势", "GET", "/api/dashboard/sales-trend")
test("11.3 分类销售占比", "GET", "/api/dashboard/category-sales")
test("11.4 预警列表", "GET", "/api/dashboard/warnings")

# ===== 模块12: 销售统计 =====
header("模块12: 销售统计 /api/sales-stats")
test("12.1 销售概况", "GET", "/api/sales-stats/summary")
test("12.2 每日统计", "GET", "/api/sales-stats/daily?year=2026&month=5")
test("12.3 月度统计", "GET", "/api/sales-stats/monthly?year=2026")
test("12.4 年度统计", "GET", "/api/sales-stats/yearly")
test("12.5 产品销售排行", "GET", "/api/sales-stats/ranking?startDate=2026-01-01&endDate=2026-12-31&limit=10")

# ===== 模块13: 统计报表 =====
header("模块13: 统计报表 /api/reports")
test("13.1 销售报表", "GET", "/api/reports/sales?startDate=2026-01-01&endDate=2026-12-31")
test("13.2 进货报表", "GET", "/api/reports/purchase?startDate=2026-01-01&endDate=2026-12-31")
test("13.3 库存报表", "GET", "/api/reports/inventory")
test("13.4 利润报表", "GET", "/api/reports/profit?startDate=2026-01-01&endDate=2026-12-31")
test("13.5 客户报表", "GET", "/api/reports/customer?startDate=2026-01-01&endDate=2026-12-31")

# ===== 汇总 =====
header("测试结果汇总")
TOTAL = PASS_COUNT + FAIL_COUNT
print(f"  总计: {TOTAL} 个接口")
print(f"  通过: {PASS_COUNT} ✅")
print(f"  失败: {FAIL_COUNT} ❌")
print(f"  通过率: {PASS_COUNT/TOTAL*100:.1f}%")

if ERRORS:
    print(f"\n  失败详情:")
    for i, err in enumerate(ERRORS, 1):
        print(f"    {i}. {err}")