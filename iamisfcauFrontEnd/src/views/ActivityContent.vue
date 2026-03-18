<template>
  <div class="activityContent">
    <div class="header">
      <h2>活动列表</h2>
    </div>

    <ul class="list">
      <li
        v-for="item in pagedActivities"
        :key="item.id"
        class="list-item"
      >
        <router-link
          :to="`/home/activity/${item.id}`"
          class="list-link"
        >
          <div class="row">
            <div class="left">
              <span
                v-for="tag in item.tags"
                :key="tag"
                class="tag"
                :class="tag"
              >
                {{ tag === 'top' ? '置顶' : '活动' }}
              </span>

              <span class="title">
                {{ item.title }}
              </span>
            </div>

            <div class="right">
              <span class="location">
                {{ item.location }}
              </span>
              <span class="time">
                {{ formatTime(item) }}
              </span>
            </div>
          </div>
        </router-link>
      </li>
    </ul>

    <div class="pagination">
      <div class="pager">
        <div class="controls">
          <button @click="goFirst" :disabled="currentPage === 1">首页</button>
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>

          <button
            v-for="p in pageNumbers"
            :key="p"
            :class="{ active: p === currentPage }"
            @click="goPage(p)"
          >
            {{ p }}
          </button>

          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
          >
            下一页
          </button>
          <button
            @click="goLast"
            :disabled="currentPage === totalPages"
          >
            尾页
          </button>
        </div>

        <div class="jump">
          跳转
          <input
            type="number"
            v-model.number="jumpPage"
            :min="1"
            :max="totalPages"
          />
          <button @click="jumpToPage">GO</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="ActivityContent">
import { ref, computed, onMounted } from 'vue'
import { ymdHm, ymdHmFromDateTime } from '@/utils/time'
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
  tags: string[]
}

const activities = ref<ActivityItem[]>([])

const loadActivities = async () => {
  const res = await apiGet<any[]>('/api/activities')
  activities.value = (res || []).map((item) => ({
    id: item.id,
    title: item.title,
    organizer: item.organizer,
    leaderId: item.leaderId ?? item.leader_id,
    leaderName: item.leaderName ?? item.leader_name,
    date: item.date ?? item.activityDate ?? item.activity_date,
    time: item.time ?? item.activityTime ?? item.activity_time,
    startAt: item.startAt ?? item.start_at,
    endAt: item.endAt ?? item.end_at,
    location: item.location,
    summary: item.summary ?? '',
    content: item.content ?? '',
    tags: ['normal']
  }))
}

onMounted(() => {
  loadActivities().catch((err: any) => {
    window.alert(err?.message || '活动列表加载失败')
  })
})

const pageSize = ref(6)
const currentPage = ref(1)
const jumpPage = ref<number | null>(null)

const total = computed(() => activities.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const pagedActivities = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return activities.value.slice(start, start + pageSize.value)
})

const pageNumbers = computed(() => {
  const pages: number[] = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

const goPage = (p: number) => (currentPage.value = p)
const prevPage = () => currentPage.value > 1 && currentPage.value--
const nextPage = () =>
  currentPage.value < totalPages.value && currentPage.value++
const goFirst = () => (currentPage.value = 1)
const goLast = () => (currentPage.value = totalPages.value)

const jumpToPage = () => {
  if (
    jumpPage.value &&
    jumpPage.value >= 1 &&
    jumpPage.value <= totalPages.value
  ) {
    currentPage.value = jumpPage.value
  }
}

const formatTime = (item: ActivityItem) => {
  if (item.startAt) return ymdHm(item.startAt)
  if (item.date && item.time) return ymdHmFromDateTime(item.date, item.time)
  return ''
}
</script>

<style scoped>
.activityContent {
  height: 100%;
  background: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 16px 30px;
  border-bottom: 1px solid #eee;
}

.list {
  flex: 1;
  padding: 0 30px;
  margin: 0;
  list-style: none;
  overflow-y: auto;
}

.list-item {
  border-bottom: 1px solid #f2f2f2;
}

.list-link {
  display: block;
  padding: 14px 0;
  text-decoration: none;
  color: inherit;
}

.row {
  display: flex;
  align-items: center;
}

.left {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  overflow: hidden;
}

.title {
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.right {
  display: flex;
  align-items: center;
  gap: 50px;
  margin-left: 16px;
  white-space: nowrap;
}

.location {
  font-size: 13px;
  color: #666;
}

.time {
  font-size: 13px;
  color: #999;
}

.pagination {
  border-top: 1px solid #eee;
  padding: 12px 0;
  display: flex;
  justify-content: center;
}

.pager {
  display: flex;
  align-items: center;
  gap: 12px;
}

.controls {
  display: flex;
  gap: 6px;
}

.controls button {
  padding: 4px 10px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
}

.controls button.active {
  background: #4c98d4;
  color: #fff;
  border-color: #4c98d4;
}

.controls button:disabled {
  color: #aaa;
  cursor: not-allowed;
}

.jump {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.jump input {
  width: 50px;
  padding: 2px 4px;
}

.tag {
  flex-shrink: 0;
  height: 18px;
  padding: 0 6px;
  font-size: 12px;
  border-radius: 4px;
  display: inline-flex;
  align-items: center;
}

.tag.top {
  background: #fde2e2;
  color: #d93026;
}

.tag.normal {
  background: #e8f1fb;
  color: #4c98d4;
}
</style>