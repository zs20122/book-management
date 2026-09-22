<template>
    <div class="register-container">
        <el-card class="register-card">
            <h2>📚 图书管理系统</h2>
            <h3>注册新账号</h3>
            <el-form :model="form" :rules="rules" ref="formRef">
                <el-form-item prop="username">
                    <el-input v-model="form.username" placeholder="用户名（3-20位）" prefix-icon="User" size="large" />
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" type="password" placeholder="密码（6-20位）" prefix-icon="Lock" size="large" />
                </el-form-item>
                <el-form-item prop="email">
                    <el-input v-model="form.email" placeholder="邮箱" prefix-icon="Message" size="large" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleRegister" :loading="loading" style="width:100%" size="large">
                        注册
                    </el-button>
                </el-form-item>
                <div style="text-align:center; color:#999">
                    已有账号？<el-button type="primary" link @click="$router.push('/login')">去登录</el-button>
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
    password: '',
    email: ''
})

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度为3-20位', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ]
}

const handleRegister = async () => {
    await formRef.value?.validate()
    loading.value = true
    try {
        const res = await auth.register(form)
        if (res.code === 200) {
            ElMessage.success('注册成功，请登录')
            router.push('/login')
        } else {
            ElMessage.error(res.message || '注册失败')
        }
    } catch (error) {
        // 错误已在拦截器中处理
    } finally {
        loading.value = false
    }
}
</script>

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 80vh;
}
.register-card {
    width: 420px;
    padding: 30px;
}
.register-card h2 {
    text-align: center;
    margin-bottom: 10px;
}
.register-card h3 {
    text-align: center;
    margin-bottom: 25px;
    color: #666;
    font-weight: normal;
}
</style>