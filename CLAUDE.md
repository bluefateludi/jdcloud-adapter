# 简道云二开认证考核项目 - 技术文档

## 先写后端在写前端
## 📖 项目概述

本项目是简道云二次开发认证考核系统,采用前后端分离架构,实现了与简道云平台的深度集成,包括API调用、Webhook回调、单点登录、数据封装、Excel自动处理等核心功能。

**项目时长:** 5-7天
**考核题目:** 1、2、3、4、7（共5题）
**总分:** 58分




---

## 🏗️ 项目架构

### 整体架构

```
jdcloud-adapter/
├── jdcloud-adapter-backend/     # 后端项目（Spring Boot 2.7.x）
├── jdcloud-adapter-fronted/     # 前端项目（Vue 3 + Vite）
├── 简道云二开认证考核.pdf        # 考核题目文档
└── CLAUDE.md                    # 本技术文档
```

### 技术选型

#### 后端技术栈（Spring Boot 2.7.x）

| 技术                   | 版本   | 用途                        |
| ---------------------- | ------ | --------------------------- |
| Java                   | 8      | 开发语言                    |
| Spring Boot            | 2.7.x  | 基础框架                    |
| Spring Web             | 2.7.x  | RESTful API                 |
| Spring Validation      | 2.7.x  | 参数校验                    |
| OkHttp3                | 4.x    | HTTP客户端（调用简道云API） |
| Gson                   | 2.13.x | JSON处理                    |
| Lombok                 | 1.18.x | 简化代码                    |
| Apache POI / EasyExcel | 3.x    | Excel处理                   |
| Hutool                 | 5.x    | 工具类库                    |
| SLF4J + Logback        | 1.7.x  | 日志框架                    |
| MySQL                  | 5.7x   | 数据库                      |
| MyBatis Plus           | 3.5.x  | ORM框架                     |

#### 前端技术栈（Vue 3 + Vite）

| 技术                      | 版本     | 用途                   |
| ------------------------- | -------- | ---------------------- |
| Node.js                   | 16.x     | 运行环境               |
| Vue.js                    | 3.4.x    | 前端框架（Composition API） |
| Vue Router                | 4.x      | 路由管理               |
| Pinia                     | 2.x      | 状态管理（替代Vuex）   |
| Axios                     | 1.6.x    | HTTP客户端             |
| Ant Design Vue            | 4.x      | UI组件库（Vue 3版本）  |
| js-cookie                 | 3.x      | Cookie操作             |
| Vite                      | 5.x      | 现代化构建工具         |
| ESLint + Prettier         | -        | 代码规范               |
| VueUse                    | 10.x     | Vue 3组合式工具库      |
| TypeScript (可选)         | 5.x      | 类型支持（学习推荐）   |

#### 第三方集成

- **简道云开放平台API**
- **简道云Webhook机制**
- **简道云单点登录（SSO）**

---

## 🎯 得分总览

| 题目            | 分值           | 时长          | 关键得分点                                    |
| --------------- | -------------- | ------------- | --------------------------------------------- |
| 题目1:API调用   | 12分           | 3h            | 基本功能6分 + 页面体验2分 + 校验1分 + 并发3分 |
| 题目2:Webhook   | 10分           | 2h            | 基本功能5分 + 并发2.5分 + 校验2.5分           |
| 题目3:数据封装  | 11分           | 3h            | 基本功能5分 + 页面1分 + mock1分 + 分页3分     |
| 题目4:单点登录  | 12分           | 8h            | 基本功能6分 + 安全3分 + 校验3分               |
| 题目7:Excel处理 | 13分           | 3h            | 基本功能6分                                   |
| **总计**  | **58分** | **19h** | -                                             |

---

## 🚀 快速开始

### 环境准备

#### 开发工具
1. JDK 8
2. Maven 3.6+
3. Node.js 16.x
4. MySQL 5.7X
5. 简道云企业账号（需要获取AppKey和AppSecret）

#### 开发环境
- **操作系统:** Windows 10
- **终端:** PowerShell
- **IDE:** IntelliJ IDEA（后端）/ WebStorm（前端）
- **数据库工具:** IDEA Database插件

---

## 📚 简道云API文档参考

- 简道云开放平台:https://www.jiandaoyun.com/open
- API文档:https://docs.jiandaoyun.com/
- Webhook配置:https://help.jiandaoyun.com/

## 部署环境


**最后更新时间:** 2025-12-12
