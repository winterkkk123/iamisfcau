<template>
  <div class="activityCenterShell">
    <div class="header">
      <h2>活动中心</h2>

      <div class="subnav">
        <router-link :to="listPath" class="subnav-item" active-class="active">活动列表</router-link>

        <router-link
          :to="applyPath"
          class="subnav-item"
          active-class="active"
          :class="{ disabled: !canGoApply }"
          @click.prevent="!canGoApply"
        >
          活动申请
        </router-link>

        <router-link
          :to="auditPath"
          class="subnav-item"
          active-class="active"
          :class="{ disabled: !canGoAudit }"
          @click.prevent="!canGoAudit"
        >
          活动审批
        </router-link>

        <router-link
          :to="attendancePath"
          class="subnav-item"
          active-class="active"
          :class="{ disabled: !canGoAttendance }"
          @click.prevent="!canGoAttendance"
        >
          活动出勤
        </router-link>
      </div>
    </div>

    <div class="panel">
      <router-view />
    </div>
  </div>
</template>

<script lang="ts" setup name="ActivityCenter">
import { computed, watchEffect } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'

type Role = '管理员' | '教师' | '辅导员' | '学生'

const appStore = useAppStore()
const route = useRoute()
const router = useRouter()

const me = computed(() => appStore.user as { id: string; role: Role })

const listPath = '/home/activityCenter/list'
const applyPath = '/home/activityCenter/apply'
const auditPath = '/home/activityCenter/audit'
const attendancePath = '/home/activityCenter/attendance'

// - 活动列表：所有人都能看
// - 活动申请：学生主要入口
// - 活动审批：只有管理员
// - 活动出勤：学生看自己；老师看自己带的活动/学生的信息
const canGoApply = computed(() => {
  const role = me.value?.role
  return role === '学生' || role === '管理员'
})

const canGoAudit = computed(() => {
  const role = me.value?.role
  return role === '管理员'
})

const canGoAttendance = computed(() => {
  const role = me.value?.role
  return role === '学生' || role === '教师' || role === '管理员'
})

// 路由守卫：进入不允许的子页时自动跳转
watchEffect(() => {
  const p = route.path

  if (p.includes('/activityCenter/apply') && !canGoApply.value) {
    router.replace(listPath)
    return
  }
  if (p.includes('/activityCenter/audit') && !canGoAudit.value) {
    router.replace(listPath)
    return
  }
  if (p.includes('/activityCenter/attendance') && !canGoAttendance.value) {
    router.replace(listPath)
    return
  }
  if (p.endsWith('/activityCenter') || p.endsWith('/activityCenter/')) {
    router.replace(listPath)
  }
})
</script>

<style scoped>
.activityCenterShell {
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
  gap: 16px;
}

.header h2 {
  margin: 0;
}

.subnav {
  display: flex;
  gap: 10px;
  margin-left: 14px;
  flex-wrap: wrap;
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