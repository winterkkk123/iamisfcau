<template>
  <div class="panelInner">
    <!-- 查询栏 -->
    <div class="queryBar">
      <div class="field">
        <span class="label">用户</span>
        <input class="input" v-model.trim="auditQueryUserName" placeholder="请输入用户姓名" />
      </div>

      <div class="field">
        <span class="label">申请人</span>
        <input class="input" v-model.trim="auditQueryApplicantName" placeholder="请输入申请人姓名" />
      </div>

      <div class="field">
        <span class="label">状态</span>
        <select class="select" v-model="auditQueryStatus">
          <option value="">全部</option>
          <option value="待审核">待审核</option>
          <option value="已同意">已同意</option>
          <option value="已拒绝">已拒绝</option>
        </select>
      </div>

      <button class="btn primary" @click="doAuditSearch">查询</button>
      <button class="btn" @click="resetAuditSearch">重置</button>
    </div>

    <!-- 表头 -->
    <div class="tableHeader auditGridRow">
      <span class="cell">申请ID</span>
      <span class="cell">用户</span>
      <span class="cell">申请人</span>
      <span class="cell">变更类型</span>
      <span class="cell">变更前</span>
      <span class="cell">变更后</span>
      <span class="cell">提交时间</span>
      <span class="cell">状态</span>
      <span class="cell opsHeader">操作</span>
    </div>

    <!-- 列表 -->
    <ul class="list">
      <li v-for="r in pagedAudits" :key="r.applyId" class="list-item">
        <div class="auditGridRow row">
          <span class="cell ellipsis" :title="r.applyId">{{ r.applyId }}</span>
          <span class="cell ellipsis" :title="r.userName">{{ r.userName }}</span>
          <span class="cell ellipsis" :title="r.applicantName">{{ r.applicantName }}</span>
          <span class="cell ellipsis" :title="r.changeType">{{ r.changeType }}</span>
          <span class="cell ellipsis" :title="r.before">{{ r.before }}</span>
          <span class="cell ellipsis" :title="r.after">{{ r.after }}</span>
          <span class="cell ellipsis" :title="r.submittedAt">{{ ymdHm(r.submittedAt) }}</span>

          <span class="cell">
            <span class="statusTag" :class="statusClass(r.status)">{{ r.status }}</span>
          </span>

          <span class="cell ops">
            <div class="opsInner">
              <button class="opBtn" @click="viewAuditDetail(r)">查看详细信息</button>
              <span class="sep">|</span>
              <button class="opBtn" :disabled="r.status !== '待审核'" @click="approveAudit(r.applyId)">
                同意
              </button>
              <span class="sep">|</span>
              <button class="opBtn danger" :disabled="r.status !== '待审核'" @click="rejectAudit(r.applyId)">
                拒绝
              </button>
            </div>
          </span>
        </div>
      </li>

      <li v-if="pagedAudits.length === 0" class="empty">暂无符合条件的审核记录</li>
    </ul>

    <!-- 分页 -->
    <div class="pagination">
      <div class="pager">
        <div class="controls">
          <button @click="auditGoFirst" :disabled="auditCurrentPage === 1">首页</button>
          <button @click="auditPrevPage" :disabled="auditCurrentPage === 1">上一页</button>

          <button
            v-for="p in auditPageNumbers"
            :key="p"
            :class="{ active: p === auditCurrentPage }"
            @click="auditGoPage(p)"
          >
            {{ p }}
          </button>

          <button @click="auditNextPage" :disabled="auditCurrentPage === auditTotalPages">下一页</button>
          <button @click="auditGoLast" :disabled="auditCurrentPage === auditTotalPages">尾页</button>
        </div>

        <div class="jump">
          跳转
          <input type="number" v-model.number="auditJumpPage" :min="1" :max="auditTotalPages" />
          <button @click="auditJumpToPage">GO</button>
        </div>
      </div>
    </div>

    <div v-if="detailVisible" class="modalMask" @click.self="detailVisible = false">
      <div class="modal">
        <div class="modalHeader">
          <div class="modalTitle">详细信息</div>
          <button class="closeBtn" @click="detailVisible = false">×</button>
        </div>

        <div class="modalBody">
          <div class="kv">
            <span class="k">申请ID</span>
            <span class="v">{{ detailData?.applyId }}</span>
          </div>
          <div class="kv">
            <span class="k">用户</span>
            <span class="v">{{ detailData?.userName }}</span>
          </div>
          <div class="kv">
            <span class="k">用户ID</span>
            <span class="v">{{ detailData?.userId }}</span>
          </div>
          <div class="kv">
            <span class="k">申请人</span>
            <span class="v">{{ detailData?.applicantName }}</span>
          </div>
          <div class="kv">
            <span class="k">申请人ID</span>
            <span class="v">{{ detailData?.applicantId }}</span>
          </div>
          <div class="kv">
            <span class="k">变更类型</span>
            <span class="v">{{ detailData?.changeType }}</span>
          </div>
          <div class="kv">
            <span class="k">更改前</span>
            <span class="v break">{{ detailData?.before }}</span>
          </div>
          <div class="kv">
            <span class="k">更改后</span>
            <span class="v break">{{ detailData?.after }}</span>
          </div>
          <div class="kv">
            <span class="k">提交时间</span>
            <span class="v">{{ ymdHm(detailData?.submittedAt) }}</span>
          </div>
          <div class="kv">
            <span class="k">状态</span>
            <span class="v">
              <span class="statusTag" :class="statusClass(detailData?.status || '待审核')">
                {{ detailData?.status }}
              </span>
            </span>
          </div>
          <div class="kv">
            <span class="k">审批意见</span>
            <span class="v">
              <textarea
                class="textarea"
                v-model="editApprovalComment"
                placeholder="可填写审批意见（可为空）"
                :disabled="detailData?.status !== '待审核'"
              />
            </span>
          </div>
        </div>

        <div class="modalFooter">
          <button class="btn" @click="detailVisible = false">关闭</button>
          <button
            class="btn primary"
            :disabled="!detailData || detailData.status !== '待审核'"
            @click="detailData && approveAudit(detailData.applyId)"
          >
            同意
          </button>
          <button
            class="btn danger"
            :disabled="!detailData || detailData.status !== '待审核'"
            @click="detailData && rejectAudit(detailData.applyId)"
          >
            拒绝
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="UserAudit">
import { ymdHm } from '@/utils/time'
import { ref, computed, onMounted } from 'vue'
import {
  getProfileChangeApplications,
  approveProfileChange,
  rejectProfileChange,
  type AuditItem
} from '@/api/profileChange'

