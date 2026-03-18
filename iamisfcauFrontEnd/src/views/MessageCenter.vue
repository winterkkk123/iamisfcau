<template>
  <div class="messageCenter">
    <!-- 左侧 会话列表 -->
    <MessageList />

    <!-- 右侧 具体会话 -->
    <router-view />
  </div>
</template>

<script lang="ts" setup name="MessageCenter">
  import MessageList from './MessageCenter/MessageList.vue'
</script>

<style scoped>
.messageCenter {
  height: 100%;
  display: flex;
  background: #ffffff;
  border-radius: 8px;
  overflow: hidden;
}
</style>
