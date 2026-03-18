<template>
  <div class="page">
    <div class="queryBar">
      <div class="field">
        <span class="label">活动名称</span>
        <input class="input" v-model.trim="queryTitle" placeholder="请输入活动名称" />
      </div>

      <button class="btn primary" @click="doSearch">查询</button>
      <button class="btn" @click="resetSearch">重置</button>
    </div>

    <!-- 我参加的活动 -->
    <div class="blockTitle">我参加的活动出勤</div>

    <div class="tableHeader gridRowMine">
      <span class="cell">活动</span>
      <span class="cell">时间</span>
      <span class="cell">地点</span>
      <span class="cell">是否迟到</span>
      <span class="cell">出勤状态</span>
      <span class="cell">签到时间</span>
      <span class="cell opsHeaderMine">操作</span>
    </div>

    <ul class="list">
      <li v-for="r in joinedRows" :key="r.activityId" class="list-item">
        <div class="gridRowMine row">
          <span class="cell ellipsis" :title="r.title">{{ r.title }}</span>
          <span class="cell ellipsis" :title="ymdHm(r.startAt)">{{ ymdHm(r.startAt) }}</span>
          <span class="cell ellipsis" :title="r.location">{{ r.location }}</span>

          <span class="cell">
            {{ lateText(r.attendStatus, r.isLate) }}
          </span>

          <span class="cell">
            <span class="statusTag" :class="attClass(r.attendStatus)">{{ r.attendStatus }}</span>
          </span>

          <span class="cell ellipsis" :title="r.signInAt ? ymdHm(r.signInAt) : ''">
            {{ r.signInAt ? ymdHm(r.signInAt) : '—' }}
          </span>

          <span class="cell opsMine">
            <button class="opBtn" :disabled="!canEdit(r.activityId, r.attendStatus)" @click="openEdit(r.activityId, r.userId)">
              编辑
            </button>
          </span>
        </div>
      </li>

      <li v-if="joinedRows.length === 0" class="empty">暂无参加活动出勤记录</li>
    </ul>

    <!-- 我管理的活动 -->
    <div class="blockTitle" style="margin-top: 10px;">我管理的活动出勤（所有人）</div>

    <div v-if="!canSeeManaged" class="emptyBox">当前身份没有管理活动权限</div>

    <template v-else>
      <div class="tableHeader gridRowManage">
        <span class="cell">活动</span>
        <span class="cell">参与人</span>
        <span class="cell">是否迟到</span>
        <span class="cell">出勤状态</span>
        <span class="cell">签到时间</span>
        <span class="cell opsHeaderManage">操作</span>
      </div>

      <ul class="list">
        <li v-for="r in managedRows" :key="r.key" class="list-item">
          <div class="gridRowManage row">
            <span class="cell ellipsis" :title="r.title">{{ r.title }}</span>
            <span class="cell ellipsis" :title="r.userName">{{ r.userName }}</span>

            <span class="cell">
              {{ lateText(r.attendStatus, r.isLate) }}
            </span>

            <span class="cell">
              <span class="statusTag" :class="attClass(r.attendStatus)">{{ r.attendStatus }}</span>
            </span>

            <span class="cell ellipsis" :title="r.signInAt ? ymdHm(r.signInAt) : ''">
              {{ r.signInAt ? ymdHm(r.signInAt) : '—' }}
            </span>

            <span class="cell opsManage">
              <button class="opBtn" :disabled="!canEdit(r.activityId, r.attendStatus)" @click="openEdit(r.activityId, r.userId)">
                编辑
              </button>
            </span>
          </div>
        </li>

        <li v-if="managedRows.length === 0" class="empty">暂无管理活动出勤记录</li>
      </ul>
    </template>

    <!-- 编辑弹窗 -->
    <div v-if="editVisible" class="modalMask" @click.self="closeEdit">
      <div class="modal">
        <div class="modalHeader">
          <div class="modalTitle">编辑出勤</div>
          <button class="closeBtn" @click="closeEdit">×</button>
        </div>

        <div class="modalBody">
          <div class="kv">
            <span class="k">活动</span>
            <span class="v">{{ editInfo?.activityTitle }}</span>
          </div>
          <div class="kv">
            <span class="k">参与人</span>
            <span class="v">{{ editInfo?.userName }}</span>
          </div>

          <div class="formRow">
            <span class="k">出勤状态</span>
            <select class="select" v-model="editAttendStatus">
              <option value="已出勤">已出勤</option>
              <option value="未出勤">未出勤</option>
              <option value="请假">请假</option>
            </select>
          </div>

          <div class="formRow">
            <span class="k">是否迟到</span>
            <label class="radioLine">
              <input type="radio" :value="false" v-model="editIsLate" :disabled="editAttendStatus !== '已出勤'" />
              <span>否</span>
            </label>
            <label class="radioLine">
              <input type="radio" :value="true" v-model="editIsLate" :disabled="editAttendStatus !== '已出勤'" />
              <span>是</span>
            </label>
          </div>
        </div>

        <div class="modalFooter">
          <button class="btn" @click="closeEdit">取消</button>
          <button class="btn primary" @click="saveEdit">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="ActivityAttendance">
