<template>
    <div id="app">
        <el-container>
            <el-header>
                <div class="header-content">
                    <div class="logo" @click="goTo('/books')" style="cursor:pointer">📚 图书管理系统</div>
                    <div class="nav">
                        <el-button text @click="goTo('/books')">图书列表</el-button>
                        <el-button text @click="goTo('/borrows')">我的借阅</el-button>
                        <el-button text @click="goTo('/ai-chat')">🤖 AI助手</el-button>
                        <el-button v-if="isAdmin" text @click="goTo('/admin/books')">📚 图书管理</el-button>
                        <el-button v-if="isAdmin" text @click="goTo('/admin/borrows')">📋 借阅审核</el-button>
                        <el-button v-if="isLoggedIn" type="danger" text @click="handleLogout">退出</el-button>
                        <el-button v-else type="primary" text @click="goTo('/login')">登录</el-button>
                    </div>
                </div>
            </el-header>
            <el-main>
                <router-view />
            </el-main>
        </el-container>
    </div>
</template>

<script>
export default {
    data() {
        return {
            isLoggedIn: false,
            role: ''
        }
    },
    computed: {
        isAdmin() {
            return this.role === 'ADMIN'
        }
    },
    methods: {
        checkLoginStatus() {
            const token = localStorage.getItem('token')
            this.isLoggedIn = !!token
            this.role = localStorage.getItem('role') || ''
        },
        goTo(path) {
            this.$router.push(path).catch(err => {
                console.error('跳转失败:', err)
            })
        },
        handleLogout() {
            localStorage.removeItem('token')
            localStorage.removeItem('role')
            localStorage.removeItem('username')
            localStorage.removeItem('userId')
            this.isLoggedIn = false
            this.role = ''
            this.$message.success('已退出')
            this.$router.push('/login')
        }
    },
    mounted() {
        this.checkLoginStatus()
        this.$router.afterEach(() => {
            this.checkLoginStatus()
        })
    }
}
</script>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

.el-header {
    background: #409EFF;
    color: white;
    display: flex;
    align-items: center;
    padding: 0 20px;
    height: 60px !important;
}

.header-content {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.logo {
    font-size: 22px;
    font-weight: bold;
}

.nav {
    display: flex;
    gap: 15px;
    align-items: center;
}

.nav .el-button {
    color: white !important;
}

.nav .el-button--text:hover {
    background: rgba(255, 255, 255, 0.2) !important;
}

.el-main {
    padding: 20px;
    min-height: calc(100vh - 60px);
    background: #f5f7fa;
}
</style>