<template>
    <div class="ai-chat-container">
        <div class="chat-header">
            <h2>🤖 AI 图书助手</h2>
            <el-button size="small" @click="clearChat">清空对话</el-button>
        </div>

        <div class="chat-box" ref="chatBox">
            <div
                v-for="(msg, index) in messages"
                :key="index"
                :class="['message', msg.role]"
            >
                <div class="avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
                <div class="bubble">{{ msg.content }}</div>
            </div>
            <div v-if="loading" class="message ai">
                <div class="avatar">🤖</div>
                <div class="bubble">思考中...</div>
            </div>
        </div>

        <div class="input-area">
            <el-input
                v-model="input"
                placeholder="问点什么，比如：红楼梦还有几本？"
                @keyup.enter="send"
                :disabled="loading"
            />
            <el-button type="primary" @click="send" :loading="loading">发送</el-button>
        </div>
    </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const messages = ref([
    { role: 'ai', content: '你好！我是图书助手，可以问我关于图书的问题，比如“有哪些书”、“红楼梦还有几本”。' }
])
const input = ref('')
const loading = ref(false)
const chatBox = ref(null)

const send = async () => {
    if (!input.value.trim() || loading.value) return

    const question = input.value.trim()
    messages.value.push({ role: 'user', content: question })
    input.value = ''
    loading.value = true

    await nextTick()
    scrollToBottom()

    try {
        const res = await api.get('/api/ai/chat', { params: { message: question } })
        // 后端返回的是纯文本
        const answer = typeof res === 'string' ? res : (res?.data || 'AI 没有返回内容')
        messages.value.push({ role: 'ai', content: answer })
    } catch (error) {
        console.error('AI 请求失败:', error)
        messages.value.push({ role: 'ai', content: '抱歉，AI 服务暂时不可用，请稍后再试。' })
    } finally {
        loading.value = false
        await nextTick()
        scrollToBottom()
    }
}

const clearChat = () => {
    messages.value = [
        { role: 'ai', content: '你好！我是图书助手，可以问我关于图书的问题。' }
    ]
}

const scrollToBottom = () => {
    if (chatBox.value) {
        chatBox.value.scrollTop = chatBox.value.scrollHeight
    }
}
</script>

<style scoped>
.ai-chat-container {
    display: flex;
    flex-direction: column;
    height: calc(100vh - 100px);
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    overflow: hidden;
}

.chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #eee;
}

.chat-header h2 {
    font-size: 18px;
}

.chat-box {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    background: #f5f7fa;
}

.message {
    display: flex;
    margin-bottom: 15px;
}

.message.user {
    flex-direction: row-reverse;
}

.avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #409EFF;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    flex-shrink: 0;
}

.message.ai .avatar {
    background: #67C23A;
}

.bubble {
    max-width: 70%;
    margin: 0 10px;
    padding: 10px 14px;
    border-radius: 8px;
    background: white;
    line-height: 1.6;
    word-break: break-word;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
}

.message.user .bubble {
    background: #409EFF;
    color: white;
}

.input-area {
    display: flex;
    gap: 10px;
    padding: 15px 20px;
    border-top: 1px solid #eee;
    background: white;
}

.input-area .el-button {
    flex-shrink: 0;
}
</style>