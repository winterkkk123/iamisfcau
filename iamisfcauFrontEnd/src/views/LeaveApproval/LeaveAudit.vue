<template>
  <div class="page">
    <!-- 查询栏 -->
    <div class="queryBar">
      <div class="field">
        <span class="label">申请人</span>
        <input class="input" v-model.trim="queryApplicant" placeholder="请输入申请人姓名" />
      </div>

      <div class="field">
        <span class="label">活动</span>
        <input class="input" v-model.trim="queryActivity" placeholder="请输入活动名称" />
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
    </div>

    <!-- 表头 -->
    <div class="tableHeader gridRow">
      <span class="cell">活动</span>
      <span class="cell">申请人</span>
      <span class="cell reasonHead">请假理由</span>
      <span class="cell">提交时间</span>
      <span class="cell">状态</span>
      <span class="cell opsHeader">操作</span>
    </div>

    <!-- 列表 -->
    <ul class="list">
      <li v-for="r in pagedRows" :key="r.applyId" class="list-item">
        <div class="gridRow row">
          <span class="cell ellipsis" :title="r.activityTitle">{{ r.activityTitle }}</span>
          <span class="cell ellipsis" :title="r.applicantName">{{ r.applicantName }}</span>
          <span class="cell reasonCell" :title="r.reason">{{ r.reason }}</span>
          <span class="cell ellipsis" :title="ymdHm(r.submittedAt)">{{ ymdHm(r.submittedAt) }}</span>

          <span class="cell">
            <span class="statusTag" :class="statusClass(r.status)">{{ r.status }}</span>
          </span>

          <span class="cell ops">
            <div class="opsInner">
              <button class="opBtn" @click="viewDetail(r)">查看详细信息</button>
              <span class="sep">|</span>
              <button class="opBtn" :disabled="r.status !== '待审核'" @click="approve(r.applyId)">同意</button>
              <span class="sep">|</span>
              <button class="opBtn danger" :disabled="r.status !== '待审核'" @click="reject(r.applyId)">拒绝</button>
            </div>
          </span>
        </div>
      </li>

      <li v-if="pagedRows.length === 0" class="empty">暂无符合条件的请假申请</li>
    </ul>

    <!-- 分页 -->
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

    <!-- 详情+审批弹窗 -->
    <div v-if="detailVisible" class="modalMask" @click.self="closeDetail">
      <div class="modal modalWide">
        <div class="modalHeader">
          <div class="modalTitle">请假详情</div>
          <button class="closeBtn" @click="closeDetail">×</button>
        </div>

        <div class="modalBody twoCol">
          <div class="col">
            <div class="sectionTitle">申请信息</div>

            <div class="kv"><span class="k">活动</span><span class="v">{{ detailData?.activityTitle }}</span></div>
            <div class="kv"><span class="k">申请人</span><span class="v">{{ detailData?.applicantName }}</span></div>
            <div class="kv"><span class="k">指导教师</span><span class="v">{{ detailData?.teacherName }}</span></div>
            <div class="kv"><span class="k">提交时间</span><span class="v">{{ ymdHm(detailData?.submittedAt || '') }}</span></div>
            <div class="kv"><span class="k">请假理由</span><span class="v break">{{ detailData?.reason }}</span></div>

            <div class="kv">
              <span class="k">状态</span>
              <span class="v">
                <span class="statusTag" :class="statusClass(detailData?.status || '待审核')">
                  {{ detailData?.status }}
                </span>
              </span>
            </div>

            <div class="kv"><span class="k">审批意见</span><span class="v break">{{ detailData?.approvalComment || '—' }}</span></div>
          </div>

          <div class="col">
            <div class="sectionTitle">审批</div>

            <div class="formRow">
              <span class="label">审批意见</span>
              <textarea
                class="textarea"
                v-model="editApprovalComment"
                placeholder="可填写审批意见（可为空）"
                :disabled="!detailData || detailData.status !== '待审核'"
              />
            </div>

            <div class="tip">仅“待审核”状态可同意/拒绝。</div>
          </div>
        </div>

        <div class="modalFooter">
          <button class="btn" @click="closeDetail">关闭</button>
          <button class="btn primary" :disabled="!detailData || detailData.status !== '待审核'" @click="detailData && approve(detailData.applyId)">
            同意
          </button>
          <button class="btn danger" :disabled="!detailData || detailData.status !== '待审核'" @click="detailData && reject(detailData.applyId)">
            拒绝
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="LeaveAudit">
import { ref, computed, onMounted } from 'vue'
import { ymdHm } from '@/utils/time'
import { useAppStore } from '@/stores/app'
import { getLeaveApprovals, approveLeave, rejectLeave } from '@/api/leaveApproval'

