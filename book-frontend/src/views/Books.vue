<template>
    <div class="books-container">
        <div class="header-actions">
            <h2>📖 图书列表</h2>
            <div class="search-area">
                <el-input 
                    v-model="keyword" 
                    placeholder="输入书名搜索" 
                    style="width:250px" 
                    clearable 
                    @clear="handleSearch" 
                    @keyup.enter="handleSearch"
                />
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button v-if="keyword" @click="resetSearch">重置</el-button>
            </div>
        </div>

        <el-table :data="bookList" style="width:100%" v-loading="loading" stripe>
            <el-table-column prop="id" label="ID" width="70" />
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="isbn" label="ISBN" width="160" />
            <el-table-column prop="publisher" label="出版社" width="150" />
            <el-table-column prop="stock" label="库存" width="70" align="center">
                <template #default="{ row }">
                    <el-tag :type="row.stock > 0 ? 'success' : 'danger'" size="small">
                        {{ row.stock }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
                <template #default="{ row }">
                    <el-button type="primary" size="small" @click="handleBorrow(row)" :disabled="row.stock <= 0">
                        借阅
                    </el-button>
                    <el-button type="info" size="small" @click="handleDetail(row)">详情</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadBooks"
            style="margin-top:20px;justify-content:center"
        />
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { books, borrows } from '../api'

const bookList = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const allBooks = ref([])

const loadBooks = async () => {
    loading.value = true
    try {
        const res = await books.list({ page: page.value - 1, size: 100 })
        console.log('=== 图书数据 ===', res)
        
        if (res.code === 200) {
            let data = res.data
            if (data && typeof data === 'object' && data.content) {
                allBooks.value = data.content
                total.value = data.totalElements || 0
            } else if (Array.isArray(data)) {
                allBooks.value = data
                total.value = data.length
            } else {
                allBooks.value = []
                total.value = 0
            }
            applySearch()
        }
    } catch (error) {
        console.error('加载失败:', error)
        ElMessage.error('加载图书失败')
    } finally {
        loading.value = false
    }
}

const applySearch = () => {
    if (!keyword.value.trim()) {
        bookList.value = allBooks.value
        return
    }
    const kw = keyword.value.trim().toLowerCase()
    bookList.value = allBooks.value.filter(book => 
        book.title.toLowerCase().includes(kw) ||
        book.author.toLowerCase().includes(kw)
    )
}

const handleSearch = () => {
    applySearch()
}

const resetSearch = () => {
    keyword.value = ''
    applySearch()
}

const handleBorrow = (row) => {
    const title = row.title || '本图书'
    ElMessageBox.confirm(
        `确定要借阅《${title}》吗？`,
        '借阅确认',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        }
    ).then(async () => {
        try {
            const res = await borrows.apply({ bookId: row.id, days: 7 })
            console.log('借阅结果:', res)
            if (res.code === 200) {
                ElMessage.success(`《${title}》借阅申请已提交，等待管理员审核`)
                loadBooks()
            } else {
                ElMessage.error(res.message || '借阅失败')
            }
        } catch (error) {
            console.error('借阅错误:', error)
            ElMessage.error('借阅失败，请稍后重试')
        }
    }).catch(() => {})
}

const handleDetail = (row) => {
    const title = row.title || '未知图书'
    const author = row.author || '未知作者'
    const isbn = row.isbn || '暂无'
    const publisher = row.publisher || '暂无'
    const stock = row.stock !== undefined ? row.stock : '未知'
    const description = row.description || '暂无简介'
    
    ElMessageBox.alert(
        `<div style="text-align:left">
            <p><strong>书名：</strong>${title}</p>
            <p><strong>作者：</strong>${author}</p>
            <p><strong>ISBN：</strong>${isbn}</p>
            <p><strong>出版社：</strong>${publisher}</p>
            <p><strong>库存：</strong>${stock} 本</p>
            <p><strong>简介：</strong>${description}</p>
        </div>`,
        `📖 《${title}》详情`,
        {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '关闭'
        }
    )
}

onMounted(() => {
    loadBooks()
})
</script>

<style scoped>
.books-container {
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}
.header-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.search-area {
    display: flex;
    gap: 10px;
    align-items: center;
}
</style>