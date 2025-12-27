# 智能垃圾分类系统

一个结合数据库与大模型的垃圾分类查询工具，支持普通用户查询与管理员审核。

## 核心流程
- 普通用户：输入垃圾名称 → 获取分类结果（数据库（优先）/AI 补全）--需手动点击AI生成
- 管理员：审核 AI 生成的建议 → 通过后自动入库，增强规则库

## 如何运行
# 后端为backend文件夹
cd backend
需要修改application.properties中数据库连接
运行 BackendApplication 即可

# 前端为frontend文件夹
cd frontend
npm install
npm run dev

## SQL
为SQL文件夹

## 登录
明文密码：admin123 --实际数据库为加密存储
账号：admin/recycler/customer/volunteer --仅为测试账号实际注册账号为：xxx@xxxx.com

## 忘记密码
仅作为本地运行未连接相应真实邮箱
直接输入邮箱账号点击获取密钥即可立即更改密码