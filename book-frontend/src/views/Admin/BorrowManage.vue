<template>
    <div class="admin-container">
        <h2>📋 借阅审核</h2>
        
        <el-table :data="borrowList" style="width:100%" v-loading="loading" stripe>
            <el-table-column prop="id" label="记录ID" width="80" />
            <el-table-column label="用户" width="120">
                <template #default="{ row }">
                    {{ row.user?.username || '-' }}
                </template>
            </el-table-column>
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
            <el-table-column label="状态" width="120">
                <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)">
                        {{ getStatusText(row.status) }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
                <template #default="{ row }">
                    <el-button 
                        v-if="row.status === 'PENDING'" 
                        type="success" 
                        size="small" 
                        @click="handleApprove(row.id, true)"
                    >
                        批准
                    </el-button>
                    <el-button 
                        v-if="row.status === 'PENDING'" 
                        type="danger" 
                        size="small" 
                        @click="handleApprove(row.id, false)"
                    >
                        拒绝
                    </el-button>
                    <span v-else style="color:#999">已处理</span>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
import { borrows } from '../../api'

export default {
    data() {
        return {
            borrowList: [],
            loading: false
        }
    },
    mounted() {
        this.loadBorrows()
    },
    methods: {
        async loadBorrows() {
            this.loading = true
            try {
                const res = await borrows.all()
                if (res.code === 200) {
                    this.borrowList = res.data || []
                }
            } catch (error) {
                console.error('加载失败:', error)
                this.$message.error('加载借阅记录失败')
            } finally {
                this.loading = false
            }
        },
        async handleApprove(id, approved) {
            try {
                const res = await borrows.approve(id, approved)
                if (res.code === 200) {
                    this.$message.success(approved ? '已批准' : '已拒绝')
                    this.loadBorrows()
                } else {
                    this.$message.error(res.message || '操作失败')
                }
            } catch (error) {
                console.error('审核失败:', error)
                this.$message.error('操作失败')
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
                'APPROVED': 'success',
                'BORROWING': 'primary',
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
.admin-container {
    background: white;
    padding: 20px;
    border-radius: 8px;
}
</style>