import { ymdHm } from '@/utils/time'
import { apiGet, http } from '@/api/http'
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/stores/app'

type Role = '管理员' | '教师' | '辅导员' | '学生'
type AttendStatusCore = '已出勤' | '未出勤' | '请假'
type AttendStatusView = AttendStatusCore | '未到签到时间'

type AttendanceItem = {
  userId: string
  userName: string
  signInAt: string
  attendStatus: AttendStatusCore
  leaveApplyId?: string
  isLate?: boolean
}

type ActivityItem = {
  id: number
  title: string
  leaderId: string
  leaderName: string
  startAt: string
  location: string
  attendance: AttendanceItem[]
}

const appStore = useAppStore()
const me = computed(() => appStore.user as { id: string; name: string; role: Role; joinedActivityIds?: number[] })

const activities = ref<ActivityItem[]>([])

const loadActivities = async () => {
  const actRes = await apiGet<any[]>('/api/activities')

  const list = await Promise.all(
    (actRes || []).map(async (item) => {
      const attRes = await apiGet<any[]>(`/api/activities/${item.id}/attendance`)
      return {
        id: item.id,
        title: item.title,
        leaderId: item.leaderId ?? item.leader_id,
        leaderName: item.leaderName ?? item.leader_name,
        startAt: item.startAt ?? item.start_at,
        location: item.location,
        attendance: (attRes || []).map((row) => ({
          userId: row.userId ?? row.user_id,
          userName: row.userName ?? row.user_name,
          signInAt: row.signInAt ?? row.sign_in_at ?? '',
          attendStatus: row.attendStatus ?? row.attend_status,
          leaveApplyId: row.leaveApplyId ?? row.leave_apply_id ?? '',
          isLate: !!(row.isLate ?? row.is_late)
        }))
      } as ActivityItem
    })
  )

  activities.value = list
}

onMounted(() => {
  loadActivities().catch((err: any) => {
    window.alert(err?.message || '活动出勤加载失败')
  })
})

const queryTitle = ref('')
const appliedTitle = ref('')
const doSearch = () => (appliedTitle.value = queryTitle.value)
const resetSearch = () => {
  queryTitle.value = ''
  appliedTitle.value = ''
}
const key = computed(() => appliedTitle.value.trim())

const nowMs = () => Date.now()
const isNotStarted = (startAt: string) => {
  const t = new Date(startAt).getTime()
  return Number.isFinite(t) ? t > nowMs() : false
}

const isJoined = (activityId: number) => (me.value.joinedActivityIds ?? []).includes(activityId)

const lateText = (status: AttendStatusView, isLate: boolean) => {
  if (status !== '已出勤') return '-'
  return isLate ? '迟到' : '-'
}

const joinedRows = computed(() => {
  const k = key.value
  const rows: Array<{
    activityId: number
    title: string
    startAt: string
    location: string
    userId: string
    userName: string
    attendStatus: AttendStatusView
    signInAt: string
    isLate: boolean
  }> = []

  for (const a of activities.value) {
    if (k && !a.title.includes(k)) continue
    if (!isJoined(a.id)) continue

    const notStarted = isNotStarted(a.startAt)
    const mine = a.attendance?.find((x) => x.userId === me.value.id)

    if (mine) {
      rows.push({
        activityId: a.id,
        title: a.title,
        startAt: a.startAt,
        location: a.location,
        userId: mine.userId,
        userName: mine.userName,
        attendStatus: notStarted ? '未到签到时间' : mine.attendStatus,
        signInAt: notStarted ? '' : mine.signInAt,
        isLate: !!mine.isLate
      })
    } else {
      rows.push({
        activityId: a.id,
        title: a.title,
        startAt: a.startAt,
        location: a.location,
        userId: me.value.id,
        userName: me.value.name,
        attendStatus: notStarted ? '未到签到时间' : '未出勤',
        signInAt: '',
        isLate: false
      })
    }
  }
  return rows
})

const canSeeManaged = computed(() => me.value.role === '教师' || me.value.role === '管理员')

const managedRows = computed(() => {
  if (!canSeeManaged.value) return []
  const k = key.value

  const acts = me.value.role === '管理员' ? activities.value : activities.value.filter((a) => a.leaderId === me.value.id)

  const rows: Array<{
    key: string
    activityId: number
    title: string
    userId: string
    userName: string
    attendStatus: AttendStatusView
    signInAt: string
    isLate: boolean
  }> = []

  for (const a of acts) {
    if (k && !a.title.includes(k)) continue
    const notStarted = isNotStarted(a.startAt)

    for (const at of a.attendance ?? []) {
      rows.push({
        key: `${a.id}-${at.userId}`,
        activityId: a.id,
        title: a.title,
        userId: at.userId,
        userName: at.userName,
        attendStatus: notStarted ? '未到签到时间' : at.attendStatus,
        signInAt: notStarted ? '' : at.signInAt,
        isLate: !!at.isLate
      })
    }
  }
  return rows
})

