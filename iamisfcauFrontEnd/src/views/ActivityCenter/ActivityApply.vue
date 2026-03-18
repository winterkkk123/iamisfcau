<template>
  <div class="page">
    <div class="queryBar">
      <div class="field">
        <span class="label">活动名称</span>
        <input class="input" v-model.trim="queryTitle" placeholder="请输入活动名称" />
      </div>

      <div class="field">
        <span class="label">状态</span>
        <select class="select" v-model="queryStatus">
          <option value="">全部</option>
          <option value="待审核">待审核</option>
          <option value="已同意">已同意</option>
          <option value="已拒绝">已拒绝</option>
        </select>
      </div>

      <button class="btn primary" @click="doSearch">查询</button>
      <button class="btn" @click="resetSearch">重置</button>

      <button class="btn primary" @click="openCreateModal">新建活动申请</button>
    </div>

    <div class="tableHeader gridRow">
      <span class="cell">活动名称</span>
      <span class="cell">负责人（申请）</span>
      <span class="cell">举办方</span>
      <span class="cell">提交时间</span>
      <span class="cell">状态</span>
      <span class="cell opsHeader">操作</span>
    </div>

    <ul class="list">
      <li v-for="r in pagedRows" :key="r.applyId" class="list-item">
        <div class="gridRow row">
          <span class="cell ellipsis" :title="r.title">{{ r.title }}</span>
          <span class="cell ellipsis" :title="r.leaderName">{{ r.leaderName }}</span>
          <span class="cell ellipsis" :title="r.organizer">{{ r.organizer }}</span>
          <span class="cell ellipsis" :title="ymdHm(r.submittedAt)">{{ ymdHm(r.submittedAt) }}</span>

          <span class="cell">
            <span class="statusTag" :class="statusClass(r.status)">{{ r.status }}</span>
          </span>

          <span class="cell ops">
            <div class="opsInner">
              <button class="opBtn" @click="viewDetail(r)">查看</button>
              <span class="sep">|</span>
              <button class="opBtn danger" :disabled="!canWithdraw(r)" @click="withdraw(r.applyId)">
                撤回
              </button>
            </div>
          </span>
        </div>
      </li>

      <li v-if="pagedRows.length === 0" class="empty">暂无申请记录</li>
    </ul>

    <!-- 分页 -->
    <div class="pagination">
      <div class="pager">
        <div class="controls">
          <button @click="goFirst" :disabled="currentPage === 1">首页</button>
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>

          <button v-for="p in pageNumbers" :key="p" :class="{ active: p === currentPage }" @click="goPage(p)">
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

    <!-- 详情 -->
    <div v-if="detailVisible" class="modalMask" @click.self="closeDetail">
      <div class="modal modalWide">
        <div class="modalHeader">
          <div class="modalTitle">活动申请详情</div>
          <button class="closeBtn" @click="closeDetail">×</button>
        </div>

        <div class="modalBody">
          <div class="kv"><span class="k">活动名称</span><span class="v">{{ detailData?.title }}</span></div>
          <div class="kv"><span class="k">举办方</span><span class="v">{{ detailData?.organizer }}</span></div>
          <div class="kv"><span class="k">负责人（申请）</span><span class="v">{{ detailData?.leaderName }}</span></div>
          <div class="kv"><span class="k">开始时间</span><span class="v">{{ ymdHm(detailData?.startAt || '') }}</span></div>
          <div class="kv"><span class="k">结束时间</span><span class="v">{{ ymdHm(detailData?.endAt || '') }}</span></div>
          <div class="kv"><span class="k">地点</span><span class="v">{{ detailData?.location }}</span></div>
          <div class="kv"><span class="k">简介</span><span class="v break">{{ detailData?.summary }}</span></div>
          <div class="kv"><span class="k">内容</span><span class="v break">{{ detailData?.content }}</span></div>

          <div class="kv">
            <span class="k">状态</span>
            <span class="v">
              <span class="statusTag" :class="statusClass(detailData?.status || '待审核')">{{ detailData?.status }}</span>
            </span>
          </div>
          <div class="kv"><span class="k">审批意见</span><span class="v break">{{ detailData?.approvalComment || '—' }}</span></div>
        </div>

        <div class="modalFooter">
          <button class="btn" @click="closeDetail">关闭</button>
        </div>
      </div>
    </div>

    <!-- 新建 -->
    <div v-if="createVisible" class="modalMask" @click.self="closeCreateModal">
      <div class="modal modalWide">
        <div class="modalHeader">
          <div class="modalTitle">新建活动申请</div>
          <button class="closeBtn" @click="closeCreateModal">×</button>
        </div>

        <div class="modalBody">
          <div class="formRow">
            <span class="label">活动名称</span>
            <input class="input wide" v-model.trim="form.title" placeholder="请输入活动名称" />
          </div>

          <div class="formRow">
            <span class="label">举办方</span>
            <input class="input wide" v-model.trim="form.organizer" placeholder="请输入举办方（学院/部门）" />
          </div>

          <div class="formRow">
            <span class="label">开始时间</span>
            <div class="dateTimeWrap">
              <input
                class="input wide dateTimeInput"
                type="datetime-local"
                v-model="form.startAt"
                step="60"
                title="小时范围 0-23,分钟范围 0-59"
              />
            </div>
          </div>

          <div class="formRow">
            <span class="label">结束时间</span>
            <div class="dateTimeWrap">
              <input
                class="input wide dateTimeInput"
                type="datetime-local"
                v-model="form.endAt"
                step="60"
                title="小时范围 0-23,分钟范围 0-59"
              />
            </div>
          </div>

          <div class="formRow">
            <span class="label">地点</span>
            <input class="input wide" v-model.trim="form.location" placeholder="请输入地点" />
          </div>

          <div class="formRow">
            <span class="label">简介</span>
            <textarea class="textarea" v-model.trim="form.summary" placeholder="请输入活动简介" />
          </div>

          <div class="formRow">
            <span class="label">内容</span>
            <textarea class="textarea" v-model.trim="form.content" placeholder="请输入活动内容" />
          </div>
        </div>

        <div class="modalFooter">
          <button class="btn" @click="closeCreateModal">取消</button>
          <button class="btn primary" :disabled="!canSubmit" @click="submitCreateApply">提交</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="ActivityApply">
