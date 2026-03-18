<template>
  <div class="message-detail" v-if="conversationId">
    <div class="message-body" ref="bodyRef">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        class="message-row"
        :class="msg.role"
      >
        <img
          v-if="msg.role !== 'self'"
          class="avatar"
          :src="getOtherAvatar()"
        />

        <div class="bubble">
          {{ msg.content }}
        </div>

        <img
          v-if="msg.role === 'self'"
          class="avatar"
          src="/profilePicture.jpg"
        />
      </div>
    </div>

    <div class="input-area">
      <input
        v-model="inputText"
        class="input"
        placeholder="请输入消息..."
        @keyup.enter="sendMessage"
      />
      <button class="send-btn" @click="sendMessage">
        发送
      </button>
    </div>
  </div>

  <div v-else class="empty">
    未找到该会话
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import {
  getConversationMessages,
  sendConversationMessage,
  type MessageDto
} from '@/api/messageCenter'

const route = useRoute()

const conversationId = computed(() => route.params.id as string)

const messages = ref<MessageDto[]>([])
const inputText = ref('')
const bodyRef = ref<HTMLElement | null>(null)

const loadMessages = async () => {
  if (!conversationId.value) return
  messages.value = await getConversationMessages(conversationId.value)
  scrollToBottom()
}

const getOtherAvatar = () => {
  return '/AnonymousAvatar.png'
}

const sendMessage = async () => {
  const content = inputText.value.trim()
  if (!content || !conversationId.value) return

  const res = await sendConversationMessage(conversationId.value, {
    role: 'self',
    content
  })

  messages.value.push(res)
  inputText.value = ''
  scrollToBottom()
}

const scrollToBottom = async () => {
  await nextTick()
  bodyRef.value?.scrollTo({
    top: bodyRef.value.scrollHeight,
    behavior: 'smooth'
  })
}

watch(
  () => conversationId.value,
  () => {
    loadMessages()
  },
  { immediate: true }
)
</script>

<style scoped>
.message-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

/* 消息区 */
.message-body {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

/* 单条消息 */
.message-row {
  display: flex;
  align-items: flex-start; 
  margin-bottom: 14px;
}

.message-row.system,
.message-row.other {
  justify-content: flex-start;
}

.message-row.self {
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 8px;
  margin-top: 2px;
}

.bubble {
  max-width: 62%;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.6;
  border-radius: 12px;
  word-break: break-word;
}

.message-row.system .bubble,
.message-row.other .bubble {
  background: #f2f3f5;
  color: #1f2937;
  border-top-left-radius: 4px;
}

.message-row.self .bubble {
  background: #4c98d4;
  color: #ffffff;
  border-top-right-radius: 4px;
}

.input-area {
  height: 56px;
  padding: 8px 12px;
  display: flex;
  align-items: center;
  background: #ffffff;
  border-top: 1px solid #e5e7eb;
}

.input {
  flex: 1;
  height: 36px;
  padding: 0 12px;
  border-radius: 18px;
  border: 1px solid #d1d5db;
  outline: none;
  font-size: 14px;
}

.send-btn {
  margin-left: 10px;
  padding: 6px 14px;
  background: #4c98d4;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.send-btn:hover {
  background: #3b82f6;
}

.empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}
</style>