type AuditStatus = '待审核' | '已同意' | '已拒绝'

const auditRecords = ref<AuditItem[]>([])
const loading = ref(false)

async function loadAudits() {
  loading.value = true
  try {
    auditRecords.value = await getProfileChangeApplications()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAudits()
})

const auditQueryUserName = ref('')
const auditQueryApplicantName = ref('')
const auditQueryStatus = ref<AuditStatus | ''>('')

const auditAppliedUserName = ref('')
const auditAppliedApplicantName = ref('')
const auditAppliedStatus = ref<AuditStatus | ''>('')

const doAuditSearch = () => {
  auditAppliedUserName.value = auditQueryUserName.value
  auditAppliedApplicantName.value = auditQueryApplicantName.value
  auditAppliedStatus.value = auditQueryStatus.value
  auditCurrentPage.value = 1
}

const resetAuditSearch = () => {
  auditQueryUserName.value = ''
  auditQueryApplicantName.value = ''
  auditQueryStatus.value = ''
  auditAppliedUserName.value = ''
  auditAppliedApplicantName.value = ''
  auditAppliedStatus.value = ''
  auditCurrentPage.value = 1
}

const filteredAudits = computed(() => {
  const uName = auditAppliedUserName.value.trim()
  const aName = auditAppliedApplicantName.value.trim()
  const status = auditAppliedStatus.value

  return auditRecords.value.filter((r) => {
    const matchUser = uName ? r.userName.includes(uName) : true
    const matchApplicant = aName ? r.applicantName.includes(aName) : true
    const matchStatus = status ? r.status === status : true
    return matchUser && matchApplicant && matchStatus
  })
})

