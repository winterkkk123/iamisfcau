// import { defineStore } from "pinia";
// import { noticeApi } from "@/api/notice";

// // 先只做 notices，其他模块后面再加
// export const useAppStateStore = defineStore("appState", {
//   state: () => ({
//     // 保持和你 mock 结构一致
//     notices: [] as any[],
//   }),

//   actions: {
//     async loadNotices() {
//       this.notices = await noticeApi.list();
//     },
//   },
// });