const leaveApprovals = ref<LeaveApprovalItem[]>([])

const loadData = async () => {
  leaveApprovals.value = await getLeaveApprovals()
}

onMounted(async () => {
  try {
    await loadData()
  } catch (e: any) {
    alert(e.message || '加载失败')
  }
})

type Role = '管理员' | '教师' | '辅导员' | '学生'
type LeaveStatus = '待审核' | '已同意' | '已拒绝'

type LeaveApprovalItem = {
  applyId: string
  activityId: number
  activityTitle: string
  teacherId: string
  teacherName: string
  applicantId: string
  applicantName: string
  reason: string
  submittedAt: string
  status: LeaveStatus
  approvalComment: string
}

const appStore = useAppStore()
const me = computed(() => appStore.user as { id: string; name: string; role: Role })

/* 审核数据源：教师只看自己负责；辅导员/管理员看全部 */
const sourceRows = computed(() => {
  if (me.value.role === '教师') return leaveApprovals.value.filter((x) => x.teacherId === me.value.id)
  return leaveApprovals.value
})

/* 查询 */
const queryApplicant = ref('')
const queryActivity = ref('')
const queryStatus = ref<LeaveStatus | ''>('')

const appliedApplicant = ref('')
const appliedActivity = ref('')
const appliedStatus = ref<LeaveStatus | ''>('')

const doSearch = () => {
  appliedApplicant.value = queryApplicant.value
  appliedActivity.value = queryActivity.value
  appliedStatus.value = queryStatus.value
  currentPage.value = 1
}
const resetSearch = () => {
  queryApplicant.value = ''
  queryActivity.value = ''
  queryStatus.value = ''
  appliedApplicant.value = ''
  appliedActivity.value = ''
  appliedStatus.value = ''
  currentPage.value = 1
}

const filteredRows = computed(() => {
  const a = appliedApplicant.value.trim()
  const t = appliedActivity.value.trim()
  const s = appliedStatus.value

  return sourceRows.value.filter((r) => {
    const matchApplicant = a ? r.applicantName.includes(a) : true
    const matchTitle = t ? r.activityTitle.includes(t) : true
    const matchStatus = s ? r.status === s : true
    return matchApplicant && matchTitle && matchStatus
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

const statusClass = (s: LeaveStatus) => {
  switch (s) {
    case '待审核':
      return 'pending'
    case '已同意':
      return 'approved'
    case '已拒绝':
      return 'rejected'
  }
}

const detailVisible = ref(false)
const detailData = ref<LeaveApprovalItem | null>(null)
const editApprovalComment = ref('')

const viewDetail = (r: LeaveApprovalItem) => {
  detailData.value = r
  editApprovalComment.value = r.approvalComment || ''
  detailVisible.value = true
}
const closeDetail = () => {
  detailVisible.value = false
  detailData.value = null
  editApprovalComment.value = ''
}

const approve = async (applyId: string) => {
  try {
    const updated = await approveLeave(applyId, {
      approvalComment: editApprovalComment.value || ''
    })
    const idx = leaveApprovals.value.findIndex((x) => x.applyId === applyId)
    if (idx > -1) leaveApprovals.value[idx] = updated
    if (detailData.value?.applyId === applyId) detailData.value = { ...updated }
  } catch (e: any) {
    alert(e.message || '审批失败')
  }
}

const reject = async (applyId: string) => {
  try {
    const updated = await rejectLeave(applyId, {
      approvalComment: editApprovalComment.value || ''
    })

    const idx = leaveApprovals.value.findIndex((x) => x.applyId === applyId)
    if (idx > -1) leaveApprovals.value[idx] = updated
    if (detailData.value?.applyId === applyId) detailData.value = { ...updated }
  } catch (e: any) {
    alert(e.message || '审批失败')
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
}

.input {
  width: 180px;
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
  grid-template-columns: 1.1fr 0.8fr 1.6fr 1fr 0.7fr 260px;
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

.reasonCell {
  max-width: 320px; 
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  min-width: 0; 
}

.reasonHead {
  max-width: 320px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  min-width: 0;
}

.opsHeader,
.ops {
  justify-self: end;
  text-align: right;
}
.opsHeader {
  padding-right: 143px;
}

.opsInner {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0;
  flex-wrap: wrap;
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

/* 分页 */
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

/* 弹窗 */
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

.twoCol {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.col {
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  padding: 12px 12px;
}

.sectionTitle {
  font-weight: bold;
  margin-bottom: 10px;
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

.formRow {
  display: grid;
  grid-template-columns: 90px 1fr;
  gap: 10px;
  align-items: center;
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

.tip {
  margin-top: 8px;
  font-size: 13px;
  color: #888;
  line-height: 1.6;
}

.modalFooter {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
