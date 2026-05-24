#!/usr/bin/env node
// -*- coding: utf-8 -*-
/**
 * 农资进销存管理系统 - 完整API联测脚本 (Node.js)
 * 覆盖所有功能模块 + 统计报表
 */

const BASE_URL = "http://localhost:8080/api";
let PASS = 0, FAIL = 0, ERRORS = [];

function log(msg, level = "INFO") {
  const prefix = { INFO: "  ✅", WARN: "  ⚠️", ERROR: "  ❌", TEST: "  🧪" };
  console.log(`${prefix[level] || "  "} ${msg}`);
}

async function test(name, func) {
  console.log(`\n${"=".repeat(60)}`);
  console.log(`  📋 测试: ${name}`);
  console.log("=".repeat(60));
  try {
    const result = await func();
    if (result) { PASS++; log("通过 ✅"); }
    else { FAIL++; ERRORS.push(name); log("失败 ❌", "ERROR"); }
  } catch (e) {
    FAIL++; ERRORS.push(`${name}: ${e.message}`);
    log(`异常: ${e.message}`, "ERROR");
  }
}

async function api(method, path, token, data) {
  const url = `${BASE_URL}${path}`;
  const opts = { method, headers: {} };
  if (token) opts.headers["Authorization"] = `Bearer ${token}`;
  if (data) {
    opts.headers["Content-Type"] = "application/json";
    opts.body = JSON.stringify(data);
  }
  const r = await fetch(url, opts);
  return r.json();
}

function apiGet(path, token, params) {
  let url = path;
  if (params) {
    const qs = new URLSearchParams(params).toString();
    url = `${path}?${qs}`;
  }
  return api("GET", url, token);
}
function apiPost(path, token, data) { return api("POST", path, token, data); }
function apiPut(path, token, data) { return api("PUT", path, token, data); }
function apiDelete(path, token) { return api("DELETE", path, token); }

// ======== 1. 登录认证 ========
async function testLogin() {
  let token;
  for (const [user, role] of [["admin", "ADMIN"], ["testdealer", "DEALER"], ["dealer_549662", "RETAILER"], ["testuser", "WAREHOUSE"]]) {
    const r = await apiPost("/auth/login", null, { username: user, password: "123456" });
    if (r.code === 200) log(`${user} 登录成功, 角色: ${r.data.roles}`);
    else throw new Error(`${user} 登录失败: ${r.message}`);
    if (user === "admin") token = r.data.token;
  }
  // 错误密码
  const r5 = await apiPost("/auth/login", null, { username: "admin", password: "wrong" });
  if (r5.code !== 200) log("错误密码正确拒绝");
  else throw new Error("错误密码应该登录失败");
  return token;
}

// ======== 2. 用户信息 ========
async function testAuthInfo(token) {
  const r = await apiGet("/auth/info", token);
  if (r.code !== 200) throw new Error(`获取用户信息失败: ${r.message}`);
  log(`当前用户: ${r.data.realName}, 角色: ${r.data.roles || r.data.roleNames}`);
  return true;
}

// ======== 3. 仪表盘 ========
async function testDashboard(token) {
  let r = await apiGet("/dashboard/overview", token);
  if (r.code !== 200) throw new Error(`仪表盘概览失败: ${r.message}`);
  const d = r.data;
  log(`总产品数: ${d.totalProducts ?? "N/A"}, 总客户数: ${d.totalCustomers ?? "N/A"}, 总供应商数: ${d.totalSuppliers ?? "N/A"}`);
  log(`今日销售额: ${d.todaySales ?? "N/A"}, 本月销售额: ${d.monthSales ?? "N/A"}`);
  log(`库存预警数: ${d.warningCount ?? "N/A"}`);

  r = await apiGet("/dashboard/sales-trend", token);
  if (r.code !== 200) throw new Error(`销售趋势失败: ${r.message}`);
  log(`销售趋势数据: ${(r.data.dates || []).length} 个数据点`);

  r = await apiGet("/dashboard/category-sales", token);
  if (r.code !== 200) throw new Error(`分类销售失败: ${r.message}`);
  log(`分类销售数据: ${(r.data || []).length} 个分类`);

  r = await apiGet("/dashboard/warnings", token);
  if (r.code !== 200) throw new Error(`库存预警失败: ${r.message}`);
  log(`库存预警产品: ${(r.data || []).length} 个`);
  return true;
}

