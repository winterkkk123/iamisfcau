<template>
  <div class="notificationDetail">
    <!-- 标题 -->
    <div class="title">
      {{ notice?.title }}
    </div>

    <!-- 正文 -->
    <div class="content">
      <pre>{{ notice?.content }}</pre>
    </div>

    <!-- 时间 -->
    <div class="time">
      {{ ymd(notice?.time) }}
    </div>
  </div>
</template>

<script lang="ts" setup name="NotificationDetail">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ymd } from '@/utils/time'
import { getNoticeById, type NoticeDto } from '@/api/notice'

const route = useRoute()
const notice = ref<NoticeDto | null>(null)

const loadNotice = async () => {
  const id = Number(route.params.id)
  if (!id) return
  notice.value = await getNoticeById(id)
}

watch(
  () => route.params.id,
  () => {
    loadNotice()
  },
  { immediate: true }
)
</script>

<style scoped>
.notificationDetail {
  width: 100%;
  min-height: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 32px 48px;
  box-sizing: border-box;
}

.title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  line-height: 1.6;
  margin-bottom: 32px;
}

.content {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
}

.content pre {
  white-space: pre-wrap;
  margin: 0;
  font-family: inherit;
}

.time {
  margin-top: 40px;
  text-align: right;
  font-size: 13px;
  color: #999;
}
</style>
