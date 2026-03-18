<template>
  <div class="userManagement">
    <div class="panel">
      <div class="queryBar">
        <div class="field">
          <span class="label">用户名</span>
          <input class="input" v-model.trim="queryName" placeholder="请输入用户名" />
        </div>

        <div class="field">
          <span class="label">身份</span>
          <select class="select" v-model="queryRole">
            <option value="">全部</option>
            <option value="管理员">管理员</option>
            <option value="教师">教师</option>
            <option value="辅导员">辅导员</option>
            <option value="学生">学生</option>
          </select>
        </div>

        <button class="btn primary" @click="doSearch">查询</button>
        <button class="btn" @click="resetSearch">重置</button>
      </div>

      <div class="tableHeader gridRow">
        <span class="cell">ID</span>
        <span class="cell">用户名</span>
        <span class="cell">身份</span>
        <span class="cell">密码</span>
        <span class="cell">性别</span>
        <span class="cell">所属部门</span>
        <span class="cell">电话</span>
        <span class="cell opsHeader">操作</span>
      </div>

      <ul class="list">
        <li v-for="u in pagedUsers" :key="u.id" class="list-item">
          <div class="gridRow row">
            <span class="cell ellipsis" :title="u.id">{{ u.id }}</span>
            <span class="cell ellipsis" :title="u.name">{{ u.name }}</span>

            <span class="cell">
              <span class="roleTag" :class="roleClass(u.role)">{{ u.role }}</span>
            </span>

            <span class="cell ellipsis pwd" :title="u.password">{{ u.password }}</span>
            <span class="cell">{{ u.gender }}</span>
            <span class="cell ellipsis dept" :title="u.department">{{ u.department }}</span>
            <span class="cell ellipsis phone" :title="u.phone">{{ u.phone }}</span>

            <span class="cell ops">
              <div class="opsInner">
                <button class="opBtn" @click="openEdit(u)">编辑</button>
                <span class="sep">|</span>
                <button class="opBtn danger" @click="removeUser(u.id, u.name)">删除</button>
              </div>
            </span>
          </div>
        </li>

        <li v-if="pagedUsers.length === 0" class="empty">暂无符合条件的用户</li>
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

      <div v-if="editVisible" class="modalMask" @click.self="closeEdit">
        <div class="modal modalWide">
          <div class="modalHeader">
            <div class="modalTitle">编辑用户信息</div>
            <button class="closeBtn" @click="closeEdit">×</button>
          </div>

          <div class="modalBody twoCol">
            <div class="col">
              <div class="sectionTitle">用户详细信息</div>

              <div class="kv"><span class="k">ID</span><span class="v">{{ editUser?.id }}</span></div>
              <div class="kv"><span class="k">用户名</span><span class="v">{{ editUser?.name }}</span></div>
              <div class="kv"><span class="k">身份</span><span class="v">{{ editUser?.role }}</span></div>
              <div class="kv"><span class="k">密码</span><span class="v">{{ editUser?.password }}</span></div>
              <div class="kv"><span class="k">性别</span><span class="v">{{ editUser?.gender }}</span></div>
              <div class="kv"><span class="k">所属部门</span><span class="v">{{ editUser?.department }}</span></div>
              <div class="kv"><span class="k">电话</span><span class="v">{{ editUser?.phone }}</span></div>
            </div>

            <div class="col">
              <div class="sectionTitle">更改信息</div>

              <div class="formRow">
                <span class="label">ID</span>
                <input class="input" v-model.trim="editForm.id" disabled />
              </div>

              <div class="formRow">
                <span class="label">用户名</span>
                <input class="input" v-model.trim="editForm.name" placeholder="请输入用户名" />
              </div>

              <div class="formRow">
                <span class="label">身份</span>
                <select class="select" v-model="editForm.role">
                  <option value="管理员">管理员</option>
                  <option value="教师">教师</option>
                  <option value="辅导员">辅导员</option>
                  <option value="学生">学生</option>
                </select>
              </div>

              <div class="formRow">
                <span class="label">密码</span>
                <input class="input" v-model.trim="editForm.password" placeholder="请输入密码" />
              </div>

              <div class="formRow">
                <span class="label">性别</span>
                <select class="select" v-model="editForm.gender">
                  <option value="男">男</option>
                  <option value="女">女</option>
                </select>
              </div>

              <div class="formRow">
                <span class="label">所属部门</span>
                <input class="input" v-model.trim="editForm.department" placeholder="请输入所属部门" />
              </div>

              <div class="formRow">
                <span class="label">电话</span>
                <input class="input" v-model.trim="editForm.phone" placeholder="请输入电话" />
              </div>
            </div>
          </div>

          <div class="modalFooter">
            <button class="btn" @click="closeEdit">取消</button>
            <button class="btn primary" :disabled="!editUser" @click="submitEdit">更改</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="UserList">
