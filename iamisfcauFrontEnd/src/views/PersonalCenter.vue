<template>
  <div class="personalCenter">
    <!-- 上 1/3 -->
    <div class="top-section">
      <!-- 左：头像 + 基本信息 -->
      <div class="profile">
        <div class="profileHeader">
          <div class="profileTitle">个人信息</div>
          <button class="more profileMore" @click="openProfileMore">更多</button>
        </div>

        <div class="profileBody">
          <img class="avatar" :src="avatarUrl" @error="onAvatarError" alt="avatar" />
          <div class="info">
            <div class="name">{{ user.name || '-' }}</div>
            <div class="college">{{ displayCollege }}</div>
            <div class="id">学号：{{ user.id || '-' }}</div>
            <div class="roleLine">身份：{{ user.role || '-' }}</div>
          </div>
        </div>
      </div>

      <!-- 右：通知 -->
      <div class="notice">
        <div class="notice-header">
          <div class="notice-title">通知</div>
          <a
            :href="router.resolve('/home/notification').href"
            target="_blank"
            rel="noopener noreferrer"
            class="more"
          >
            更多
          </a>
        </div>

        <ul class="notice-list">
          <li v-for="item in notices" :key="item.id" class="notice-item">
            <a
              :href="router.resolve(`/home/notification/${item.id}`).href"
              target="_blank"
              rel="noopener noreferrer"
              class="notice-link"
            >
              <span v-for="tag in item.tags" :key="tag" class="tag" :class="tag">
                {{ tag === 'top' ? '置顶' : '通知' }}
              </span>

              <span class="content">{{ item.title }}</span>
              <span class="time">{{ ymd(item.time) }}</span>
            </a>
          </li>
        </ul>
      </div>
    </div>

    <!-- 下 2/3 -->
    <div class="bottom-section">
      <div class="tabs">
        <div class="tab-group">
          <div class="tab" :class="{ active: activeTab === 'activity' }" @click="activeTab = 'activity'">
            活动
          </div>
          <div class="tab" :class="{ active: activeTab === 'message' }" @click="activeTab = 'message'">
            消息
          </div>
        </div>

        <a
          v-if="activeTab === 'activity'"
          :href="router.resolve('/home/activity').href"
          target="_blank"
          rel="noopener noreferrer"
          class="more"
        >
          更多
        </a>
        <router-link v-else to="/home/messageCenter" class="more">更多</router-link>
      </div>

      <div class="tab-content">
        <ul v-if="activeTab === 'activity'" class="list">
          <li v-for="a in activities" :key="a.id" class="list-item">
            <a
              :href="router.resolve(`/home/activity/${a.id}`).href"
              target="_blank"
              rel="noopener noreferrer"
              class="list-link"
            >
              <span class="date">{{ ymd(a.date) }}</span>
              <span class="text">{{ a.title }}</span>
              <span class="extra">{{ a.location }}</span>
            </a>
          </li>
        </ul>

        <ul v-else class="list">
          <li v-for="m in messageConversations" :key="m.id" class="list-item">
            <router-link :to="`/home/messageCenter/${m.id}`" class="list-link">
              <span class="text">{{ m.name }}：{{ getLastOtherMessage(m.messages) }}</span>
              <span class="date right">{{ ymd(m.updatedAt) }}</span>
            </router-link>
          </li>
        </ul>
      </div>
    </div>

    <!-- 个人信息弹窗 -->
    <div v-if="profileModalVisible" class="modalMask" @click.self="closeProfileMore">
      <div class="modal modalWide">
        <div class="modalHeader">
          <div class="modalTitle">个人信息</div>
          <button class="closeBtn" @click="closeProfileMore">×</button>
        </div>

        <div class="modalBody twoCol">
          <!-- 左侧：当前信息 -->
          <div class="col">
            <div class="sectionTitle">当前信息</div>

            <div class="kv"><span class="k">学号</span><span class="v">{{ currentUser?.id || user.id }}</span></div>
            <div class="kv"><span class="k">姓名</span><span class="v">{{ currentUser?.name || user.name }}</span></div>
            <div class="kv"><span class="k">身份</span><span class="v">{{ currentUser?.role || user.role }}</span></div>

            <div class="kv"><span class="k">学院</span><span class="v">{{ currentUser?.department || user.college || '-' }}</span></div>
            <div class="kv"><span class="k">性别</span><span class="v">{{ currentUser?.gender || '-' }}</span></div>
            <div class="kv"><span class="k">电话</span><span class="v">{{ currentUser?.phone || '-' }}</span></div>
            <div class="kv"><span class="k">密码</span><span class="v">{{ currentUser?.password || '-' }}</span></div>
          </div>

          <!-- 右侧：更改信息 -->
          <div class="col">
            <div class="sectionTitle">更改信息</div>

            <div class="formRow">
              <span class="label">学号</span>
              <input class="input" v-model.trim="editForm.id" disabled />
            </div>

            <div class="formRow">
              <span class="label">姓名</span>
              <input class="input" v-model.trim="editForm.name" disabled />
            </div>

            <div class="formRow">
              <span class="label">身份</span>
              <input class="input" v-model.trim="editForm.role" disabled />
            </div>

            <div class="formRow">
              <span class="label">学院</span>
              <input class="input" v-model.trim="editForm.department" placeholder="请输入学院（所属部门）" />
            </div>

            <div class="formRow">
              <span class="label">性别</span>
              <select class="select" v-model="editForm.gender">
                <option value="男">男</option>
                <option value="女">女</option>
              </select>
            </div>

            <div class="formRow">
              <span class="label">电话</span>
              <input class="input" v-model.trim="editForm.phone" placeholder="请输入电话" />
            </div>

            <div class="formRow">
              <span class="label">密码</span>
              <input class="input" v-model.trim="editForm.password" placeholder="请输入密码" />
            </div>
          </div>
        </div>

        <div class="modalFooter">
          <button class="btn" @click="closeProfileMore">取消</button>
          <button class="btn primary" @click="submitProfileChange">提交申请</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="PersonalCenter">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ymd } from '@/utils/time'
