# 前端开发快速指南

## 🎯 目标
实现用户注册页面，满足问题一的前端要求

## 📋 开发步骤（复制粘贴即可）

### 1️⃣ 安装依赖
```bash
# 在 PowerShell 中运行
cd D:\Program code\java\jdcloud-adapter\jdcloud-adapter-fronted
npm install axios@1.6.2 ant-design-vue@4.0.8 --ignore-engines
```

### 2️⃣ 配置 main.js
```javascript
// 文件位置：src/main.js
import { createApp } from 'vue'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import App from './App.vue'

const app = createApp(App)
app.use(Antd)
app.mount('#app')
```

### 3️⃣ 创建 API 接口
```javascript
// 新建文件：src/api/user.js
import axios from 'axios'

// 配置 axios
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(config => {
  console.log('发送请求：', config)
  return config
})

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log('收到响应：', response.data)
    return response.data
  },
  error => {
    const msg = error.response?.data?.message || error.message || '请求失败'
    console.error('请求错误：', msg)
    return Promise.reject(new Error(msg))
  }
)

// 注册接口
export const registerUser = (data) => {
  return api.post('/api/user/register', data)
}
```

### 4️⃣ 创建注册页面组件
```vue
<!-- 新建文件：src/views/Register.vue -->
<template>
  <div class="register-container">
    <a-card title="用户注册" style="width: 500px; margin: 100px auto">
      <a-form
        :model="formState"
        :rules="rules"
        @finish="handleSubmit"
        layout="vertical"
      >
        <!-- 用户名 -->
        <a-form-item label="用户名" name="username">
          <a-input
            v-model:value="formState.username"
            placeholder="2-20位，数字+字母"
          />
        </a-form-item>

        <!-- 手机号 -->
        <a-form-item label="手机号" name="phone">
          <a-input
            v-model:value="formState.phone"
            placeholder="11位手机号"
          />
        </a-form-item>

        <!-- 提交按钮 -->
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            :loading="loading"
            block
            size="large"
          >
            注册
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { registerUser } from '../api/user'

// 表单数据
const formState = reactive({
  username: '',
  phone: ''
})

// 加载状态
const loading = ref(false)

// 校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { pattern: /^[a-zA-Z0-9]{2,20}$/, message: '2-20位数字或字母' }
  ],
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号' }
  ]
}

// 提交表单
const handleSubmit = async (values) => {
  loading.value = true

  try {
    console.log('提交数据：', values)

    const response = await registerUser(values)

    if (response.code === 200) {
      message.success('注册成功！')
      // 清空表单
      formState.username = ''
      formState.phone = ''
    } else {
      message.error(response.message || '注册失败')
    }
  } catch (error) {
    message.error(error.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
```

### 5️⃣ 修改 App.vue
```vue
<!-- 文件位置：src/App.vue -->
<template>
  <div id="app">
    <Register />
  </div>
</template>

<script setup>
import Register from './views/Register.vue'
</script>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
```

## 🚀 启动项目
```bash
npm run dev
```

## 🔍 测试步骤

1. **启动后端**（新开一个终端）
```bash
cd D:\Program code\java\jdcloud-adapter\jdcloud-adapter-backend
mvn spring-boot:run
```

2. **测试功能**
- 打开浏览器访问 http://localhost:5173
- 输入测试数据：
  - 用户名：test123
  - 手机号：13800138000
- 点击注册按钮
- 查看控制台日志和页面提示

## 🐛 常见问题

### Q: 提示 "Network Error"
A: 检查后端是否启动在 8080 端口

### Q: 跨域问题
A: 在后端添加 CORS 配置：
```java
@CrossOrigin(origins = "http://localhost:5173")
```

### Q: 请求格式错误
A: 检查请求体格式，应该是：
```json
{
  "username": "test123",
  "phone": "13800138000"
}
```

## ✅ 完成标志

- [x] 页面正常显示
- [x] 输入框有校验
- [x] 点击按钮能发送请求
- [x] 能看到响应结果
- [x] 错误时有提示

## 📱 最终效果
- 居中的注册卡片
- 美观的输入框
- 实时表单校验
- 加载状态显示
- 成功/失败消息提示

---

创建时间：2025-12-14
版本：v1.0