import { ref, computed, onMounted } from 'vue'
import {
  getUsers,
  updateUser,
  deleteUser,
  type UserItem,
  type UserRole,
  type Gender,
} from '@/api/user'

const users = ref<UserItem[]>([])
const loading = ref(false)

async function loadUsers() {
  loading.value = true
  try {
    users.value = await getUsers()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUsers()
})

const queryName = ref('')
const queryRole = ref<UserRole | ''>('')

const appliedName = ref('')
const appliedRole = ref<UserRole | ''>('')

const doSearch = () => {
  appliedName.value = queryName.value
  appliedRole.value = queryRole.value
  currentPage.value = 1
}

const resetSearch = () => {
  queryName.value = ''
  queryRole.value = ''
  appliedName.value = ''
  appliedRole.value = ''
  currentPage.value = 1
}

const filteredUsers = computed(() => {
  const name = appliedName.value.trim()
  const role = appliedRole.value

  return users.value.filter((u) => {
    const matchName = name ? u.name.includes(name) : true
    const matchRole = role ? u.role === role : true
    return matchName && matchRole
  })
})

const pageSize = ref(12)
const currentPage = ref(1)
const jumpPage = ref<number | null>(null)

const total = computed(() => filteredUsers.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredUsers.value.slice(start, start + pageSize.value)
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

const removeUser = async (id: string, name?: string) => {
  const label = name ? `${name}（${id}）` : id
  const ok = window.confirm(`确认删除用户：${label} 吗？\n删除后将无法恢复。`)
  if (!ok) return

  await deleteUser(id)

  const idx = users.value.findIndex((u) => u.id === id)
  if (idx !== -1) users.value.splice(idx, 1)

  const newTotalPages = Math.max(1, Math.ceil(filteredUsers.value.length / pageSize.value))
  if (currentPage.value > newTotalPages) currentPage.value = newTotalPages
}

const roleClass = (role: UserRole) => {
  switch (role) {
    case '管理员':
      return 'admin'
    case '教师':
      return 'teacher'
    case '辅导员':
      return 'counselor'
    case '学生':
      return 'student'
  }
}

const editVisible = ref(false)
const editUser = ref<UserItem | null>(null)

type EditForm = {
  id: string
  name: string
  role: UserRole
  password: string
  gender: Gender
  avatar?: string
  department: string
  phone: string
}

const emptyEditForm = (): EditForm => ({
  id: '',
  name: '',
  role: '学生',
  password: '',
  gender: '男',
  avatar: '/AnonymousAvatar.png',
  department: '',
  phone: '',
})

const editForm = ref<EditForm>(emptyEditForm())

const openEdit = (u: UserItem) => {
  editUser.value = u
  editForm.value = {
    id: u.id,
    name: u.name,
    role: u.role,
    password: u.password,
    gender: u.gender,
    avatar: u.avatar,
    department: u.department,
    phone: u.phone,
  }
  editVisible.value = true
}

const closeEdit = () => {
  editVisible.value = false
  editUser.value = null
  editForm.value = emptyEditForm()
}

const submitEdit = async () => {
  if (!editUser.value) return

  const updated = await updateUser(editUser.value.id, {
    name: editForm.value.name,
    role: editForm.value.role,
    password: editForm.value.password,
    gender: editForm.value.gender,
    avatar: editForm.value.avatar,
    department: editForm.value.department,
    phone: editForm.value.phone,
  })

  const idx = users.value.findIndex((x) => x.id === editUser.value!.id)
  if (idx !== -1) users.value[idx] = updated

  closeEdit()
}
</script>

<style scoped>
.userManagement {
  height: 100%;
  background: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 16px 30px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: flex-end;
  justify-content: flex-start;
  gap: 16px;
}

.header h2 {
  margin: 0;
}

.subnav {
  display: flex;
  gap: 10px;
  margin-left: 14px;
}

.subnav-item {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 999px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  user-select: none;
}

.subnav-item.active {
  border-color: #4c98d4;
  color: #4c98d4;
  background: #e8f1fb;
}

.panel {
  flex: 1;
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

.gridRow {
  display: grid;
  grid-template-columns: 1.05fr 1.05fr 0.85fr 1fr 0.7fr 1.25fr 1.05fr 120px;
  column-gap: 6px;
  align-items: center;
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

.pwd,
.dept,
.phone {
  color: #666;
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
  gap: 0;
  flex-wrap: wrap;
}

.roleTag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 20px;
  padding: 0 8px;
  border-radius: 4px;
  font-size: 12px;
}

.roleTag.admin {
  background: #fde2e2;
  color: #d93026;
}
.roleTag.teacher {
  background: #e8f1fb;
  color: #4c98d4;
}
.roleTag.counselor {
  background: #e7f7ee;
  color: #1e8e3e;
}
.roleTag.student {
  background: #f3e8ff;
  color: #7c3aed;
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
  font-size: 18px;
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

.input:disabled {
  background: #f7f7f7;
  color: #666;
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
