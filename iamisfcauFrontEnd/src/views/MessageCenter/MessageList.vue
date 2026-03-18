<template>
  <div class="message-list">
    <div class="header">消息中心</div>

    <div
      v-for="item in sortedConversations"
      :key="item.id"
      class="message-item"
    >
      <router-link
        :to="`/home/messageCenter/${item.id}`"
        class="message-link"
        :class="{ active: isActive(item.id) }"
      >
        <img
          :src="getAvatar(item.avatar)"
          class="avatar"
          @error="onImgError"
        />

        <div class="content">
          <div class="name">{{ item.name }}</div>
          <div class="last-message">
            {{ item.lastMessage || '暂无消息' }}
          </div>
        </div>

        <div class="time">
          {{ formatDate(item.updatedAt) }}
        </div>
      </router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  getMessageConversations,
  type MessageConversationDto
} from '@/api/messageCenter'

const router = useRouter()
const route = useRoute()

const conversations = ref<MessageConversationDto[]>([])

const loadConversations = async () => {
  conversations.value = await getMessageConversations()
}

/* 按时间倒序 */
const sortedConversations = computed(() => {
  return [...conversations.value].sort(
    (a, b) =>
      new Date(b.updatedAt).getTime() -
      new Date(a.updatedAt).getTime()
  )
})

onMounted(async () => {
  await loadConversations()

  const firstConversation = sortedConversations.value[0]
  if (!route.params.id && firstConversation) {
    router.replace(`/home/messageCenter/${firstConversation.id}`)
  }
})

const isActive = (id: string) => {
  return route.params.id === id
}

const getAvatar = (avatar?: string) => {
  return avatar || '/AnonymousAvatar.png'
}

const onImgError = (e: Event) => {
  ;(e.target as HTMLImageElement).src = '/AnonymousAvatar.png'
}

const formatDate = (time: string) => {
  if (!time) return ''
  return time.slice(5, 10)
}
</script>

<style scoped>
.message-list {
  width: 220px; 
  border-right: 1px solid #e5e7eb;
  overflow-y: auto;
  background: #fff;
}

.header {
  height: 56px;
  padding: 0 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
  color: #1f2937;
}

.message-item {
  margin-top: 5px;
  padding: 4px 8px;
}

.message-link {
  display: flex;
  gap: 10px;
  padding: 8px;
  border-radius: 8px;
  text-decoration: none;
  color: inherit;
  transition: background-color 0.15s ease;
}

.message-link:hover {
  background-color: #eef4ff;
}

.message-link.active {
  background-color: #dbeafe;
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  object-fit: cover;
}

.content {
  flex: 1;
  min-width: 0;
}

.name {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 2px;
  color: #111827;
}

.last-message {
  font-size: 13px;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.time {
  font-size: 12px;
  color: #9ca3af;
  margin-left: 4px;
}
</style>
