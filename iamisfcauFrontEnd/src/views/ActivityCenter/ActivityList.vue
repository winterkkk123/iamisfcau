<template>
  <div class="page">
    <div class="queryBar">
      <div class="field">
        <span class="label">活动名称</span>
        <input class="input" v-model.trim="queryTitle" placeholder="请输入活动名称" />
      </div>

      <div class="field">
        <span class="label">举办方</span>
        <input class="input" v-model.trim="queryOrganizer" placeholder="请输入举办方" />
      </div>

      <button class="btn primary" @click="doSearch">查询</button>
      <button class="btn" @click="resetSearch">重置</button>
    </div>

    <div class="tableHeader gridRow">
      <span class="cell">活动</span>
      <span class="cell">举办方</span>
      <span class="cell">负责人</span>
      <span class="cell">时间</span>
      <span class="cell">地点</span>
      <span class="cell opsHeader">操作</span>
    </div>

    <ul class="list">
      <li v-for="a in pagedRows" :key="a.id" class="list-item">
        <div class="gridRow row">
          <span class="cell ellipsis" :title="a.title">{{ a.title }}</span>
          <span class="cell ellipsis" :title="a.organizer">{{ a.organizer }}</span>
          <span class="cell ellipsis" :title="a.leaderName">{{ a.leaderName }}</span>
          <span class="cell ellipsis" :title="ymdHm(a.startAt)">{{ ymdHm(a.startAt) }}</span>
          <span class="cell ellipsis" :title="a.location">{{ a.location }}</span>

          <span class="cell ops">
            <div class="opsInner">
              <button class="opBtn" @click="viewDetail(a)">查看详细信息</button>

              <span class="sep">|</span>

              <template v-if="!isJoined(a)">
                <button class="opBtn" disabled>未报名</button>
                <span class="sep">|</span>
                <button class="opBtn" @click="joinActivity(a)">点击报名</button>
              </template>

              <template v-else>
                <button class="opBtn disabledText" disabled>已报名</button>
                <span class="sep">|</span>
                <button class="opBtn danger" @click="cancelJoin(a)">取消报名</button>
              </template>
            </div>
          </span>
        </div>
      </li>

      <li v-if="pagedRows.length === 0" class="empty">暂无活动</li>
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

          <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
          <button @click="goLast" :disabled="currentPage === totalPages">尾页</button>
        </div>

        <div class="jump">
          跳转
          <input type="number" v-model.number="jumpPage" :min="1" :max="totalPages" />
          <button @click="jumpToPage">GO</button>
        </div>
      </div>
    </div>

    <div v-if="detailVisible" class="modalMask" @click.self="closeDetail">
      <div class="modal modalWide">
        <div class="modalHeader">
          <div class="modalTitle">活动详情</div>
          <button class="closeBtn" @click="closeDetail">×</button>
        </div>

        <div class="modalBody">
          <div class="kv"><span class="k">活动</span><span class="v">{{ detailData?.title }}</span></div>
          <div class="kv"><span class="k">举办方</span><span class="v">{{ detailData?.organizer }}</span></div>
          <div class="kv"><span class="k">负责人</span><span class="v">{{ detailData?.leaderName }}</span></div>
          <div class="kv"><span class="k">开始时间</span><span class="v">{{ ymdHm(detailData?.startAt || '') }}</span></div>
          <div class="kv"><span class="k">结束时间</span><span class="v">{{ ymdHm(detailData?.endAt || '') }}</span></div>
          <div class="kv"><span class="k">地点</span><span class="v">{{ detailData?.location }}</span></div>
          <div class="kv"><span class="k">简介</span><span class="v break">{{ detailData?.summary }}</span></div>
          <div class="kv"><span class="k">内容</span><span class="v break">{{ detailData?.content }}</span></div>
        </div>

        <div class="modalFooter">
          <button class="btn" @click="closeDetail">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="ActivityList">
import { ref, computed, onMounted } from 'vue'
import { ymdHm } from '@/utils/time'
import { apiGet, apiPost, apiDelete } from '@/api/http'
import { useAppStore } from '@/stores/app'

type Role = '管理员' | '教师' | '辅导员' | '学生'

type ActivityItem = {
  id: number
  title: string
  organizer: string
  leaderId: string
  leaderName: string
  date?: string
  time?: string
  startAt: string
  endAt: string
  location: string
  summary: string
  content: string
}

type JoinResponse = {
  activityId: number
  userId: string
  joined: boolean
  message: string
}

const appStore = useAppStore()
const me = computed(() => appStore.user as { id: string; name: string; role: Role })

const activities = ref<ActivityItem[]>([])
const joinedIds = ref<number[]>([])

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
    content: item.content ?? ''
  }))
}

const loadJoinedIds = async () => {
  const res = await apiGet<number[]>('/api/activities/joined', {
    params: { userId: me.value.id }
  })
  joinedIds.value = res || []
}

const initData = async () => {
  await Promise.all([loadActivities(), loadJoinedIds()])
}

onMounted(() => {
  initData().catch((err: any) => {
    window.alert(err?.message || '活动列表加载失败')
  })
})