const auditPageSize = ref(12)
const auditCurrentPage = ref(1)
const auditJumpPage = ref<number | null>(null)

const auditTotal = computed(() => filteredAudits.value.length)
const auditTotalPages = computed(() => Math.max(1, Math.ceil(auditTotal.value / auditPageSize.value)))

const pagedAudits = computed(() => {
  const start = (auditCurrentPage.value - 1) * auditPageSize.value
  return filteredAudits.value.slice(start, start + auditPageSize.value)
})

const auditPageNumbers = computed(() => {
  const pages: number[] = []
  const start = Math.max(1, auditCurrentPage.value - 2)
  const end = Math.min(auditTotalPages.value, start + 4)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

const auditGoPage = (p: number) => (auditCurrentPage.value = p)
const auditPrevPage = () => auditCurrentPage.value > 1 && auditCurrentPage.value--
const auditNextPage = () => auditCurrentPage.value < auditTotalPages.value && auditCurrentPage.value++
const auditGoFirst = () => (auditCurrentPage.value = 1)
const auditGoLast = () => (auditCurrentPage.value = auditTotalPages.value)

const auditJumpToPage = () => {
  if (auditJumpPage.value && auditJumpPage.value >= 1 && auditJumpPage.value <= auditTotalPages.value) {
    auditCurrentPage.value = auditJumpPage.value
  }
}

const detailVisible = ref(false)
const detailData = ref<AuditItem | null>(null)
const editApprovalComment = ref('')

const viewAuditDetail = (r: AuditItem) => {
  detailData.value = r
  editApprovalComment.value = r.approvalComment || ''
  detailVisible.value = true
}

const approveAudit = async (applyId: string) => {
  const t = auditRecords.value.find((x) => x.applyId === applyId)
  if (!t || t.status !== '待审核') return

  try {
    const updated = await approveProfileChange(applyId, editApprovalComment.value)

    const idx = auditRecords.value.findIndex((x) => x.applyId === applyId)
    if (idx !== -1) auditRecords.value[idx] = updated
    if (detailData.value?.applyId === applyId) detailData.value = { ...updated }
  } catch (e: any) {
    alert(e?.message || '审批失败')
  }
}

const rejectAudit = async (applyId: string) => {
  const t = auditRecords.value.find((x) => x.applyId === applyId)
  if (!t || t.status !== '待审核') return

  try {
    const updated = await rejectProfileChange(applyId, editApprovalComment.value)

    const idx = auditRecords.value.findIndex((x) => x.applyId === applyId)
    if (idx !== -1) auditRecords.value[idx] = updated
    if (detailData.value?.applyId === applyId) detailData.value = { ...updated }
  } catch (e: any) {
    alert(e?.message || '审批失败')
  }
}

const statusClass = (s: AuditStatus) => {
  switch (s) {
    case '待审核':
      return 'pending'
    case '已同意':
      return 'approved'
    case '已拒绝':
      return 'rejected'
  }
}
</script>

<style scoped>
.panelInner {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
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
}

.input {
  width: 180px;
}
.select {
  width: 140px;
  background: #fff;
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

.auditGridRow {
  display: grid;
  grid-template-columns: 1.05fr 0.7fr 0.8fr 1fr 1.1fr 1.1fr 1.2fr 0.8fr 240px;
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
  gap: 10px;
  font-size: 14px;
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

.textarea:disabled {
  background: #f7f7f7;
  color: #666;
}

.modalFooter {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