// ======== 4. 用户管理 ========
async function testUserManagement(token) {
  const r = await apiGet("/users", token, { page: 1, pageSize: 10 });
  if (r.code !== 200) throw new Error(`用户列表失败: ${r.message}`);
  log(`用户总数: ${r.data.total}`);
  (r.data.records || []).forEach(u => log(`  用户: ${u.username} (${u.realName}) - ${u.roleNames || ""}`));
  return true;
}

// ======== 5. 分类管理 ========
async function testCategoryManagement(token) {
  const r = await apiGet("/categories", token);
  if (r.code !== 200) throw new Error(`分类列表失败: ${r.message}`);
  (r.data || []).forEach(c => log(`  分类: ${c.name} - ${c.description || ""}`));
  return (r.data || []).length > 0;
}

// ======== 6. 单位管理 ========
async function testUnitManagement(token) {
  const r = await apiGet("/units", token);
  if (r.code !== 200) throw new Error(`单位列表失败: ${r.message}`);
  (r.data || []).forEach(u => log(`  单位: ${u.name} (${u.abbreviation || ""})`));
  return (r.data || []).length > 0;
}

// ======== 7. 农资产品管理 ========
async function testProductManagement(token) {
  let r = await apiGet("/products", token, { page: 1, pageSize: 10 });
  if (r.code !== 200) throw new Error(`产品列表失败: ${r.message}`);
  log(`产品总数: ${r.data.total}`);
  (r.data.records || []).forEach(p => log(`  产品: ${p.name} - 进价:${p.purchasePrice} 售价:${p.sellingPrice}`));

  r = await apiGet("/products/category/2", token);
  if (r.code !== 200) throw new Error(`按分类查询失败: ${r.message}`);
  log(`化肥分类产品数: ${(r.data || []).length}`);

  r = await apiGet("/products/1", token);
  if (r.code !== 200) throw new Error(`产品详情失败: ${r.message}`);
  log(`产品详情: ${r.data.name}`);
  return true;
}

// ======== 8. 供应商管理 ========
async function testSupplierManagement(token) {
  let r = await apiGet("/suppliers", token, { page: 1, pageSize: 10 });
  if (r.code !== 200) throw new Error(`供应商列表失败: ${r.message}`);
  log(`供应商总数: ${r.data.total}`);
  (r.data.records || []).forEach(s => log(`  供应商: ${s.name} - 联系人:${s.contactPerson || ""} 信用等级:${s.creditLevel || ""}`));

  r = await apiGet("/suppliers/all", token);
  if (r.code !== 200) throw new Error(`全部启用供应商失败: ${r.message}`);
  log(`启用供应商数: ${(r.data || []).length}`);
  return true;
}

// ======== 9. 客户管理 ========
async function testCustomerManagement(token) {
  const r = await apiGet("/customers", token, { page: 1, pageSize: 10 });
  if (r.code !== 200) throw new Error(`客户列表失败: ${r.message}`);
  log(`客户总数: ${r.data.total}`);
  (r.data.records || []).forEach(c => log(`  客户: ${c.name} - 电话:${c.phone || ""}`));
  return true;
}

// ======== 10. 进货管理 ========
async function testPurchaseManagement(token) {
  const r = await apiGet("/purchase-orders", token, { page: 1, pageSize: 10 });
  if (r.code !== 200) throw new Error(`进货单列表失败: ${r.message}`);
  log(`进货单总数: ${r.data.total}`);
  const sMap = { 0: "待确认", 1: "已入库", 2: "已取消" };
  (r.data.list || r.data.records || []).forEach(po => log(`  进货单: ${po.orderNo} - 金额:${po.totalAmount} 状态:${sMap[po.status] || "未知"}`));

  if (r.data && (r.data.list || r.data.records) && (r.data.list || r.data.records).length > 0) {
    const firstId = (r.data.list || r.data.records)[0].id;
    const r2 = await apiGet(`/purchase-orders/${firstId}`, token);
    if (r2.code === 200) log(`进货单详情: ${r2.data.orderNo}, 明细数: ${(r2.data.items || []).length}`);
  }
  return true;
}

