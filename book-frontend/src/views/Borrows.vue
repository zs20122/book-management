<template>
    <div class="borrows-container">
        <h2>📋 我的借阅记录</h2>
        
        <el-table :data="borrowList" style="width:100%" v-loading="loading" stripe>
            <el-table-column prop="id" label="记录ID" width="80" />
            <el-table-column label="书名" width="200">
                <template #default="{ row }">
                    {{ row.book?.title || '-' }}
                </template>
            </el-table-column>
            <el-table-column label="借阅日期" width="180">
                <template #default="{ row }">
                    {{ formatDate(row.borrowDate) }}
                </template>
            </el-table-column>
            <el-table-column label="应还日期" width="180">
                <template #default="{ row }">
                    {{ formatDate(row.dueDate) }}
                </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">
                        {{ getStatusText(row.status) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="罚金" width="100">
                <template #default="{ row }">
                    {{ row.fine && row.fine > 0 ? row.fine + '元' : '-' }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
                <template #default="{ row }">
                    <el-button 
                        v-if="row.status === 'BORROWING'" 
                        type="success" 
                        size="small" 
                        @click="handleReturn(row.id)"
                    >
                        归还
                    </el-button>
                    <el-button 
                        v-if="row.status === 'BORROWING'" 
                        type="warning" 
                        size="small" 
                        @click="handleRenew(row.id)"
                    >
                        续借
                    </el-button>
                    <span v-else style="color:#999">-</span>
                </template>
            </el-table-column>
        </el-table>
        
        <el-empty v-if="!loading && borrowList.length === 0" description="暂无借阅记录" />
    </div>
</template>

<script>
import { borrows } from '../api'

export default {
    data() {
        return {
            loading: false,
            borrowList: []
        }
    },
    mounted() {
        this.loadBorrows()
    },
    methods: {
        async loadBorrows() {
            this.loading = true
            try {
                const res = await borrows.list()
                console.log('借阅记录:', res)
                if (res.code === 200) {
                    this.borrowList = res.data || []
                } else {
                    this.$message.error(res.message || '加载借阅记录失败')
                }
            } catch (error) {
                console.error('加载借阅记录失败:', error)
                this.$message.error('加载借阅记录失败')
            } finally {
                this.loading = false
            }
        },
        async handleReturn(id) {
            try {
                const res = await borrows.returnBook(id)
                if (res.code === 200) {
                    this.$message.success('归还成功')
                    this.loadBorrows()
                } else {
                    this.$message.error(res.message || '归还失败')
                }
            } catch (error) {
                console.error('归还错误:', error)
                this.$message.error('归还失败，请稍后重试')
            }
        },
        async handleRenew(id) {
            try {
                const res = await borrows.renew(id)
                if (res.code === 200) {
                    this.$message.success('续借成功')
                    this.loadBorrows()
                } else {
                    this.$message.error(res.message || '续借失败')
                }
            } catch (error) {
                console.error('续借错误:', error)
                this.$message.error('续借失败，请稍后重试')
            }
        },
        formatDate(dateStr) {
            if (!dateStr) return '-'
            try {
                const date = new Date(dateStr)
                if (isNaN(date.getTime())) return '-'
                return date.toLocaleString('zh-CN')
            } catch {
                return '-'
            }
        },
        getStatusText(status) {
            const map = {
                'PENDING': '待审核',
                'APPROVED': '已批准',
                'BORROWING': '借阅中',
                'RETURNED': '已归还',
                'REJECTED': '已拒绝',
                'ARCHIVED': '已归档'
            }
            return map[status] || status
        },
        getStatusType(status) {
            const map = {
                'PENDING': 'warning',
                'APPROVED': 'info',
                'BORROWING': 'success',
                'RETURNED': 'info',
                'REJECTED': 'danger',
                'ARCHIVED': 'info'
            }
            return map[status] || ''
        }
    }
}
</script>

<style scoped>
.borrows-container {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
</style>