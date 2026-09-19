import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { session } from '@/lib/session'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_ORIGIN || '/wx',
  timeout: 60000,
})

http.interceptors.request.use((config) => {
  if (session.token) config.headers.token = session.token
  return config
})

http.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data?.code === 401) {
      session.clear()
      router.replace({ name: 'login' })
      return Promise.reject(new Error(data.msg || '登录已失效'))
    }
    if (data?.code && data.code !== 200) {
      ElMessage.error(data.msg || '请求失败')
      return Promise.reject(new Error(data.msg || '请求失败'))
    }
    return data
  },
  (error) => {
    if (error.response?.status === 401) {
      session.clear()
      router.replace({ name: 'login' })
    }
    ElMessage.error(error.response?.data?.msg || '网络请求失败')
    return Promise.reject(error)
  },
)

export default http
