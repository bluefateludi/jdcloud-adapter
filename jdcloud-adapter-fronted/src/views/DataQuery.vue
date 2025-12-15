<template>
  <div class="data-query-container">
    <a-card title="题目3：数据查询展示">
      <a-button type="primary" @click="loadData" :loading="loading">
        刷新
      </a-button>

      <a-table
        :columns="columns"
        :data-source="tableData"
        :loading="loading"
        :pagination="false"
        row-key="key"
        style="margin-top: 16px"
      />

      <!-- 分页组件 -->
      <div style="margin-top: 16px; text-align: right">
        <a-pagination
          v-model:current="pagination.current"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :show-size-changer="true"
          :show-quick-jumper="true"
          :show-total="total => `共 ${total} 条数据`"
          @change="handlePageChange"
          @showSizeChange="handlePageChange"
        />
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { queryData } from '../api/data'

// 表格列定义
const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '手机号', dataIndex: 'phone', key: 'phone' },
  { title: '部门', dataIndex: 'department', key: 'department' },
  { title: '状态', dataIndex: 'status', key: 'status' }
]

const tableData = ref([])
const loading = ref(false)

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0
})

// 解析子表单数据
const parseSubFormData = (record) => {
  const testInfo = record.test_info?.[0]
  if (!testInfo) {
    return { name: '-', phone: '-', department: '-', status: '-' }
  }
  return {
    name: testInfo.name || '-',
    phone: testInfo.phone || '-',
    department: testInfo.department || '-',
    status: testInfo.status || '-'
  }
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const response = await queryData(pagination.current, pagination.pageSize)
    if (response.code === 200) {
      const rawList = response.data.list || []
      tableData.value = rawList.map((record, index) => ({
        key: index,
        ...parseSubFormData(record)
      }))
      pagination.total = response.data.total || 0
      message.success(`加载成功，共 ${pagination.total} 条数据`)
    } else {
      message.error(response.message || '查询失败')
    }
  } catch (error) {
    message.error(error.message || '查询失败')
  } finally {
    loading.value = false
  }
}

// 分页变化处理
const handlePageChange = (page, pageSize) => {
  pagination.current = page
  pagination.pageSize = pageSize
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.data-query-container {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}
</style>