import { apiGet, apiPost } from '@/api/http'

type UserRole = '管理员' | '教师' | '辅导员' | '学生' | string
type Gender = '男' | '女'
type AuditStatus = '待审核' | '已同意' | '已拒绝'

type UserMe = {
  id: string
  name: string
  role: UserRole
  college?: string | null
  department?: string | null
  avatar?: string | null
  gender?: Gender | null
  phone?: string | null
  password?: string | null
  isLogin?: boolean
}

type NoticeItem = {
  id: number
  title: string
  content: string
  tags: string[]
  time: string
}

type ActivityItem = {
  id: number
  title: string
  organizer: string
  leaderId: string
  leaderName: string
  date: string
  time: string
  startAt: string
  endAt: string
  location: string
  summary: string
  content: string
}

type MessageItem = {
  role?: 'system' | 'other' | 'self' | string
  content?: string
  time?: string
  sentAt?: string
  type?: string
}

type MessageConversationItem = {
  id: string
  name: string
  avatar?: string
  updatedAt: string
  messages: MessageItem[]
}

type UserItem = {
  id: string
  name: string
  role: UserRole
  password: string
  gender: Gender
  department: string
  phone: string
  avatar?: string
  college?: string
}

type AuditItem = {
  applyId: string
  userName: string
  userId: string
  applicantName: string
  applicantId: string
  changeType: string
  before: string
  after: string
  submittedAt: string
  status: AuditStatus
  approvalComment: string
}

const router = useRouter()

const user = ref<UserMe>({
  id: '',
  name: '',
  role: '',
  college: '',
  department: '',
  avatar: '',
  gender: '男',
  phone: '',
  password: '',
  isLogin: true
})

const notices = ref<NoticeItem[]>([])
const activities = ref<ActivityItem[]>([])
const messageConversations = ref<MessageConversationItem[]>([])
const auditRecords = ref<AuditItem[]>([])

const activeTab = ref<'activity' | 'message'>('activity')
const loading = ref(false)

const currentUser = computed<UserItem | null>(() => {
  if (!user.value.id) return null
  return {
    id: user.value.id,
    name: user.value.name,
    role: user.value.role,
    password: user.value.password || '',
    gender: (user.value.gender as Gender) || '男',
    department: user.value.department || user.value.college || '',
    phone: user.value.phone || '',
    avatar: user.value.avatar || '',
    college: user.value.college || ''
  }
})

const displayCollege = computed(() => {
  return currentUser.value?.department || user.value.college || '-'
})

const avatarUrl = computed(() => user.value.avatar || '/AnonymousAvatar.png')

const onAvatarError = (e: Event) => {
  ;(e.target as HTMLImageElement).src = '/AnonymousAvatar.png'
}

const getLastOtherMessage = (messages: MessageItem[] = []) => {
  const msg = [...messages]
    .reverse()
    .find((item) => item && item.role !== 'self' && item.type !== 'time')

  return msg?.content ?? ''
}

const profileModalVisible = ref(false)

