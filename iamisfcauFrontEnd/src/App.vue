<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useAppStore } from "@/stores/app";

const appStore = useAppStore();

onMounted(async () => {
  try {
    await appStore.bootstrap();
    console.log("初始化完成", appStore.$state);
  } catch (e: any) {
    console.error("初始化失败：", e?.message || e);
  }
});
</script>