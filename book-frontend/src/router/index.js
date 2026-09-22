import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Books from '../views/Books.vue'

const routes = [
    { path: '/', redirect: '/books' },
    { path: '/login', name: 'Login', component: Login },
    { path: '/register', name: 'Register', component: Register },
    { path: '/books', name: 'Books', component: Books },
    {
        path: '/borrows',
        name: 'Borrows',
        component: () => import('../views/Borrows.vue')
    },
    {
        path: '/ai-chat',
        name: 'AiChat',
        component: () => import('../views/AiChat.vue')
    },
    {
        path: '/admin/books',
        name: 'AdminBooks',
        component: () => import('../views/Admin/BookManage.vue'),
        meta: { requiresAdmin: true }
    },
    {
        path: '/admin/borrows',
        name: 'AdminBorrows',
        component: () => import('../views/Admin/BorrowManage.vue'),
        meta: { requiresAdmin: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from) => {
    const token = localStorage.getItem('token')
    const role = localStorage.getItem('role')

    if (to.meta.requiresAdmin) {
        if (!token) {
            ElMessage.warning('请先登录')
            return '/login'
        }
        if (role !== 'ADMIN') {
            ElMessage.error('需要管理员权限')
            return '/books'
        }
        return true
    }

    if (to.path === '/login' || to.path === '/register') {
        if (token) {
            return '/books'
        }
        return true
    }

    if (!token && to.path !== '/login' && to.path !== '/register') {
        return '/login'
    }
    return true
})

export default router