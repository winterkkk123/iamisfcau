<template>
  <div class="conversation-list">
    <div class="header">
      <span class="title">历史对话</span>
      <button class="new-btn" @click="createConversation">+</button>
    </div>

    <div class="list">
      <div
        v-for="conv in conversations"
        :key="conv.id"
        class="item"
        :class="{ active: conv.id === activeId }"
        @click="selectConversation(conv.id)"
      >
        <div class="item-title">
          {{ conv.title || '新的对话' }}
        </div>
        <div class="item-time">
          {{ formatTime(conv.createdAt) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useAppStore } from '@/stores/app'
import { aiApi } from '@/api/ai'

const appStore = useAppStore()

const conversations = computed(() => appStore.conversations || [])
const activeId = computed(() => appStore.activeConversationId)

const selectConversation = async (id: string) => {
  const messages = await aiApi.messages(id)

  const index = appStore.conversations.findIndex((c: any) => c.id === id)
  if (index >= 0) {
    appStore.conversations[index].messages = messages
  }

  appStore.activeConversationId = id
}

const createConversation = async () => {
  const created = await aiApi.createConversation()
  appStore.conversations.unshift(created)
  appStore.activeConversationId = created.id
}

const formatTime = (time: string) => {
  const date = new Date(time)
  return `${date.getMonth() + 1}月${date.getDate()}日`
}
</script>

<style scoped>
.conversation-list {
  width: 240px;
  background: #ffffff;
  border-left: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.header {
  height: 56px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
}

.header .title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.new-btn {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: none;
  background: #4c98d4;
  color: white;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
}

.list {
  flex: 1;
  overflow-y: auto;
}

.item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.item:hover {
  background: #f5f7fa;
}

.item.active {
  background: #eef4ff;
}

.item-title {
  font-size: 14px;
  color: #222;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-time {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}
</style>