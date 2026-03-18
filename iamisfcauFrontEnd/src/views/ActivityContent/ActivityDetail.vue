<template>
  <div class="activity-detail" v-if="activity">
    <h1 class="title">{{ activity.title }}</h1>
    <div class="organizer">{{ activity.organizer }}</div>

    <div class="content">
      {{ activity.content }}
    </div>

    <div class="footer">
      <span class="location">{{ activity.location }}</span>
      <span class="time">{{ formatTime(activity) }}</span>
    </div>
  </div>

  <div v-else class="activity-detail">
    <div class="empty">活动详情加载中或活动不存在</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ymd, ymdHm } from '@/utils/time'
import { apiGet } from '@/api/http'

type ActivityItem = {
  id: number
  title: string
  organizer: string
  leaderId: string
  leaderName: string
  date?: string
  time?: string
  startAt?: string
  endAt?: string
  location: string
  summary?: string
  content?: string
}

const route = useRoute()
const activity = ref<ActivityItem | null>(null)

const loadDetail = async () => {
  const id = Number(route.params.id)
  if (!id) return

  const res = await apiGet<any>(`/api/activities/${id}`)
  activity.value = {
    id: res.id,
    title: res.title,
    organizer: res.organizer,
    leaderId: res.leaderId ?? res.leader_id,
    leaderName: res.leaderName ?? res.leader_name,
    date: res.date ?? res.activityDate ?? res.activity_date,
    time: res.time ?? res.activityTime ?? res.activity_time,
    startAt: res.startAt ?? res.start_at,
    endAt: res.endAt ?? res.end_at,
    location: res.location,
    summary: res.summary ?? '',
    content: res.content ?? ''
  }
}

const formatTime = (a: ActivityItem) => {
  if (a.startAt) return ymdHm(a.startAt)
  if (a.date && a.time) return `${ymd(a.date)} ${a.time}`
  return ''
}

onMounted(() => {
  loadDetail().catch((err: any) => {
    window.alert(err?.message || '活动详情加载失败')
  })
})
</script>

<style scoped>
.activity-detail {
  width: 100%;
  min-height: 100%;
  background: #fff;
  border-radius: 8px;
  padding: 32px 48px;
  box-sizing: border-box;
}

.title {
  text-align: center;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 6px;
}

.organizer {
  text-align: center;
  font-size: 14px;
  color: #888;
  margin-bottom: 20px;
}

.content {
  font-size: 15px;
  line-height: 1.7;
  white-space: pre-line;
}

.footer {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.empty {
  text-align: center;
  color: #999;
  padding: 60px 0;
}
</style>