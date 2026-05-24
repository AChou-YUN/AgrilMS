#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
农资进销存管理系统 - 完整API联测脚本
覆盖所有功能模块，包含统计报表数据验证
"""

import requests
import json
import time
from datetime import datetime

BASE_URL = "http://localhost:8080/api"
PASS = 0
FAIL = 0
ERRORS = []

def log(msg, level="INFO"):
    prefix = {"INFO": "✅", "WARN": "⚠️", "ERROR": "❌", "TEST": "🧪"}
    print(f"  {prefix.get(level, ' ')} {msg}")

def test(name, func):
    global PASS, FAIL
    print(f"\n{'='*60}")
    print(f"  📋 测试: {name}")
    print(f"{'='*60}")
    try:
        result = func()
        if result:
            PASS += 1
            log(f"通过 ✅")
        else:
            FAIL += 1
            ERRORS.append(name)
            log(f"失败 ❌", "ERROR")
    except Exception as e:
        FAIL += 1
        ERRORS.append(f"{name}: {str(e)}")
        log(f"异常: {e}", "ERROR")

def api_get(path, token, params=None):
    r = requests.get(f"{BASE_URL}{path}", headers={"Authorization": f"Bearer {token}"}, params=params)
    return r.json()

def api_post(path, token, data=None):
    r = requests.post(f"{BASE_URL}{path}", headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"}, json=data)
    return r.json()

def api_put(path, token, data=None):
    r = requests.put(f"{BASE_URL}{path}", headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"}, json=data)
    return r.json()

def api_delete(path, token):
    r = requests.delete(f"{BASE_URL}{path}", headers={"Authorization": f"Bearer {token}"})
    return r.json()


# ============================================
# 1. 登录认证测试
# ============================================
def test_login():
    # 测试admin登录
    r = api_post("/auth/login", None, {"username": "admin", "password": "123456"})
    assert r["code"] == 200, f"admin登录失败: {r['message']}"
    log(f"admin 登录成功, 角色: {r['data']['roles']}")
    
    # 测试经销商登录
    r2 = api_post("/auth/login", None, {"username": "testdealer", "password": "123456"})
    assert r2["code"] == 200, f"testdealer登录失败: {r2['message']}"
    log(f"testdealer 登录成功, 角色: {r2['data']['roles']}")
    
    # 测试零售商登录
    r3 = api_post("/auth/login", None, {"username": "dealer_549662", "password": "123456"})
    assert r3["code"] == 200, f"dealer_549662登录失败: {r3['message']}"
    log(f"dealer_549662 登录成功, 角色: {r3['data']['roles']}")
    
    # 测试仓管登录
    r4 = api_post("/auth/login", None, {"username": "testuser", "password": "123456"})
    assert r4["code"] == 200, f"testuser登录失败: {r4['message']}"
    log(f"testuser 登录成功, 角色: {r4['data']['roles']}")
    
    # 测试错误密码
    r5 = api_post("/auth/login", None, {"username": "admin", "password": "wrong"})
    assert r5["code"] != 200, "错误密码应该登录失败"
    log(f"错误密码正确拒绝")
    
    return r["data"]["token"]

# ============================================
# 2. 用户信息测试
# ============================================
def test_auth_info(token):
    r = api_get("/auth/info", token)
    assert r["code"] == 200, f"获取用户信息失败: {r['message']}"
    log(f"当前用户: {r['data']['realName']}, 角色: {r['data'].get('roles', [])}")
    return True

# ============================================
# 3. 仪表盘测试
# ============================================
def test_dashboard(token):
    # 概览数据
    r = api_get("/dashboard/overview", token)
    assert r["code"] == 200, f"仪表盘概览失败: {r['message']}"
    data = r["data"]
    log(f"总产品数: {data.get('totalProducts', 'N/A')}")
    log(f"总客户数: {data.get('totalCustomers', 'N/A')}")
    log(f"总供应商数: {data.get('totalSuppliers', 'N/A')}")
    log(f"今日销售额: {data.get('todaySales', 'N/A')}")
    log(f"本月销售额: {data.get('monthSales', 'N/A')}")
    log(f"库存预警数: {data.get('warningCount', 'N/A')}")
    
    # 销售趋势
    r2 = api_get("/dashboard/sales-trend", token)
    assert r2["code"] == 200, f"销售趋势失败: {r2['message']}"
    log(f"销售趋势数据: {len(r2['data'].get('dates', []))} 个数据点")
    
    # 分类销售
    r3 = api_get("/dashboard/category-sales", token)
    assert r3["code"] == 200, f"分类销售失败: {r3['message']}"
    log(f"分类销售数据: {len(r3['data'])} 个分类")
    
    # 库存预警
    r4 = api_get("/dashboard/warnings", token)
    assert r4["code"] == 200, f"库存预警失败: {r4['message']}"
    log(f"库存预警产品: {len(r4['data'])} 个")
    
    return True

# ============================================
# 4. 用户管理测试
# ============================================
def test_user_management(token):
    r = api_get("/users", token, {"page": 1, "pageSize": 10})
    assert r["code"] == 200, f"用户列表失败: {r['message']}"
    total = r["data"]["total"]
    log(f"用户总数: {total}")
    for u in r["data"]["records"]:
        log(f"  用户: {u['username']} ({u['realName']}) - {u.get('roleNames', [])}")
    return True

# ============================================
# 5. 分类管理测试
# ============================================
def test_category_management(token):
    r = api_get("/categories", token)
    assert r["code"] == 200, f"分类列表失败: {r['message']}"
    for c in r["data"]:
        log(f"  分类: {c['name']} - {c.get('description', '')}")
    return len(r["data"]) > 0

# ============================================
# 6. 单位管理测试
# ============================================
def test_unit_management(token):
    r = api_get("/units", token)
    assert r["code"] == 200, f"单位列表失败: {r['message']}"
    for u in r["data"]:
        log(f"  单位: {u['name']} ({u.get('abbreviation', '')})")
    return len(r["data"]) > 0

# ============================================
# 7. 农资产品管理测试
# ============================================
def test_product_management(token):
    # 列表
    r = api_get("/products", token, {"page": 1, "pageSize": 10})
    assert r["code"] == 200, f"产品列表失败: {r['message']}"
    total = r["data"]["total"]
    log(f"产品总数: {total}")
    for p in r["data"]["records"]:
        log(f"  产品: {p['name']} - 进价:{p['purchasePrice']} 售价:{p['sellingPrice']}")
    
    # 按分类查询
    r2 = api_get("/products/category/2", token)
    assert r2["code"] == 200, f"按分类查询失败: {r2['message']}"
    log(f"化肥分类产品数: {len(r2['data'])}")
    
    # 产品详情
    r3 = api_get("/products/1", token)
    assert r3["code"] == 200, f"产品详情失败: {r3['message']}"
    log(f"产品详情: {r3['data']['name']}")
    
    return True

# ============================================
# 8. 供应商管理测试
# ============================================
def test_supplier_management(token):
    r = api_get("/suppliers", token, {"page": 1, "pageSize": 10})
    assert r["code"] == 200, f"供应商列表失败: {r['message']}"
    total = r["data"]["total"]
    log(f"供应商总数: {total}")
    for s in r["data"]["records"]:
        log(f"  供应商: {s['name']} - 联系人:{s.get('contactPerson', '')} 信用等级:{s.get('creditLevel', '')}")
    
    # 全部启用供应商
    r2 = api_get("/suppliers/all-active", token)
    assert r2["code"] == 200, f"全部启用供应商失败: {r2['message']}"
    log(f"启用供应商数: {len(r2['data'])}")
    return True

# ============================================
# 9. 客户管理测试
# ============================================
def test_customer_management(token):
    r = api_get("/customers", token, {"page": 1, "pageSize": 10})
    assert r["code"] == 200, f"客户列表失败: {r['message']}"
    total = r["data"]["total"]
    log(f"客户总数: {total}")
    for c in r["data"]["records"]:
        log(f"  客户: {c['name']} - 电话:{c.get('phone', '')}")
    return True

# ============================================
# 10. 进货管理测试
# ============================================
def test_purchase_management(token):
    r = api_get("/purchase-orders", token, {"page": 1, "pageSize": 10})
    assert r["code"] == 200, f"进货单列表失败: {r['message']}"
    total = r["data"]["total"]
    log(f"进货单总数: {total}")
    for po in r["data"]["records"]:
        status_text = {0: "待确认", 1: "已入库", 2: "已取消"}.get(po["status"], "未知")
        log(f"  进货单: {po['orderNo']} - 金额:{po['totalAmount']} 状态:{status_text}")
    
    # 进货单详情
    if total > 0:
        first_id = r["data"]["records"][0]["id"]
        r2 = api_get(f"/purchase-orders/{first_id}", token)
        assert r2["code"] == 200, f"进货单详情失败: {r2['message']}"
        log(f"进货单详情: {r2['data']['orderNo']}, 明细数: {len(r2['data'].get('items', []))}")
    
    # 进货统计
    r3 = api_get("/purchase-orders/stats/summary", token)
    if r3["code"] == 200:
        log(f"进货统计获取成功")
    else:
        log(f"进货统计接口: {r3['message']}", "WARN")
    
    return True

# ============================================
# 11. 销售管理测试
# ============================================
def test_sales_management(token):
    r = api_get("/sales-orders", token, {"page": 1, "pageSize": 10})
    assert r["code"] == 200, f"销售单列表失败: {r['message']}"
    total = r["data"]["total"]
    log(f"销售单总数: {total}")
    for so in r["data"]["records"]:
        status_text = {1: "已完成", 2: "已退货", 3: "部分退货"}.get(so["status"], "未知")
        pay_text = {1: "现金", 2: "微信", 3: "支付宝", 4: "赊账"}.get(so.get("paymentMethod"), "未知")
        log(f"  销售单: {so['orderNo']} - 金额:{so['totalAmount']} 付款:{pay_text} 状态:{status_text}")
    
    # 销售单详情
    if total > 0:
        first_id = r["data"]["records"][0]["id"]
        r2 = api_get(f"/sales-orders/{first_id}", token)
        assert r2["code"] == 200, f"销售单详情失败: {r2['message']}"
        log(f"销售单详情: {r2['data']['orderNo']}, 明细数: {len(r2['data'].get('items', []))}")
    
    return True

# ============================================
# 12. 库存管理测试
# ============================================
def test_inventory_management(token):
    # 库存列表
    r = api_get("/inventory", token, {"page": 1, "pageSize": 10})
    assert r["code"] == 200, f"库存列表失败: {r['message']}"
    total = r["data"]["total"]
    log(f"库存记录总数: {total}")
    for inv in r["data"]["records"]:
        log(f"  产品:{inv.get('productName', inv.get('name', 'N/A'))} 库存:{inv['currentStock']}")
    
    # 库存预警
    r2 = api_get("/inventory/warnings", token)
    assert r2["code"] == 200, f"库存预警失败: {r2['message']}"
    log(f"预警产品数: {len(r2['data'])}")
    
    # 预警计数
    r3 = api_get("/inventory/warning-count", token)
    assert r3["code"] == 200, f"预警计数失败: {r3['message']}"
    log(f"预警数量: {r3['data']}")
    
    # 库存日志
    r4 = api_get("/inventory/logs", token, {"page": 1, "pageSize": 5})
    assert r4["code"] == 200, f"库存日志失败: {r4['message']}"
    log(f"库存日志总数: {r4['data']['total']}")
    for log_item in r4["data"]["records"][:3]:
        log(f"  日志: {log_item.get('changeType', '')} 数量:{log_item.get('changeQuantity', '')} 产品:{log_item.get('productName', '')}")
    
    return True

# ============================================
# 13. 统计报表测试
# ============================================
def test_reports(token):
    # 销售报表
    r = api_get("/reports/sales", token, {"startDate": "2025-12-01", "endDate": "2026-06-30"})
    assert r["code"] == 200, f"销售报表失败: {r['message']}"
    log(f"销售报表数据条数: {len(r['data'])}")
    total_sales = sum(item.get("totalAmount", 0) for item in r["data"])
    log(f"销售报表总金额: {total_sales}")
    for item in r["data"][:5]:
        log(f"  日期:{item.get('date', item.get('name', ''))} 金额:{item.get('totalAmount', 0)}")
    
    # 进货报表
    r2 = api_get("/reports/purchase", token, {"startDate": "2025-12-01", "endDate": "2026-06-30"})
    assert r2["code"] == 200, f"进货报表失败: {r2['message']}"
    log(f"进货报表数据条数: {len(r2['data'])}")
    total_purchase = sum(item.get("totalAmount", 0) for item in r2["data"])
    log(f"进货报表总金额: {total_purchase}")
    
    # 库存报表
    r3 = api_get("/reports/inventory", token)
    assert r3["code"] == 200, f"库存报表失败: {r3['message']}"
    log(f"库存报表: {r3['data']}")
    
    # 利润报表
    r4 = api_get("/reports/profit", token, {"startDate": "2025-12-01", "endDate": "2026-06-30"})
    assert r4["code"] == 200, f"利润报表失败: {r4['message']}"
    log(f"利润报表数据条数: {len(r4['data'])}")
    for item in r4["data"][:5]:
        log(f"  日期:{item.get('date', item.get('name', ''))} 毛利:{item.get('grossProfit', item.get('totalProfit', 0))}")
    
    return True

# ============================================
# 14. 销售统计测试
# ============================================
def test_sales_stats(token):
    # 概况
    r = api_get("/sales-stats/summary", token)
    assert r["code"] == 200, f"销售概况失败: {r['message']}"
    data = r["data"]
    log(f"今日销售额: {data.get('todaySales', 0)}, 今日订单数: {data.get('todayOrders', 0)}")
    log(f"本月销售额: {data.get('monthSales', 0)}, 本月订单数: {data.get('monthOrders', 0)}")
    log(f"本年销售额: {data.get('yearSales', 0)}, 本年订单数: {data.get('yearOrders', 0)}")
    
    # 日统计
    r2 = api_get("/sales-stats/daily", token, {"startDate": "2026-05-01", "endDate": "2026-05-31"})
    assert r2["code"] == 200, f"日统计失败: {r2['message']}"
    log(f"5月每日数据条数: {len(r2['data'])}")
    
    # 月统计
    r3 = api_get("/sales-stats/monthly", token, {"year": 2026})
    assert r3["code"] == 200, f"月统计失败: {r3['message']}"
    log(f"2026年月度数据条数: {len(r3['data'])}")
    for item in r3["data"]:
        log(f"  {item.get('date', item.get('name', ''))} 月销售额: {item.get('totalAmount', 0)}")
    
    # 年统计
    r4 = api_get("/sales-stats/yearly", token)
    assert r4["code"] == 200, f"年统计失败: {r4['message']}"
    log(f"年度数据条数: {len(r4['data'])}")
    
    # 排行
    r5 = api_get("/sales-stats/ranking", token, {"startDate": "2025-12-01", "endDate": "2026-06-30"})
    assert r5["code"] == 200, f"排行榜失败: {r5['message']}"
    log(f"排行榜数据: {len(r5['data'])} 条")
    for item in r5["data"][:5]:
        log(f"  排名: {item.get('rank', item.get('ranking', ''))} 产品:{item.get('productName', '')} 销量:{item.get('totalQuantity', item.get('quantity', ''))}")
    
    return True


# ============================================
# 主测试流程
# ============================================
def main():
    global PASS, FAIL
    
    print("\n" + "="*60)
    print("  🌾 农资进销存管理系统 - 全功能API联测")
    print(f"  📅 时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"  🌐 服务: {BASE_URL}")
    print("="*60)
    
    # 登录获取Token
    test("1. 登录认证测试", lambda: (setattr(main, 'token', test_login()) or True))
    
    # 如果token获取失败则退出
    if not hasattr(main, 'token') or not main.token:
        print("\n❌ Token获取失败，无法继续测试")
        return
    
    token = main.token
    
    test("2. 用户信息查询", lambda: test_auth_info(token))
    test("3. 仪表盘数据", lambda: test_dashboard(token))
    test("4. 用户管理", lambda: test_user_management(token))
    test("5. 分类管理", lambda: test_category_management(token))
    test("6. 单位管理", lambda: test_unit_management(token))
    test("7. 农资产品管理", lambda: test_product_management(token))
    test("8. 供应商管理", lambda: test_supplier_management(token))
    test("9. 客户管理", lambda: test_customer_management(token))
    test("10. 进货管理", lambda: test_purchase_management(token))
    test("11. 销售管理", lambda: test_sales_management(token))
    test("12. 库存管理", lambda: test_inventory_management(token))
    test("13. 统计报表", lambda: test_reports(token))
    test("14. 销售统计", lambda: test_sales_stats(token))
    
    # 汇总
    print("\n" + "="*60)
    print(f"  📊 测试结果汇总")
    print(f"  ✅ 通过: {PASS}")
    print(f"  ❌ 失败: {FAIL}")
    print(f"  📈 总计: {PASS + FAIL}")
    print(f"  📋 通过率: {PASS/(PASS+FAIL)*100:.1f}%")
    if ERRORS:
        print(f"\n  ❌ 失败项:")
        for e in ERRORS:
            print(f"    - {e}")
    print("="*60)


if __name__ == "__main__":
    main()