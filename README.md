# 简道云二开认证考核项目

<div align="center">

![简道云](https://www.jiandaoyun.com/assets/img/logo.svg)

**基于Spring Boot + Vue 3的简道云平台适配器系统**

[![Java](https://img.shields.io/badge/Java-8-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4.x-brightgreen.svg)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-5.x-646CFF.svg)](https://vitejs.dev/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

</div>

---

## 📖 项目简介

本项目是简道云二次开发认证考核系统，采用前后端分离架构，实现了与简道云平台的深度集成，包括：

- ✅ **API调用** - 完整的简道云数据CRUD操作
- ✅ **Webhook回调** - 实时数据变更通知处理
- ✅ **单点登录(SSO)** - 与简道云账号体系集成
- ✅ **数据封装** - 优雅的数据结构转换与展示
- ✅ **Excel自动处理** - 批量数据导入导出

**考核题目:** 1、2、3、4、7（共5题） | **总分:** 58分 | **项目时长:** 5-7天

---

## 🏗️ 项目结构

```
jdcloud-adapter/
├── jdcloud-adapter-backend/     # 后端服务 (Spring Boot 2.7.x)
│   ├── src/main/java/          # Java源代码
│   ├── src/main/resources/     # 配置文件
│   └── pom.xml                 # Maven配置
│
├── jdcloud-adapter-fronted/     # 前端应用 (Vue 3 + Vite)
│   ├── src/                    # Vue源代码
│   ├── public/                 # 静态资源
│   └── package.json            # NPM配置
│
├── CLAUDE.md                   # 详细技术文档
├── README.md                   # 本文件
└── 简道云二开认证考核.pdf       # 考核题目文档
```

---

## 🚀 技术栈

### 后端技术

| 技术                 | 版本   | 说明                  |
| -------------------- | ------ | --------------------- |
| Java                 | 8      | 开发语言              |
| Spring Boot          | 2.7.x  | 基础框架              |
| MyBatis Plus         | 3.5.x  | ORM框架               |
| MySQL                | 5.7+   | 关系型数据库          |
| OkHttp3              | 4.x    | HTTP客户端            |
| Apache POI/EasyExcel | 3.x    | Excel处理             |
| Lombok               | 1.18.x | 代码简化工具          |

### 前端技术

| 技术             | 版本  | 说明                    |
| ---------------- | ----- | ----------------------- |
| Vue.js           | 3.4.x | 渐进式JavaScript框架    |
| Vite             | 5.x   | 现代化构建工具          |
| Vue Router       | 4.x   | 官方路由管理器          |
| Pinia            | 2.x   | 状态管理（替代Vuex）    |
| Ant Design Vue   | 4.x   | 企业级UI组件库          |
| Axios            | 1.6.x | HTTP客户端              |

---

## 📦 快速开始

### 环境要求

- ✅ JDK 8+
- ✅ Maven 3.6+
- ✅ Node.js 16.x+
- ✅ MySQL 5.7+
- ✅ 简道云企业账号（需获取AppKey和AppSecret）

### 后端启动

```bash
# 进入后端目录
cd jdcloud-adapter-backend

# 配置数据库（编辑 src/main/resources/application.yml）
# 配置简道云API密钥

# 安装依赖并启动
mvn clean install
mvn spring-boot:run

# 访问后端API
# http://localhost:8080
```

### 前端启动

```bash
# 进入前端目录
cd jdcloud-adapter-fronted

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 访问前端页面
# http://localhost:5173
```

---

## 🎯 功能模块

### 题目1: API调用 (12分)
- 调用简道云API获取表单数据
- 页面优雅展示数据
- 参数校验与异常处理
- 高并发场景处理

### 题目2: Webhook回调 (10分)
- 接收简道云数据变更通知
- 数据持久化存储
- 并发安全处理
- 数据完整性校验

### 题目3: 数据封装 (11分)
- 简道云数据结构转换
- Mock数据生成
- 分页查询优化
- 前端友好展示

### 题目4: 单点登录 (12分)
- OAuth 2.0流程实现
- Token安全管理
- 用户信息同步
- 会话状态维护

### 题目7: Excel自动处理 (13分)
- Excel文件解析
- 批量数据导入
- 错误数据处理
- 导入结果反馈

---

## 📚 文档资源

- [详细技术文档](CLAUDE.md) - 完整的开发指南和技术细节
- [简道云开放平台](https://www.jiandaoyun.com/open) - 官方开发文档
- [API文档](https://docs.jiandaoyun.com/) - 接口调用说明
- [Webhook配置](https://help.jiandaoyun.com/) - 回调配置指南

---

## 🔧 开发工具

- **后端开发:** IntelliJ IDEA
- **前端开发:** WebStorm / VSCode
- **数据库工具:** IDEA Database插件
- **API测试:** Postman / Apifox
- **版本控制:** Git

---

## 📝 注意事项

1. **敏感信息保护**
   - 不要提交 `.env` 文件
   - 数据库密码和API密钥使用环境变量
   - 生产配置文件应被 `.gitignore` 忽略

2. **代码规范**
   - 遵循阿里巴巴Java开发规范
   - 使用ESLint + Prettier格式化前端代码
   - 提交前进行代码审查

3. **分支管理**
   - `main` - 生产稳定分支
   - `develop` - 开发主分支
   - `feature/*` - 功能开发分支
   - `hotfix/*` - 紧急修复分支

---

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👨‍💻 作者

**项目类型:** 简道云二次开发认证考核

**最后更新:** 2025-12-12

---

<div align="center">

**如果这个项目对你有帮助，请给一个⭐Star支持一下！**

Made with ❤️ by [Your Name]

</div>