type EditForm = {
  id: string
  name: string
  role: string
  department: string
  gender: Gender
  phone: string
  password: string
}

const editForm = ref<EditForm>({
  id: '',
  name: '',
  role: '',
  department: '',
  gender: '男',
  phone: '',
  password: ''
})

type PickResult =
  | { type: 'none' }
  | { type: 'multi'; count: number }
  | { type: 'single'; key: keyof UserItem; label: string }

const pickChangedField = (before: UserItem, after: UserItem): PickResult => {
  const changes: Array<{ key: keyof UserItem; label: string }> = []

  if (before.phone !== after.phone) changes.push({ key: 'phone', label: '电话变更' })
  if (before.department !== after.department) changes.push({ key: 'department', label: '所属部门变更' })
  if (before.gender !== after.gender) changes.push({ key: 'gender', label: '性别变更' })
  if (before.password !== after.password) changes.push({ key: 'password', label: '密码变更' })

  if (changes.length === 0) return { type: 'none' }
  if (changes.length === 1) return { type: 'single', key: changes[0]!.key, label: changes[0]!.label }
  return { type: 'multi', count: changes.length }
}

async function fetchUserMe() {
  const res = await apiGet<any>('/api/users/me')
  user.value = {
    id: res.id,
    name: res.name,
    role: res.role,
    college: res.college ?? res.department ?? '',
    department: res.department ?? res.college ?? '',
    avatar: res.avatar ?? '',
    gender: res.gender ?? '男',
    phone: res.phone ?? '',
    password: res.password ?? '',
    isLogin: true
  }
}

async function fetchNotices() {
  const res = await apiGet<any[]>('/api/notices')
  notices.value = (res || []).map((item) => ({
    id: item.id,
    title: item.title,
    content: item.content,
    tags: Array.isArray(item.tags) ? item.tags : [],
    time: item.time ?? item.noticeTime ?? item.notice_time ?? ''
  }))
}

async function fetchActivities() {
  const res = await apiGet<any[]>('/api/activities')
  activities.value = (res || []).map((item) => ({
    id: item.id,
    title: item.title,
    organizer: item.organizer,
    leaderId: item.leaderId ?? item.leader_id ?? '',
    leaderName: item.leaderName ?? item.leader_name ?? '',
    date: item.date ?? item.activityDate ?? item.activity_date ?? item.startAt ?? item.start_at ?? '',
    time: item.time ?? item.activityTime ?? item.activity_time ?? '',
    startAt: item.startAt ?? item.start_at ?? '',
    endAt: item.endAt ?? item.end_at ?? '',
    location: item.location ?? '',
    summary: item.summary ?? '',
    content: item.content ?? ''
  }))
}

async function fetchAuditRecords() {
  const res = await apiGet<any[]>('/api/profile-change-applications')
  auditRecords.value = (res || []).map((item) => ({
    applyId: item.applyId ?? item.apply_id,
    userName: item.userName ?? item.targetUserName ?? item.target_user_name,
    userId: item.userId ?? item.targetUserId ?? item.target_user_id,
    applicantName: item.applicantName ?? item.applicant_name,
    applicantId: item.applicantId ?? item.applicant_id,
    changeType: item.changeType ?? item.change_type,
    before: item.before ?? item.beforeValue ?? item.before_value ?? '',
    after: item.after ?? item.afterValue ?? item.after_value ?? '',
    submittedAt: item.submittedAt ?? item.submitted_at ?? '',
    status: item.status,
    approvalComment: item.approvalComment ?? item.approval_comment ?? ''
  }))
}

async function fetchMessageConversations() {
  const conversations = await apiGet<any[]>('/api/messages/conversations')

  const fullList = await Promise.all(
    (conversations || []).map(async (conv) => {
      const messagesRes = await apiGet<any[]>(`/api/messages/conversations/${conv.id}/messages`)
      const messages: MessageItem[] = (messagesRes || []).map((msg) => ({
        role: msg.role,
        content: msg.content,
        time: msg.time ?? msg.sentAt ?? msg.sent_at ?? '',
        sentAt: msg.sentAt ?? msg.sent_at ?? ''
      }))

      return {
        id: conv.id,
        name: conv.name,
        avatar: conv.avatar ?? '',
        updatedAt: conv.updatedAt ?? conv.updated_at ?? '',
        messages
      }
    })
  )

  messageConversations.value = fullList
}

async function loadPageData() {
  try {
    loading.value = true
    await Promise.all([
      fetchUserMe(),
      fetchNotices(),
      fetchActivities(),
      fetchAuditRecords(),
      fetchMessageConversations()
    ])
  } catch (error: any) {
    window.alert(error?.message || '页面数据加载失败')
  } finally {
    loading.value = false
  }
}