const canEdit = (activityId: number, viewStatus: AttendStatusView) => {
  const act = activities.value.find((x) => x.id === activityId)
  if (!act) return false
  if (isNotStarted(act.startAt) || viewStatus === '未到签到时间') return false

  if (me.value.role === '管理员') return true
  if (me.value.role === '教师') return act.leaderId === me.value.id
  return false
}

const editVisible = ref(false)
const editInfo = ref<null | { activityId: number; activityTitle: string; userId: string; userName: string }>(null)
const editAttendStatus = ref<AttendStatusCore>('未出勤')
const editIsLate = ref<boolean>(false)

const openEdit = (activityId: number, userId: string) => {
  const act = activities.value.find((x) => x.id === activityId)
  if (!act) return

  const at = act.attendance?.find((x) => x.userId === userId)
  editInfo.value = {
    activityId,
    activityTitle: act.title,
    userId,
    userName: at?.userName ?? userId
  }

  editAttendStatus.value = at?.attendStatus ?? '未出勤'
  editIsLate.value = !!at?.isLate
  editVisible.value = true
}

const closeEdit = () => {
  editVisible.value = false
  editInfo.value = null
}

const saveEdit = async () => {
  if (!editInfo.value) return

  try {
    const { activityId, userId } = editInfo.value
    const res = await http.put<any>(`/api/activities/${activityId}/attendance/${userId}`, {
      attendStatus: editAttendStatus.value,
      isLate: editAttendStatus.value === '已出勤' ? !!editIsLate.value : false
    })

    const act = activities.value.find((x) => x.id === activityId)
    if (act) {
      const row = act.attendance.find((x) => x.userId === userId)
      const next = {
        userId: res.data.userId ?? res.data.user_id,
        userName: res.data.userName ?? res.data.user_name,
        signInAt: res.data.signInAt ?? res.data.sign_in_at ?? '',
        attendStatus: res.data.attendStatus ?? res.data.attend_status,
        leaveApplyId: res.data.leaveApplyId ?? res.data.leave_apply_id ?? '',
        isLate: !!(res.data.isLate ?? res.data.is_late)
      }

      if (row) {
        Object.assign(row, next)
      } else {
        act.attendance.push(next)
      }
    }

    closeEdit()
  } catch (err: any) {
    window.alert(err?.message || '保存失败')
  }
}

const attClass = (s: AttendStatusView) => {
  switch (s) {
    case '已出勤':
      return 'approved'
    case '请假':
      return 'pending'
    case '未出勤':
      return 'rejected'
    case '未到签到时间':
      return 'pending'
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
.input {
  height: 32px;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 0 10px;
  font-size: 14px;
  outline: none;
  background: #fff;
  width: 220px;
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

.blockTitle {
  padding: 12px 30px 8px;
  font-weight: bold;
  color: #333;
}

.gridRowMine {
  display: grid;
  grid-template-columns: 1.35fr 1fr 1fr 0.6fr 0.8fr 1fr 80px;
  column-gap: 6px;
  align-items: center;
}
.gridRowManage {
  display: grid;
  grid-template-columns: 1.35fr 0.9fr 0.6fr 0.8fr 1fr 80px;
  column-gap: 6px;
  align-items: center;
}

.opsHeaderMine,
.opsMine,
.opsHeaderManage,
.opsManage {
  justify-self: end;
  text-align: right;
}

.tableHeader {
  padding: 10px 30px;
  border-bottom: 1px solid #eee;
  color: #666;
  font-size: 13px;
  font-weight: bold;
}

.list {
  min-height: 100px;
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
.empty {
  padding: 18px 30px;
  color: #999;
}
.emptyBox {
  margin: 0 30px;
  padding: 14px;
  background: #fff8f0;
  border: 1px solid #ffe1c2;
  border-radius: 8px;
  color: #b26a00;
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
.opBtn:disabled {
  color: #aaa;
  cursor: not-allowed;
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
  width: 520px;
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
  gap: 12px;
}
.kv {
  display: grid;
  grid-template-columns: 80px 1fr;
  gap: 12px;
  font-size: 14px;
}
.k {
  color: #666;
}
.v {
  color: #333;
}
.formRow {
  display: flex;
  align-items: center;
  gap: 10px;
}
.select {
  height: 32px;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 0 10px;
  outline: none;
}
.radioLine {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #333;
}
.hint {
  color: #999;
  font-size: 12px;
}
.modalFooter {
  padding: 12px 16px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
