import axios from 'axios'

// 配置 axios
const api = axios.create({
  baseURL: 'http://localhost:8081',
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