// ======== 11. 销售管理 ========
async function testSalesManagement(token) {
  const r = await apiGet("/sales-orders", token, { page: 1, pageSize: 10 });
  if (r.code !== 200) throw new Error(`销售单列表失败: ${r.message}`);
  log(`销售单总数: ${r.data.total}`);
  const sMap = { 1: "已完成", 2: "已退货", 3: "部分退货" };
  const pMap = { 1: "现金", 2: "微信", 3: "支付宝", 4: "赊账" };
  (r.data.list || r.data.records || []).forEach(so => log(`  销售单: ${so.orderNo} - 金额:${so.totalAmount} 付款:${pMap[so.paymentMethod] || ""} 状态:${sMap[so.status] || ""}`));

  if (r.data && (r.data.list || r.data.records) && (r.data.list || r.data.records).length > 0) {
    const firstId = (r.data.list || r.data.records)[0].id;
    const r2 = await apiGet(`/sales-orders/${firstId}`, token);
    if (r2.code === 200) log(`销售单详情: ${r2.data.orderNo}, 明细数: ${(r2.data.items || []).length}`);
  }
  return true;
}

// ======== 12. 库存管理 ========
async function testInventoryManagement(token) {
  let r = await apiGet("/inventory", token, { page: 1, pageSize: 10 });
  if (r.code !== 200) throw new Error(`库存列表失败: ${r.message}`);
  log(`库存记录总数: ${r.data.total}`);
  (r.data.records || []).forEach(inv => log(`  产品:${inv.productName || inv.name || "N/A"} 库存:${inv.currentStock}`));

  r = await apiGet("/inventory/warnings", token);
  if (r.code !== 200) throw new Error(`库存预警失败: ${r.message}`);
  log(`预警产品数: ${(r.data || []).length}`);

  r = await apiGet("/inventory/warnings/count", token);
  if (r.code !== 200) throw new Error(`预警计数失败: ${r.message}`);
  log(`预警数量: ${r.data}`);

  r = await apiGet("/inventory/logs", token, { page: 1, pageSize: 5 });
  if (r.code !== 200) throw new Error(`库存日志失败: ${r.message}`);
  log(`库存日志总数: ${r.data.total}`);
  (r.data.records || []).slice(0, 3).forEach(l => log(`  日志: ${l.changeType} 数量:${l.changeQuantity} 产品:${l.productName || ""}`));
  return true;
}

// ======== 13. 统计报表 ========
async function testReports(token) {
  // 默认按月统计当前年
  let r = await apiGet("/reports/sales", token, { type: "monthly", year: 2026 });
  if (r.code !== 200) throw new Error(`销售报表失败: ${r.message}`);
  const totalSales = (r.data || []).reduce((s, i) => s + (i.amount || 0), 0);
  log(`销售报表(2026年月度)数据条数: ${(r.data || []).length}, 总金额: ${totalSales}`);
  (r.data || []).forEach(i => log(`  ${i.date || ""} 金额:${i.amount || 0} 笔数:${i.count || 0}`));

  r = await apiGet("/reports/purchase", token, { type: "monthly", year: 2026 });
  if (r.code !== 200) throw new Error(`进货报表失败: ${r.message}`);
  const totalPurchase = (r.data || []).reduce((s, i) => s + (i.amount || 0), 0);
  log(`进货报表(2026年月度)数据条数: ${(r.data || []).length}, 总金额: ${totalPurchase}`);
  (r.data || []).forEach(i => log(`  ${i.date || ""} 金额:${i.amount || 0} 笔数:${i.count || 0}`));

  r = await apiGet("/reports/inventory", token);
  if (r.code !== 200) throw new Error(`库存报表失败: ${r.message}`);
  log(`库存报表: ${JSON.stringify(r.data).substring(0, 200)}`);

  r = await apiGet("/reports/profit", token, { startDate: "2025-12-01", endDate: "2026-06-30" });
  if (r.code !== 200) throw new Error(`利润报表失败: ${r.message}`);
  log(`利润报表数据条数: ${(r.data || []).length}`);
  (r.data || []).slice(0, 5).forEach(i => log(`  ${i.date || i.name || ""} 毛利:${i.grossProfit || i.totalProfit || 0}`));

  return true;
}

