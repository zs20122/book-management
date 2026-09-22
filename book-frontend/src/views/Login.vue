<template>
    <div class="login-container">
        <el-card class="login-card">
            <h2>📚 图书管理系统</h2>
            <h3>用户登录</h3>
            <el-form :model="form" :rules="rules" ref="formRef">
                <el-form-item prop="username">
                    <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" @keyup.enter="handleLogin" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%" size="large">
                        登录
                    </el-button>
                </el-form-item>
                <div style="text-align:center; color:#999">
                    还没有账号？<el-button type="primary" link @click="$router.push('/register')">立即注册</el-button>
                </div>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { auth } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
    username: '',
    password: ''
})

const rules = {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
    await formRef.value?.validate()
    loading.value = true
    try {
        const res = await auth.login(form)
        console.log('登录响应:', res)
        
        if (res.code === 200) {
            localStorage.setItem('token', 'logged_in')
            localStorage.setItem('username', res.data.username)
            localStorage.setItem('role', res.data.role)
            localStorage.setItem('userId', res.data.id)
            ElMessage.success('登录成功')
            router.push('/books')
        } else {
            ElMessage.error(res.message || '登录失败')
        }
    } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error('登录失败，请检查网络')
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 80vh;
}
.login-card {
    width: 400px;
    padding: 30px;
}
.login-card h2 {
    text-align: center;
    margin-bottom: 10px;
}
.login-card h3 {
    text-align: center;
    margin-bottom: 25px;
    color: #666;
    font-weight: normal;
}
</style>