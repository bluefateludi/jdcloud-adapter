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

        <!-- 状态选择 -->
        <a-form-item label="状态" name="status">
          <a-select
            v-model:value="formState.status"
            placeholder="请选择状态"
          >
            <a-select-option value="启用">启用</a-select-option>
            <a-select-option value="停用">停用</a-select-option>
          </a-select>
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
  phone: '',
  status: '启用'  // 默认值为"启用"
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
  ],
  status: [
    { required: true, message: '请选择状态' }
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
      formState.status = '启用'  // 重置为默认值
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