import { ref, computed, onMounted } from 'vue'
import { ymdHm } from '@/utils/time'
import { apiGet, apiPost } from '@/api/http'
import { http } from '@/api/http'
import { useAppStore } from '@/stores/app'

type Role = '管理员' | '教师' | '辅导员' | '学生'
type Status = '待审核' | '已同意' | '已拒绝'

type ActivityCreateApproval = {
  applyId: string
  title: string
  organizer: string
  leaderId: string
  leaderName: string
  startAt: string
  endAt: string
  location: string
  summary: string
  content: string
  submittedAt: string
  status: Status
  approvalComment: string
  auditorId: string
  auditorName: string
  auditedAt: string
}

const appStore = useAppStore()
const me = computed(() => appStore.user as { id: string; name: string; role: Role })

const approvals = ref<ActivityCreateApproval[]>([])

const mapApproval = (item: any): ActivityCreateApproval => ({
  applyId: item.applyId ?? item.apply_id,
  title: item.title,
  organizer: item.organizer,
  leaderId: item.leaderId ?? item.leader_id,
  leaderName: item.leaderName ?? item.leader_name,
  startAt: item.startAt ?? item.start_at,
  endAt: item.endAt ?? item.end_at,
  location: item.location,
  summary: item.summary ?? '',
  content: item.content ?? '',
  submittedAt: item.submittedAt ?? item.submitted_at,
  status: item.status,
  approvalComment: item.approvalComment ?? item.comment ?? '',
  auditorId: item.auditorId ?? item.auditor_id ?? '',
  auditorName: item.auditorName ?? item.auditor_name ?? '',
  auditedAt: item.auditedAt ?? item.audited_at ?? ''
})

const loadApprovals = async () => {
  const res = await apiGet<any[]>('/api/activity-create-approvals')
  approvals.value = (res || []).map(mapApproval)
}

onMounted(() => {
  loadApprovals().catch((err: any) => {
    window.alert(err?.message || '活动申请列表加载失败')
  })
})

const sourceRows = computed(() => {
  if (me.value.role === '管理员') return approvals.value
  return approvals.value.filter((x) => x.leaderId === me.value.id)
})

const queryTitle = ref('')
const queryStatus = ref<Status | ''>('')

const appliedTitle = ref('')
const appliedStatus = ref<Status | ''>('')

const doSearch = () => {
  appliedTitle.value = queryTitle.value
  appliedStatus.value = queryStatus.value
  currentPage.value = 1
}
const resetSearch = () => {
  queryTitle.value = ''
  queryStatus.value = ''
  appliedTitle.value = ''
  appliedStatus.value = ''
  currentPage.value = 1
}