const openProfileMore = () => {
  const base = currentUser.value
  editForm.value = {
    id: base?.id || user.value.id,
    name: base?.name || user.value.name,
    role: String(base?.role || user.value.role || ''),
    department: base?.department || user.value.college || '',
    gender: base?.gender || '男',
    phone: base?.phone || '',
    password: base?.password || ''
  }
  profileModalVisible.value = true
}

const closeProfileMore = () => {
  profileModalVisible.value = false
}

const submitProfileChange = async () => {
  const base = currentUser.value
  if (!base) {
    closeProfileMore()
    return
  }

  const beforeUser: UserItem = { ...base }

  const afterUser: UserItem = {
    ...beforeUser,
    department: editForm.value.department,
    gender: editForm.value.gender,
    phone: editForm.value.phone,
    password: editForm.value.password,
    id: beforeUser.id,
    name: beforeUser.name,
    role: beforeUser.role
  }

  const picked = pickChangedField(beforeUser, afterUser)

  if (picked.type === 'none') {
    window.alert('未检测到修改内容')
    return
  }

  if (picked.type === 'multi') {
    window.alert('一次只允许修改一个字段，请只修改一项后再提交申请。')
    return
  }

  try {
    await apiPost('/api/profile-change-applications', {
      targetUserId: beforeUser.id,
      applicantId: beforeUser.id,
      changeType: picked.label,
      beforeValue: String(beforeUser[picked.key] ?? ''),
      afterValue: String(afterUser[picked.key] ?? '')
    })

    window.alert('提交成功')
    closeProfileMore()
    await fetchAuditRecords()
  } catch (error: any) {
    window.alert(error?.message || '提交失败')
  }
}

onMounted(() => {
  loadPageData()
})
</script>

<style scoped>
.personalCenter {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: hidden;
}

.top-section {
  flex: 1;
  display: flex;
  gap: 20px;
  overflow: hidden;
}

.profile {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.profileHeader {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.profileTitle {
  font-size: 16px;
  font-weight: bold;
}

.profileMore {
  margin-left: auto;
  margin-right: 0px;
  background: transparent;
  border: none;
  padding: 0;
  cursor: pointer;
}

.profileBody {
  flex: 1;
  display: flex;
  align-items: center;
}

.avatar {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  object-fit: cover;
  margin: 0 30px 0 20px;
  background: #e3efff;
}

.info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.name {
  font-size: 20px;
  font-weight: bold;
}

.college,
.id,
.roleLine {
  font-size: 14px;
  color: #666;
}

.notice {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notice-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.notice-title {
  font-size: 16px;
  font-weight: bold;
}

.notice-header .more {
  margin-left: auto;
}

.notice-list {
  flex: 1;
  list-style: none;
  padding: 0;
  margin: 0;
  overflow-y: auto;
}

.notice-item {
  margin-bottom: 8px;
}

.notice-link {
  display: flex;
  align-items: center;
  gap: 4px;
  width: 100%;
  text-decoration: none;
  color: inherit;
}

.tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 18px;
  padding: 0 6px;
  font-size: 12px;
  border-radius: 4px;
  white-space: nowrap;
  flex-shrink: 0;
}

.tag.top {
  background: #fde2e2;
  color: #d93026;
}

.tag.normal {
  background: #e8f1fb;
  color: #4c98d4;
}

.notice-link .content {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.time {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
}

.bottom-section {
  flex: 2;
  background: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tabs {
  display: flex;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.tab-group {
  display: flex;
}

.tab {
  padding: 12px 20px;
  cursor: pointer;
  color: #666;
}

.tab.active {
  color: #4c98d4;
  font-weight: bold;
  border-bottom: 2px solid #4c98d4;
}

.more {
  margin-left: auto;
  margin-right: 16px;
  font-size: 14px;
  color: #4c98d4;
  text-decoration: none;
}

.more:hover {
  text-decoration: underline;
}

.tab-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-item {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.list-link {
  display: flex;
  align-items: center;
  width: 100%;
  text-decoration: none;
  color: inherit;
}

.list-link .date {
  width: 90px;
  color: #999;
  white-space: nowrap;
}

.list-link .text {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.list-link .extra {
  margin-left: auto;
  color: #666;
  white-space: nowrap;
}

.right {
  margin-left: auto;
}

/* 弹窗风格 */
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
  padding: 6px 0;
}

.k {
  color: #666;
}

.v {
  color: #333;
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

.input:disabled {
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
</style>