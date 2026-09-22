import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

api.interceptors.request.use(
    config => {
        console.log('发送请求:', config.url, config.method)
        return config
    },
    error => Promise.reject(error)
)

api.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        if (error.response) {
            const { status, data } = error.response
            ElMessage.error(data?.message || '请求失败')
        } else {
            ElMessage.error('网络异常，请检查后端是否启动')
        }
        return Promise.reject(error)
    }
)

export const auth = {
    register: (data) => api.post('/api/auth/register', data),
    login: (data) => api.post('/api/auth/login', data)
}

export const books = {
    list: (params) => api.get('/api/books', { params }),
    detail: (id) => api.get(`/api/books/${id}`),
    create: (data) => api.post('/api/books', data),
    update: (id, data) => api.put(`/api/books/${id}`, data),
    delete: (id) => api.delete(`/api/books/${id}`)
}

export const borrows = {
    // 借阅申请 - 需要传递 userId
    apply: (data) => {
        const userId = localStorage.getItem('userId')
        return api.post(`/api/borrows?userId=${userId}`, data)
    },
    // 我的借阅 - 需要传递 userId
    list: () => {
        const userId = localStorage.getItem('userId')
        return api.get(`/api/borrows/me?userId=${userId}`)
    },
    // 所有借阅记录（管理员）
    all: () => api.get('/api/borrows/all'),
    // 审核
    approve: (id, approved) => api.put(`/api/borrows/${id}/approve?approved=${approved}`),
    // 归还 - 需要传递 userId
    returnBook: (id) => {
        const userId = localStorage.getItem('userId')
        return api.put(`/api/borrows/${id}/return?userId=${userId}`)
    },
    // 续借 - 需要传递 userId
    renew: (id) => {
        const userId = localStorage.getItem('userId')
        return api.put(`/api/borrows/${id}/renew?userId=${userId}`)
    }
}

export default api