const filteredRows = computed(() => {
  const t = appliedTitle.value.trim()
  const s = appliedStatus.value
  return sourceRows.value.filter((r) => {
    const mt = t ? r.title.includes(t) : true
    const ms = s ? r.status === s : true
    return mt && ms
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
  if (jumpPage.value && jumpPage.value >= 1 && jumpPage.value <= totalPages.value) currentPage.value = jumpPage.value
}

const statusClass = (s: Status) => {
  switch (s) {
    case '待审核':
      return 'pending'
    case '已同意':
      return 'approved'
    case '已拒绝':
      return 'rejected'
  }
}

const canWithdraw = (r: ActivityCreateApproval) => r.status === '待审核' && r.leaderId === me.value.id

const withdraw = async (applyId: string) => {
  try {
    await http.delete(`/api/activity-create-approvals/${applyId}`)
    approvals.value = approvals.value.filter((x) => x.applyId !== applyId)

    const newTotalPages = Math.max(1, Math.ceil(filteredRows.value.length / pageSize.value))
    if (currentPage.value > newTotalPages) currentPage.value = newTotalPages
  } catch (err: any) {
    window.alert(err?.message || '撤回失败')
  }
}

const detailVisible = ref(false)
const detailData = ref<ActivityCreateApproval | null>(null)

const viewDetail = (r: ActivityCreateApproval) => {
  detailData.value = r
  detailVisible.value = true
}
const closeDetail = () => {
  detailVisible.value = false
  detailData.value = null
}

const createVisible = ref(false)
const form = ref({
  title: '',
  organizer: '',
  startAt: '',
  endAt: '',
  location: '',
  summary: '',
  content: ''
})

const openCreateModal = () => {
  form.value = {
    title: '',
    organizer: '',
    startAt: '',
    endAt: '',
    location: '',
    summary: '',
    content: ''
  }
  createVisible.value = true
}
const closeCreateModal = () => (createVisible.value = false)

const canSubmit = computed(() => {
  const f = form.value
  return (
    !!f.title.trim() &&
    !!f.organizer.trim() &&
    !!f.startAt.trim() &&
    !!f.endAt.trim() &&
    !!f.location.trim() &&
    !!f.summary.trim() &&
    !!f.content.trim()
  )
})

const toBackendDateTime = (value: string) => {
  // datetime-local 通常返回：2026-02-20T14:30
  // 后端 LocalDateTime 更稳妥的格式：2026-02-20T14:30:00
  if (!value) return ''
  return value.length === 16 ? `${value}:00` : value
}

const submitCreateApply = async () => {
  if (!canSubmit.value) return

  try {
    const startAt = toBackendDateTime(form.value.startAt)
    const endAt = toBackendDateTime(form.value.endAt)

    if (!startAt || !endAt) {
      window.alert('请选择开始时间和结束时间')
      return
    }

    if (new Date(startAt).getTime() >= new Date(endAt).getTime()) {
      window.alert('结束时间必须晚于开始时间')
      return
    }

    const res = await apiPost<any>('/api/activity-create-approvals', {
      title: form.value.title.trim(),
      organizer: form.value.organizer.trim(),
      leaderId: me.value.id,
      startAt,
      endAt,
      location: form.value.location.trim(),
      summary: form.value.summary.trim(),
      content: form.value.content.trim()
    })

    approvals.value.unshift(mapApproval(res))
    createVisible.value = false
    currentPage.value = 1
  } catch (err: any) {
    window.alert(err?.message || '提交失败')
  }
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
.input,
.select {
  height: 32px;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 0 10px;
  font-size: 14px;
  outline: none;
  background: #fff;
  box-sizing: border-box;
}

.input {
  width: 180px;
}

.input.wide {
  width: 100%;
  max-width: 100%;
  min-width: 0;
}

.select {
  width: 160px;
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
.btn.danger {
  border-color: #d93026;
  background: #d93026;
  color: #fff;
}

.gridRow {
  display: grid;
  grid-template-columns: 1.1fr 0.8fr 0.9fr 1fr 0.7fr 180px;
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
  padding-right: 43px;
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
.opBtn.danger {
  color: #d93026;
}
.opBtn:disabled {
  color: #aaa;
  cursor: not-allowed;
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

.statusTag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 20px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
}
.statusTag.pending {
  background: #fff4e5;
  color: #b26a00;
}
.statusTag.approved {
  background: #e7f7ee;
  color: #1e8e3e;
}
.statusTag.rejected {
  background: #fde2e2;
  color: #d93026;
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
  gap: 8px;
  overflow-x: hidden;
}
.kv {
  display: grid;
  grid-template-columns: 110px 1fr;
  gap: 16px;
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
.formRow {
  display: grid;
  grid-template-columns: 90px minmax(0, 1fr);
  gap: 10px;
  align-items: start;
  padding: 8px 0;
}
.textarea {
  width: 100%;
  min-height: 72px;
  resize: vertical;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 8px 10px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
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
input[type='datetime-local'].input {
  padding-right: 10px;
}

.dateTimeWrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
  min-width: 0;
}

.dateTimeHint {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
  margin-left: 2px;
}

.dateTimeInput {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  height: 32px;
  padding: 0 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  color: #333;
  font-size: 14px;
  line-height: 32px;
  box-sizing: border-box;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.dateTimeInput:hover {
  border-color: #c8c8c8;
}

.dateTimeInput:focus {
  border-color: #4c98d4;
  box-shadow: 0 0 0 2px rgba(76, 152, 212, 0.12);
}

.dateTimeInput::-webkit-datetime-edit {
  color: #333;
  padding: 0;
}

.dateTimeInput::-webkit-datetime-edit-fields-wrapper {
  padding: 0;
}

.dateTimeInput::-webkit-datetime-edit-year-field,
.dateTimeInput::-webkit-datetime-edit-month-field,
.dateTimeInput::-webkit-datetime-edit-day-field,
.dateTimeInput::-webkit-datetime-edit-hour-field,
.dateTimeInput::-webkit-datetime-edit-minute-field {
  color: #333;
  font-weight: 400;
  padding: 0 1px;
  background: transparent;
}

.dateTimeInput::-webkit-datetime-edit-text {
  color: #666;
  padding: 0 1px;
}

.dateTimeInput::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.75;
  padding: 2px;
}

.dateTimeInput::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
}
</style>
