<template>
  <div class="leaveApprovalShell">
    <!-- 标题 -->
    <div class="header">
      <h2>请假审批</h2>

      <!-- 子导航：和用户管理一样 -->
      <div class="subnav">
        <router-link
          :to="applyPath"
          class="subnav-item"
          active-class="active"
          :class="{ disabled: !canGoApply }"
          @click.prevent="!canGoApply"
        >
          请假申请
        </router-link>

        <router-link
          :to="auditPath"
          class="subnav-item"
          active-class="active"
          :class="{ disabled: !canGoAudit }"
          @click.prevent="!canGoAudit"
        >
          请假审核
        </router-link>
      </div>
    </div>

    <div class="panel">
      <router-view />
    </div>
  </div>
</template>

<script lang="ts" setup name="LeaveApproval">
import { computed, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

type Role = '管理员' | '教师' | '辅导员' | '学生'

const appStore = useAppStore()
const me = computed(() => appStore.user as { id: string; name: string; role: Role })

const route = useRoute()
const router = useRouter()

const applyPath = '/home/leaveApproval/apply'
const auditPath = '/home/leaveApproval/audit'

const canGoApply = computed(() => {
  const role = me.value?.role
  return role === '学生' || role === '管理员'
})

const canGoAudit = computed(() => {
  const role = me.value?.role
  return role === '教师' || role === '辅导员' || role === '管理员'
})

watchEffect(() => {
  const path = route.path
  const role = me.value?.role

  if (path.includes('/leaveApproval/apply') && !canGoApply.value) {
    if (canGoAudit.value) router.replace(auditPath)
    return
  }

  if (path.includes('/leaveApproval/audit') && !canGoAudit.value) {
    if (canGoApply.value) router.replace(applyPath)
    return
  }

  if (path.endsWith('/leaveApproval') || path.endsWith('/leaveApproval/')) {
    if (role === '学生') router.replace(applyPath)
    else router.replace(auditPath)
  }
})
</script>

<style scoped>
.leaveApprovalShell {
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
  text-decoration: none;
  background: #fff;
}

.subnav-item.active {
  border-color: #4c98d4;
  color: #4c98d4;
  background: #e8f1fb;
}

/* 禁用态 */
.subnav-item.disabled {
  color: #aaa;
  background: #f5f5f5;
  border-color: #e5e5e5;
  cursor: not-allowed;
  pointer-events: none;
}

.panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
</style>
