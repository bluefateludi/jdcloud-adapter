import axios from 'axios'

// 配置 axios
const api = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000
})

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    const msg = error.response?.data?.message || error.message || '请求失败'
    return Promise.reject(new Error(msg))
  }
)

/**
 * 查询数据（支持分页）
 * @param {number} page - 当前页
 * @param {number} pageSize - 每页大小
 */
export const queryData = (page = 1, pageSize = 20) => {
  return api.post('/api/data/query', {
    formId: '693ebc497f13ce039a36b621',
    page,
    pageSize,
    filters: {}
  })
}
