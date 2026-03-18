<template>
  <div class="userManagementShell">
    <!-- 标题 -->
    <div class="header">
      <div class="left">
        <h2>用户管理</h2>

        <div class="subnav">
          <router-link to="/home/userManagement/list" class="subnav-item" active-class="active">
            用户列表
          </router-link>
          <router-link to="/home/userManagement/audit" class="subnav-item" active-class="active">
            信息审核
          </router-link>
        </div>
      </div>

      <!-- 右侧：创建新用户按钮 -->
      <button class="createBtn" @click="openCreate">
        <span class="plus">＋</span>
        创建新用户
      </button>
    </div>

    <div class="panel">
      <router-view />
    </div>

    <!-- 创建用户弹窗 -->
    <div v-if="create.visible" class="mask" @click.self="closeCreate">
      <div class="dialog">
        <div class="dialogTop">
          <div class="dialogTitle">创建新用户</div>
          <button class="iconBtn" @click="closeCreate">关闭</button>
        </div>

        <div class="form">
          <div class="row">
            <div class="field">
              <div class="label">学号/工号（id）</div>
              <input v-model="create.form.id" class="input" placeholder="例如：20250001" />
            </div>

            <div class="field">
              <div class="label">姓名</div>
              <input v-model="create.form.name" class="input" placeholder="例如：张三" />
            </div>
          </div>

          <div class="row">
            <div class="field">
              <div class="label">角色</div>
              <select v-model="create.form.role" class="input">
                <option value="学生">学生</option>
                <option value="教师">教师</option>
                <option value="辅导员">辅导员</option>
                <option value="管理员">管理员</option>
              </select>
            </div>

            <div class="field">
              <div class="label">性别</div>
              <select v-model="create.form.gender" class="input">
                <option value="男">男</option>
                <option value="女">女</option>
              </select>
            </div>
          </div>

          <div class="row">
            <div class="field">
              <div class="label">所属部门</div>
              <input v-model="create.form.department" class="input" placeholder="例如：计算机学院" />
            </div>

            <div class="field">
              <div class="label">电话</div>
              <input v-model="create.form.phone" class="input" placeholder="例如：13800000009" />
            </div>
          </div>

          <div class="row">
            <div class="field">
              <div class="label">登录密码</div>
              <input
                v-model="create.form.password"
                class="input"
                type="password"
                placeholder="例如：student123"
              />
            </div>

            <div class="field">
              <div class="label">头像（可选）</div>
              <input v-model="create.form.avatar" class="input" placeholder="例如：/AnonymousAvatar.png" />
            </div>
          </div>

          <div v-if="create.error" class="error">{{ create.error }}</div>
        </div>

        <div class="dialogActions">
          <button class="btn" @click="closeCreate">取消</button>
          <button class="btn primary" @click="submitCreate">创建</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="UserManagement">
import { reactive } from 'vue'
import { createUser, type UserRole, type Gender } from '@/api/user'

type CreateForm = {
  id: string
  name: string
  role: UserRole
  password: string
  gender: Gender
  avatar: string
  department: string
  phone: string
}

const defaultForm = (): CreateForm => ({
  id: '',
  name: '',
  role: '学生',
  password: 'student123',
  gender: '男',
  avatar: '/AnonymousAvatar.png',
  department: '',
  phone: '',
})

const create = reactive<{
  visible: boolean
  error: string
  form: CreateForm
}>({
  visible: false,
  error: '',
  form: defaultForm()
})

function openCreate() {
  create.visible = true
  create.error = ''
}

function closeCreate() {
  create.visible = false
  create.error = ''
  create.form = defaultForm()
}

async function submitCreate() {
  create.error = ''

  const id = create.form.id.trim()
  const name = create.form.name.trim()
  const department = create.form.department.trim()
  const phone = create.form.phone.trim()
  const password = create.form.password.trim()

  if (!id || !name || !department || !phone || !password) {
    create.error = '请把必填项填写完整（id、姓名、所属部门、电话、密码）。'
    return
  }

  try {
    await createUser({
      id,
      name,
      role: create.form.role,
      password,
      gender: create.form.gender,
      avatar: create.form.avatar?.trim() || '/AnonymousAvatar.png',
      department,
      phone,
    })

    closeCreate()
  } catch (e: any) {
    create.error = e?.message || '创建失败'
  }
}
</script>

<style scoped>
.userManagementShell {
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
  justify-content: space-between; /* 关键：左右分布 */
  gap: 16px;
}

.left {
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
  text-decoration: none;
}

.subnav-item.active {
  border-color: #4c98d4;
  color: #4c98d4;
  background: #e8f1fb;
}

.panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

/* 右侧按钮 */
.createBtn {
  height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid #4c98d4;
  background: #e8f1fb;
  color: #4c98d4;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.plus {
  font-size: 16px;
  line-height: 1;
  font-weight: 700;
}

/* 弹窗遮罩 */
.mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.25);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  z-index: 999;
}

.dialog {
  width: 680px;
  max-width: 94vw;
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 10px 30px rgba(0,0,0,.15);
}

.dialogTop {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.dialogTitle {
  font-size: 16px;
  font-weight: 700;
}

.iconBtn {
  height: 32px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}

.form {
  margin-top: 12px;
}

.row {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.field {
  flex: 1;
  min-width: 0;
}

.label {
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
}

.input {
  width: 100%;
  height: 34px;
  border-radius: 10px;
  border: 1px solid #ddd;
  padding: 0 10px;
  box-sizing: border-box;
  outline: none;
}

.error {
  margin-top: 8px;
  color: #d93025;
  font-size: 12px;
}

.dialogActions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 12px;
}

.btn {
  height: 34px;
  padding: 0 14px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}

.btn.primary {
  background-color: #4c98d4;
  color: #fff;
  border-color: #4c98d4;
}
</style>
