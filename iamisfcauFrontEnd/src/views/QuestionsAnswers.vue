<template>
  <div class="questionsAnswers">
    <div class="chat-area">
      <div class="chat-container" ref="chatContainer">
        <div
          v-for="(item, index) in activeMessages"
          :key="index"
          class="chat-item"
          :class="item.role"
        >
          <template v-if="item.role === 'assistant'">
            <img class="avatar" src="/chatgpt.png" />
            <div class="bubble assistant-bubble">
              {{ item.content }}
            </div>
          </template>

          <template v-else>
            <div class="bubble user-bubble">
              {{ item.content }}
            </div>
            <img class="avatar" :src="userAvatar" />
          </template>
        </div>
      </div>

      <div class="input-area">
        <div class="input-wrapper">
          <input
            v-model="input"
            :disabled="isSending"
            placeholder="请输入你的问题..."
            @keydown.enter="sendMessage"
          />
          <button :disabled="isSending" @click="sendMessage">
            {{ isSending ? '发送中...' : '发送' }}
          </button>
        </div>
      </div>
    </div>

    <ConversationList />
  </div>
</template>

<script lang="ts" setup name="QuestionsAnswers">
import { ref, computed, nextTick, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'
import ConversationList from './QuestionsAnswers/ConversationList.vue'
import { aiApi } from '@/api/ai'

const appStore = useAppStore()

const user = computed(() => appStore.user)
const userAvatar = computed(() => user.value.avatar || '/AnonymousAvatar.png')

const activeConversation = computed(() => {
  return appStore.conversations.find(
    (c: any) => c.id === appStore.activeConversationId
  )
})

const activeMessages = computed(() => {
  return activeConversation.value?.messages || []
})

const input = ref('')
const isSending = ref(false)
const chatContainer = ref<HTMLElement | null>(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const setConversationMessages = (id: string, messages: any[]) => {
  const index = appStore.conversations.findIndex((c: any) => c.id === id)
  if (index >= 0) {
    appStore.conversations[index].messages = messages
  }
}

const loadMessagesForConversation = async (id: string) => {
  const messages = await aiApi.messages(id)
  setConversationMessages(id, messages)
  appStore.activeConversationId = id
  await scrollToBottom()
}

const createConversation = async () => {
  const created = await aiApi.createConversation()
  appStore.conversations.unshift(created)
  appStore.activeConversationId = created.id
  await scrollToBottom()
}

const moveConversationToTop = (conversationId: string) => {
  const index = appStore.conversations.findIndex(
    (c: any) => c.id === conversationId
  )
  if (index > 0) {
    const [item] = appStore.conversations.splice(index, 1)
    appStore.conversations.unshift(item)
  }
}

onMounted(async () => {
  const list = await aiApi.conversations()

  appStore.conversations = list.map((item) => ({
    ...item,
    messages: []
  }))

  if (
    appStore.conversations.length === 0 ||
    (appStore.conversations.length === 1 &&
      appStore.conversations[0].title === '智能助手使用说明')
  ) {
    await createConversation()
    return
  }

  await loadMessagesForConversation(appStore.conversations[0].id)
})

const sendMessage = async () => {
  const content = input.value.trim()
  if (!content || !activeConversation.value || isSending.value) return

  input.value = ''
  isSending.value = true

  activeConversation.value.messages.push({
    role: 'user',
    content
  })

  activeConversation.value.messages.push({
    role: 'assistant',
    content: '正在思考中，请稍等...'
  })
  const assistantIndex = activeConversation.value.messages.length - 1

  await scrollToBottom()

  try {
    const resp = await aiApi.sendMessage({
      conversationId: activeConversation.value.id,
      message: content,
      userId: user.value.id,
      userName: user.value.name
    })

    activeConversation.value.messages[assistantIndex] = {
      role: 'assistant',
      content: resp.answer
    }

    activeConversation.value.title = resp.title
    moveConversationToTop(resp.conversationId)
  } catch (e) {
    activeConversation.value.messages[assistantIndex] = {
      role: 'assistant',
      content: '抱歉，请求失败。请检查后端、MySQL 和 Ollama 是否都已经启动。'
    }
  } finally {
    isSending.value = false
    await scrollToBottom()
  }
}
</script>

<style scoped>
.questionsAnswers {
  height: 100%;
  display: flex;
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.chat-item.assistant {
  justify-content: flex-start;
}

.chat-item.user {
  justify-content: flex-end;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin: 0 10px;
  object-fit: cover;
}

.bubble {
  max-width: 60%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  font-size: 14px;
  word-break: break-word;
}

.assistant-bubble {
  background: #f1f1f1;
  color: #333;
  border-top-left-radius: 4px;
}

.user-bubble {
  background: #4c98d4;
  color: #fff;
  border-top-right-radius: 4px;
}

.input-area {
  display: flex;
  justify-content: center;
  padding: 16px;
  background: #ffffff;
}

.input-wrapper {
  width: 50%;
  display: flex;
  align-items: center;
}

.input-wrapper input {
  flex: 1;
  padding: 12px 14px;
  border-radius: 15px;
  border: 1px solid #ddd;
  font-size: 14px;
  outline: none;
}

.input-wrapper input:focus {
  border-color: #4c98d4;
}

.input-wrapper button {
  margin-left: 12px;
  padding: 10px 18px;
  border: none;
  border-radius: 8px;
  background: #4c98d4;
  color: white;
  cursor: pointer;
}

.input-wrapper button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.input-area button:hover {
  opacity: 0.9;
}
</style>