// ======== 14. 销售统计 ========
async function testSalesStats(token) {
  let r = await apiGet("/sales-stats/summary", token);
  if (r.code !== 200) throw new Error(`销售概况失败: ${r.message}`);
  const d = r.data;
  log(`今日销售额: ${d.todaySales || 0}, 今日订单: ${d.todayOrders || 0}`);
  log(`本月销售额: ${d.monthSales || 0}, 本月订单: ${d.monthOrders || 0}`);
  log(`本年销售额: ${d.yearSales || 0}, 本年订单: ${d.yearOrders || 0}`);

  r = await apiGet("/sales-stats/daily", token, { year: 2026, month: 5 });
  if (r.code !== 200) throw new Error(`日统计失败: ${r.message}`);
  log(`5月每日数据条数: ${(r.data || []).length}`);

  r = await apiGet("/sales-stats/monthly", token, { year: 2026 });
  if (r.code !== 200) throw new Error(`月统计失败: ${r.message}`);
  log(`2026年月度数据条数: ${(r.data || []).length}`);
  (r.data || []).forEach(i => log(`  ${i.date || i.name || ""} 月销售额: ${i.totalAmount || 0}`));

  r = await apiGet("/sales-stats/yearly", token);
  if (r.code !== 200) throw new Error(`年统计失败: ${r.message}`);
  log(`年度数据条数: ${(r.data || []).length}`);

  r = await apiGet("/sales-stats/ranking", token, { startDate: "2025-12-01", endDate: "2026-06-30" });
  if (r.code !== 200) throw new Error(`排行榜失败: ${r.message}`);
  log(`排行榜数据: ${(r.data || []).length} 条`);
  (r.data || []).slice(0, 5).forEach(i => log(`  排名:${i.rank || i.ranking || ""} 产品:${i.productName || ""} 销量:${i.totalQuantity || i.quantity || ""}`));
  return true;
}

// ======== 主流程 ========
async function main() {
  console.log(`\n${"=".repeat(60)}`);
  console.log(`  🌾 农资进销存管理系统 - 全功能API联测`);
  console.log(`  📅 时间: ${new Date().toLocaleString("zh-CN")}`);
  console.log(`  🌐 服务: ${BASE_URL}`);
  console.log("=".repeat(60));

  let token = null;
  // 登录
  await test("1. 登录认证测试", async () => { token = await testLogin(); return true; });
  if (!token) { console.log("\n❌ Token获取失败"); return; }

  await test("2. 用户信息查询", async () => { await testAuthInfo(token); return true; });
  await test("3. 仪表盘数据", async () => { await testDashboard(token); return true; });
  await test("4. 用户管理", async () => { await testUserManagement(token); return true; });
  await test("5. 分类管理", async () => { await testCategoryManagement(token); return true; });
  await test("6. 单位管理", async () => { await testUnitManagement(token); return true; });
  await test("7. 农资产品管理", async () => { await testProductManagement(token); return true; });
  await test("8. 供应商管理", async () => { await testSupplierManagement(token); return true; });
  await test("9. 客户管理", async () => { await testCustomerManagement(token); return true; });
  await test("10. 进货管理", async () => { await testPurchaseManagement(token); return true; });
  await test("11. 销售管理", async () => { await testSalesManagement(token); return true; });
  await test("12. 库存管理", async () => { await testInventoryManagement(token); return true; });
  await test("13. 统计报表", async () => { await testReports(token); return true; });
  await test("14. 销售统计", async () => { await testSalesStats(token); return true; });

  console.log(`\n${"=".repeat(60)}`);
  console.log(`  📊 测试结果汇总`);
  console.log(`  ✅ 通过: ${PASS}`);
  console.log(`  ❌ 失败: ${FAIL}`);
  console.log(`  📈 总计: ${PASS + FAIL}`);
  console.log(`  📋 通过率: ${(PASS / (PASS + FAIL) * 100).toFixed(1)}%`);
  if (ERRORS.length) {
    console.log(`\n  ❌ 失败项:`);
    ERRORS.forEach(e => console.log(`    - ${e}`));
  }
  console.log("=".repeat(60));
}

main().catch(console.error);