# 后端API接口文档

## 项目信息
- **项目名称**: 简道云二开认证考核系统
- **后端地址**: http://localhost:8081/api
- **CORS配置**: 允许 http://localhost:5173

---

## 一、用户管理接口 (题目1)

### 1. 用户注册
- **接口**: `POST /api/user/register`
- **用途**: 注册新用户，同时调用简道云通讯录API和数据API
- **Content-Type**: application/json

**请求参数**:
```json
{
  "username": "test123",
  "phone": "13800138000"
}
```

**参数说明**:
- username: 用户名，2-20位数字字母，全局唯一
- phone: 手机号，11位数字，全局唯一

**响应格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "test123",
    "phone": "13800138000",
    "status": "启用",
    "createTime": "2025-12-14T10:00:00"
  }
}
```

### 2. 根据用户名查询
- **接口**: `GET /api/user/username/{username}`
- **用途**: 检查用户名是否已存在

**响应格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "test123",
    "phone": "13800138000",
    "status": "启用"
  }
}
```

### 3. 根据手机号查询
- **接口**: `GET /api/user/phone/{phone}`
- **用途**: 检查手机号是否已存在

---

## 二、简道云数据接口

### 1. 创建简道云数据
- **接口**: `POST /api/jiandaoyun/data/create`
- **用途**: 向简道云表单插入数据
- **Content-Type**: application/json

**请求参数**:
```json
{
  "app_id": "59f1c8df83a8409c9a8a3d4405a1d12e",
  "entry_id": "60643542586c4a7f9b3485cd492ba087",
  "transaction_id": "uuid",
  "data": {
    "widget名称": {
      "value": "字段值"
    }
  }
}
```

**响应格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "data_id": "619b3486c6dd4c1a89024831edc0f3e5",
    "transaction_id": "uuid"
  }
}
```

---

## 三、简道云API集成说明

### 1. 通讯录API
**创建企业成员**:
- API端点: `/v5/api/contacts/add_member`
- 功能: 在简道云企业通讯录中创建新成员
- 请求参数:
  ```json
  {
    "username": "用户名",
    "mobile": "手机号"
  }
  ```

**删除企业成员**:
- API端点: `/v5/api/contacts/delete_member`
- 功能: 从简道云企业通讯录中删除成员
- 请求参数:
  ```json
  {
    "user_id": "成员ID"
  }
  ```

### 2. 数据API
**创建表单数据**:
- API端点: `/v5/api/app/59f1c8df83a8409c9a8a3d4405a1d12e/entry/60643542586c4a7f9b3485cd492ba087/data`
- 功能: 向指定表单创建一条数据记录
- 认证方式: Bearer Token (使用AppKey)
- 数据格式: V5 API格式，每个字段需要包装为`{"value": "值"}`

---

## 四、配置信息

### application.properties 关键配置
```properties
# 服务器端口
server.port=8081
# 上下文路径
server.servlet.context-path=/api

# 简道云API配置
jiandaoyun.api.baseUrl=https://api.jiandaoyun.com
jiandaoyun.api.appKey=7UDthmj3tirdoPzvm5JtRmOw1kDYzQr5
jiandaoyun.api.appId=59f1c8df83a8409c9a8a3d4405a1d12e
jiandaoyun.api.appSecret=2AHfcM62Gy7Fw2AXwBEP75iFzQ7h3KMX
jiandaoyun.api.formIdUserBase=60643542586c4a7f9b3485cd492ba087

# API路径配置
jiandaoyun.api.memberCreate=/v5/api/contacts/add_member
jiandaoyun.api.memberDelete=/v5/api/contacts/delete_member
jiandaoyun.api.dataCreate=/v5/api/app/{appId}/entry/{entryId}/data
```

---

## 五、错误处理

### 通用错误响应格式
```json
{
  "code": 400/500,
  "message": "错误描述",
  "data": null
}
```

### 常见错误码
- **400**: 请求参数错误（如用户名已存在、手机号格式错误）
- **500**: 服务器内部错误

### 特殊错误消息
- "输入内容不合法，请重新输入": 前端需要显示给用户的友好提示
- "用户名已存在": 用户名重复
- "手机号已存在": 手机号重复

---

## 六、并发处理

- 使用 `@Transactional` 注解确保数据一致性
- 数据库字段有唯一索引，防止重复数据
- 并发创建用户时，先检查唯一性，再插入

---

## 七、Webhook预留接口（题目2）

待实现:
- **接口**: `POST /api/webhook/jiandaoyun`
- **用途**: 接收简道云表单变更通知
- **功能**: 监听用户状态变更，自动停用时删除通讯录成员

---

## 八、测试用例

### 用户注册测试
```bash
# 正常注册
curl -X POST http://localhost:8081/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test123","phone":"13800138000"}'

# 重复用户名
curl -X POST http://localhost:8081/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test123","phone":"13900139000"}'

# 重复手机号
curl -X POST http://localhost:8081/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test456","phone":"13800138000"}'
```

---

**文档创建时间**: 2025-12-14
**版本**: v1.0