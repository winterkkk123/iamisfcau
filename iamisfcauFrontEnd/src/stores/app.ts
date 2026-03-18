import { defineStore } from "pinia";
import { appStateMock } from "@/mock/appState";

import { userApi } from "@/api/user";
import { noticeApi } from "@/api/notice";
import { activityApi } from "@/api/activity";
import { communityApi } from "@/api/community";
import { aiApi } from "@/api/ai";
import { messageCenterApi } from "@/api/messageCenter";
import { approvalsApi } from "@/api/approvals";
import { authApi } from "@/api/auth";

function createInitialState() {
  const state = structuredClone(appStateMock) as any;

  // 默认未登录
  state.user.isLogin = false;
  state.user.id = "";
  state.user.name = "";
  state.user.role = "";
  state.user.college = "";
  state.user.avatar = "";

  return state;
}

export const useAppStore = defineStore("app", {
  state: () => createInitialState(),

  actions: {
    async login(account: string, password: string) {
      const me = await authApi.login({ id: account, password });

      this.user = {
        ...this.user,
        ...me,
        isLogin: true,
      };

      localStorage.setItem("loginUser", JSON.stringify(this.user));
    },

    logout() {
      const emptyState = createInitialState();
      this.user = emptyState.user;

      localStorage.removeItem("loginUser");
    },

    restoreLogin() {
      const cache = localStorage.getItem("loginUser");
      if (!cache) return false;

      try {
        const user = JSON.parse(cache);
        this.user = {
          ...this.user,
          ...user,
          isLogin: true,
        };
        return true;
      } catch {
        localStorage.removeItem("loginUser");
        return false;
      }
    },

    async bootstrap() {
      const restored = this.restoreLogin();

      // 没登录，不继续请求全站数据
      if (!restored || !this.user?.id) return;

      const [
        me,
        notices,
        activities,
        communityPosts,
        aiConvs,
        msgConvs,
        leaveApprovals,
        activityCreateApprovals,
        profileChangeApprovals,
      ] = await Promise.all([
        userApi.me(),
        noticeApi.list(),
        activityApi.list(),
        communityApi.listPosts(),
        aiApi.conversations(),
        messageCenterApi.conversations(),
        approvalsApi.leaveList(),
        approvalsApi.activityCreateList(),
        approvalsApi.profileChangeList(),
      ]);

      this.user = {
        ...this.user,
        ...me,
        isLogin: true,
      };

      // 重新覆盖缓存，保证 userApi.me() 返回的新信息同步到本地
      localStorage.setItem("loginUser", JSON.stringify(this.user));

      this.notices = notices;
      this.activities = activities;
      this.communityPosts = communityPosts;
      this.leaveApprovals = leaveApprovals;
      this.activityCreateApprovals = activityCreateApprovals;
      this.profileChangeApprovals = profileChangeApprovals;

      const attendanceLists = await Promise.all(
        (this.activities ?? []).map((a: any) => activityApi.attendance(a.id))
      );

      this.activities = (this.activities ?? []).map((a: any, i: number) => ({
        ...a,
        attendance: attendanceLists[i] ?? [],
      }));

      const convWithMsgs = await Promise.all(
        (aiConvs ?? []).map(async (c: any) => ({
          ...c,
          messages: await aiApi.messages(c.id),
        }))
      );
      this.conversations = convWithMsgs;
      this.activeConversationId = convWithMsgs?.[0]?.id ?? "";

      const msgConvWithMsgs = await Promise.all(
        (msgConvs ?? []).map(async (c: any) => ({
          ...c,
          messages: await messageCenterApi.messages(c.id),
        }))
      );
      this.messageConversations = msgConvWithMsgs;
      this.activeMessageId = msgConvWithMsgs?.[0]?.id ?? "";
    },
  },
});