const queryTitle = ref('')
const queryOrganizer = ref('')
const appliedTitle = ref('')
const appliedOrganizer = ref('')

const doSearch = () => {
  appliedTitle.value = queryTitle.value
  appliedOrganizer.value = queryOrganizer.value
  currentPage.value = 1
}
const resetSearch = () => {
  queryTitle.value = ''
  queryOrganizer.value = ''
  appliedTitle.value = ''
  appliedOrganizer.value = ''
  currentPage.value = 1
}

const filteredRows = computed(() => {
  const t = appliedTitle.value.trim()
  const o = appliedOrganizer.value.trim()
  return activities.value.filter((a) => {
    const mt = t ? a.title.includes(t) : true
    const mo = o ? a.organizer.includes(o) : true
    return mt && mo
  })
})

const pageSize = ref(12)
const currentPage = ref(1)
const jumpPage = ref<number | null>(null)

const total = computed(() => filteredRows.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const pagedRows = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredRows.value.slice(start, start + pageSize.value)
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
const nextPage = () => currentPage.value < totalPages.value && currentPage.value++
const goFirst = () => (currentPage.value = 1)
const goLast = () => (currentPage.value = totalPages.value)
const jumpToPage = () => {
  if (jumpPage.value && jumpPage.value >= 1 && jumpPage.value <= totalPages.value) {
    currentPage.value = jumpPage.value
  }
}

const isJoined = (a: ActivityItem) => joinedIds.value.includes(a.id)

const joinActivity = async (a: ActivityItem) => {
  if (isJoined(a)) return

  try {
    const res = await apiPost<JoinResponse>(`/api/activities/${a.id}/join`, {
      userId: me.value.id
    })
    if (!joinedIds.value.includes(a.id)) {
      joinedIds.value.push(a.id)
    }
    if (res?.message) {
      window.alert(res.message)
    }
  } catch (err: any) {
    window.alert(err?.message || '报名失败')
  }
}

const cancelJoin = async (a: ActivityItem) => {
  if (!isJoined(a)) return

  try {
    const res = await apiDelete<JoinResponse>(`/api/activities/${a.id}/join`, {
      params: { userId: me.value.id }
    })
    joinedIds.value = joinedIds.value.filter((id) => id !== a.id)
    if (res?.message) {
      window.alert(res.message)
    }
  } catch (err: any) {
    window.alert(err?.message || '取消报名失败')
  }
}

const detailVisible = ref(false)
const detailData = ref<ActivityItem | null>(null)

const viewDetail = (a: ActivityItem) => {
  detailData.value = a
  detailVisible.value = true
}
const closeDetail = () => {
  detailVisible.value = false
  detailData.value = null
}
</script>

<style scoped>
.page {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.queryBar {
  padding: 14px 30px;
  border-bottom: 1px solid #f2f2f2;
  display: flex;
  align-items: center;
  gap: 14px;
  flex-wrap: wrap;
}

.field {
  display: flex;
  align-items: center;
  gap: 8px;
}

.label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.input {
  height: 32px;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 0 10px;
  font-size: 14px;
  outline: none;
  background: #fff;
  width: 180px;
}

.btn {
  height: 32px;
  padding: 0 14px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.btn.primary {
  border-color: #4c98d4;
  color: #fff;
  background: #4c98d4;
}

.gridRow {
  display: grid;
  grid-template-columns: 1.3fr 0.9fr 0.8fr 1fr 0.9fr 360px;
  column-gap: 6px;
  align-items: center;
}

.tableHeader {
  padding: 10px 30px;
  border-bottom: 1px solid #eee;
  color: #666;
  font-size: 13px;
  font-weight: bold;
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

.row {
  padding: 12px 0;
}

.cell {
  font-size: 14px;
  color: #333;
  justify-self: start;
  min-width: 0;
}

.ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.opsHeader,
.ops {
  justify-self: end;
  text-align: right;
}
.opsHeader {
  padding-right: 185px;
}

.opsInner {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.opBtn {
  border: none;
  background: transparent;
  cursor: pointer;
  color: #4c98d4;
  padding: 0;
  font-size: 14px;
  white-space: nowrap;
}

.opBtn.disabledText {
  color: #aaa !important;
  cursor: not-allowed;
}

.opBtn.danger {
  color: #d93026;
}

.opBtn:disabled {
  opacity: 1;
}

.sep {
  margin: 0 6px;
  color: #bbb;
}

.empty {
  padding: 24px 0;
  text-align: center;
  color: #999;
}

.modalMask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal {
  width: 560px;
  max-width: calc(100vw - 40px);
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.modalWide {
  width: 980px;
  max-width: calc(100vw - 40px);
}

.modalHeader {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #eee;
}

.modalTitle {
  font-size: 16px;
  font-weight: bold;
}

.closeBtn {
  border: none;
  background: transparent;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
  color: #999;
}

.modalBody {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.kv {
  display: grid;
  grid-template-columns: 90px 1fr;
  gap: 20px;
  font-size: 16px;
}

.k {
  color: #666;
}
.v {
  color: #333;
}
.break {
  white-space: pre-wrap;
  word-break: break-all;
}

.modalFooter {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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
</style>