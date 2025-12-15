<script setup>
import { ref } from 'vue'
import Register from './views/Register.vue'
import DataQuery from './views/DataQuery.vue'

// 当前显示的页面
const currentView = ref('register')  // 默认显示题目1

// 菜单选中的keys（必须是响应式数组变量）
const selectedKeys = ref(['register'])

// 菜单项
const menuItems = [
  { key: 'register', label: '题目1：用户注册' },
  { key: 'dataQuery', label: '题目3：数据查询' }
]

// 菜单选择处理
const handleMenuSelect = ({ key }) => {
  currentView.value = key
  selectedKeys.value = [key]
}
</script>

<template>
  <div id="app">
    <!-- 顶部导航菜单 -->
    <div class="nav-header">
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        @select="handleMenuSelect"
      >
        <a-menu-item
          v-for="item in menuItems"
          :key="item.key"
        >
          {{ item.label }}
        </a-menu-item>
      </a-menu>
    </div>

    <!-- 内容区 -->
    <div class="content">
      <Register v-if="currentView === 'register'" />
      <DataQuery v-if="currentView === 'dataQuery'" />
    </div>
  </div>
</template>

<style>
#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.nav-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.content {
  /* 内容区域样式由各个页面自己控制 */
}
</style>
