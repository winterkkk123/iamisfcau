<template>
  <div class="layout">
    <!-- 顶部 -->
    <header class="header">
      <div class="title">高校智能助手管理信息系统</div>
      <div class="userBox" v-if="user.isLogin">
        <span class="userInfo">{{ user.role }} | {{ user.name }} {{ user.id }}</span>
        <button class="logoutBtn" @click="goLogout">退出登录</button>
      </div>
    </header>

    <!-- 主体 -->
    <div class="main">
      <!-- 左侧导航 -->
      <nav class="sidebar">
        <router-link to="/home/activityCenter" class="sidebar-link">活动中心</router-link>
        <router-link to="/home/questionAnswer" class="sidebar-link">智能问答</router-link>
        <router-link to="/home/userManagement" class="sidebar-link">用户管理</router-link>
        <router-link to="/home/leaveApproval" class="sidebar-link">请假审批</router-link>
        <router-link to="/home/communitySharing" class="sidebar-link">社区分享</router-link>
        <router-link to="/home/messageCenter" class="sidebar-link">消息中心</router-link>
        <router-link to="/home/personalCenter" class="sidebar-link">个人中心</router-link>
      </nav>

      <!-- 内容区 -->
      <section class="content">
        <router-view />
      </section>
    </div>
  </div>
</template>

<script lang="ts" setup name="HomePage">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { useAppStore } from "@/stores/app";

const router = useRouter();
const appStore = useAppStore();
const user = computed(() => appStore.user);

const goLogout = () => {
  const ok = window.confirm("确定要退出登录吗？");
  if (!ok) return;

  appStore.logout();
  window.alert("已退出登录");
  router.replace("/login");
};
</script>

<style scoped>
.layout {
  height: 100vh;
  min-height: 900px;
  display: flex;
  flex-direction: column;
  background: #f3f3f3;
  overflow: hidden;
}

.header {
  height: 60px;
  background-color: #4c98d4;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: white;
}

.header .title {
  font-size: 20px;
}

.main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  width: 180px;
  background: #fafafa;
  font-size: 16px;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.sidebar-link {
  border-radius: 10px;
}

.sidebar a {
  padding: 15px;
  text-decoration: none;
  color: #333;
}

.sidebar a.router-link-active {
  background-color: rgb(217, 234, 252);
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  box-sizing: border-box;
}

.userBox {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: 24px;
}

.userInfo {
  color: white;
}

.logoutBtn {
  height: 32px;
  padding: 0 16px;
  border: 1px solid #ffffff;
  background: #ffffff;
  color: #4c98d4;
  border-radius: 6px;
  cursor: pointer;
}

.logoutBtn:hover {
  background: #f5f9ff;
}
</style>
