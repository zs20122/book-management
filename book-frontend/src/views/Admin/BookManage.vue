<template>
    <div class="admin-container">
        <h2>📚 图书管理</h2>
        
        <div class="toolbar">
            <el-button type="primary" @click="showDialog = true">添加图书</el-button>
        </div>

        <el-table :data="bookList" style="width:100%" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="isbn" label="ISBN" width="160" />
            <el-table-column prop="publisher" label="出版社" width="150" />
            <el-table-column prop="stock" label="库存" width="70" />
            <el-table-column label="操作" width="200">
                <template #default="{ row }">
                    <el-button type="primary" size="small" @click="editBook(row)">编辑</el-button>
                    <el-button type="danger" size="small" @click="deleteBook(row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog v-model="showDialog" :title="editId ? '编辑图书' : '添加图书'" width="500px">
            <el-form :model="form" label-width="80px">
                <el-form-item label="书名">
                    <el-input v-model="form.title" />
                </el-form-item>
                <el-form-item label="作者">
                    <el-input v-model="form.author" />
                </el-form-item>
                <el-form-item label="ISBN">
                    <el-input v-model="form.isbn" />
                </el-form-item>
                <el-form-item label="出版社">
                    <el-input v-model="form.publisher" />
                </el-form-item>
                <el-form-item label="库存">
                    <el-input-number v-model="form.stock" :min="0" />
                </el-form-item>
                <el-form-item label="总数量">
                    <el-input-number v-model="form.total" :min="0" />
                </el-form-item>
                <el-form-item label="简介">
                    <el-input v-model="form.description" type="textarea" :rows="3" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showDialog = false">取消</el-button>
                <el-button type="primary" @click="saveBook">保存</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
import { books } from '../../api'

export default {
    data() {
        return {
            bookList: [],
            loading: false,
            showDialog: false,
            editId: null,
            form: {
                title: '',
                author: '',
                isbn: '',
                publisher: '',
                stock: 0,
                total: 0,
                description: ''
            }
        }
    },
    mounted() {
        this.loadBooks()
    },
    methods: {
        async loadBooks() {
            this.loading = true
            try {
                const res = await books.list({ page: 0, size: 100 })
                if (res.code === 200) {
                    this.bookList = res.data || []
                }
            } catch (error) {
                console.error('加载失败:', error)
                this.$message.error('加载图书失败')
            } finally {
                this.loading = false
            }
        },
        resetForm() {
            this.form = {
                title: '',
                author: '',
                isbn: '',
                publisher: '',
                stock: 0,
                total: 0,
                description: ''
            }
            this.editId = null
        },
        editBook(row) {
            this.editId = row.id
            this.form = { ...row }
            this.showDialog = true
        },
        async saveBook() {
            try {
                let res
                if (this.editId) {
                    res = await books.update(this.editId, this.form)
                } else {
                    res = await books.create(this.form)
                }
                if (res.code === 200) {
                    this.$message.success(this.editId ? '更新成功' : '添加成功')
                    this.showDialog = false
                    this.resetForm()
                    this.loadBooks()
                } else {
                    this.$message.error(res.message || '操作失败')
                }
            } catch (error) {
                console.error('保存失败:', error)
                this.$message.error('操作失败')
            }
        },
        async deleteBook(id) {
            try {
                await this.$confirm('确定要删除这本图书吗？', '确认删除', { type: 'warning' })
                const res = await books.delete(id)
                if (res.code === 200) {
                    this.$message.success('删除成功')
                    this.loadBooks()
                } else {
                    this.$message.error(res.message || '删除失败')
                }
            } catch (error) {
                if (error !== 'cancel') {
                    console.error('删除失败:', error)
                    this.$message.error('删除失败')
                }
            }
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
.toolbar {
    margin-bottom: 20px;
}
</style>