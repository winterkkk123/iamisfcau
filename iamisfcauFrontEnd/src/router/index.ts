import { createRouter, createWebHistory } from 'vue-router'

import Login from '@/views/Login.vue'
import HomePage from '@/views/HomePage.vue'
import ActivityCenter from '@/views/ActivityCenter.vue'
import QuestionAnswer from '@/views/QuestionsAnswers.vue'

import UserManagement from '@/views/UserManagement.vue'
import UserList from '@/views/UserManagement/UserList.vue'
import UserAudit from '@/views/UserManagement/UserAudit.vue'

import LeaveApproval from '@/views/LeaveApproval.vue'
import LeaveApply from '@/views/LeaveApproval/LeaveApply.vue'
import LeaveAudit from '@/views/LeaveApproval/LeaveAudit.vue'

import CommunitySharing from '@/views/CommunitySharing.vue'
import CommunityHall from '@/views/CommunitySharing/CommunityHall.vue'
import CommunityFavorites from '@/views/CommunitySharing/CommunityFavorites.vue'
import CommunityMine from '@/views/CommunitySharing/CommunityMine.vue'

import MessageCenter from '@/views/MessageCenter.vue'
import MessageDetail from '@/views/MessageCenter/MessageDetail.vue'
import MessageEmpty from '@/views/MessageCenter/MessageEmpty.vue'

import PersonalCenter from '@/views/PersonalCenter.vue'
import NotificationContent from '@/views/NotificationContent.vue'
import NotificationDetail from '@/views/NotificationContent/NotificationDetail.vue'
import ActivityContent from '@/views/ActivityContent.vue'
import ActivityDetail from '@/views/ActivityContent/ActivityDetail.vue'

import ActivityApply from '@/views/ActivityCenter/ActivityApply.vue'
import ActivityAttendance from '@/views/ActivityCenter/ActivityAttendance.vue'
import ActivityList from '@/views/ActivityCenter/ActivityList.vue'
import ActivityAudit from '@/views/ActivityCenter/ActivityAudit.vue'

import { useAppStore } from "@/stores/app";

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/home',
    component: HomePage,
    redirect: '/home/activityCenter',
    children: [
      { 
        path: 'activityCenter', 
        component: ActivityCenter,
        redirect: '/home/activityCenter/list',
        children: [
          { path: 'list', component: ActivityList },
          { path: 'apply', component: ActivityApply },
          { path: 'audit', component: ActivityAudit },
          { path: 'attendance', component: ActivityAttendance }
        ] 
      },
      { path: 'questionAnswer', component: QuestionAnswer },
      {
        path: 'userManagement',
        component: UserManagement,
        redirect: '/home/userManagement/list',
        children: [
          { path: 'list', component: UserList },
          { path: 'audit', component: UserAudit }
        ]
      },
      {
        path: 'leaveApproval',
        component: LeaveApproval,
        children: [
          { path: 'apply', component: LeaveApply },
          { path: 'audit', component: LeaveAudit }
        ]
      },
      {
        path: 'communitySharing',
        component: CommunitySharing,
        redirect: '/home/communitySharing/hall',
        children: [
          { path: 'hall', component: CommunityHall },
          { path: 'favorites', component: CommunityFavorites },
          { path: 'mine', component: CommunityMine }
        ]
      },
      {
        path: 'messageCenter',
        component: MessageCenter,
        children: [
          {
            path: '',
            component: MessageEmpty
          },
          {
            path: ':id',
            component: MessageDetail
          }
        ]
      },
      { path: 'personalCenter', component: PersonalCenter },
      { path: 'notification', component: NotificationContent },
      { path: 'notification/:id', component: NotificationDetail },
      { path: 'activity', component: ActivityContent },
      { path: 'activity/:id', component: ActivityDetail }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const appStore = useAppStore();

  // 尝试从 localStorage 恢复
  if (!appStore.user?.isLogin) {
    appStore.restoreLogin();
  }

  const isLogin = !!appStore.user?.isLogin;

  if (to.path === "/login") {
    if (isLogin) {
      next("/home");
      return;
    }
    next();
    return;
  }

  if (!isLogin) {
    next("/login");
    return;
  }

  next();
});

export default router
