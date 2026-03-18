import { apiGet } from "./http";

export interface ActivityItem {
  id: number;
  title: string;
  organizer: string;
  leaderId: string;
  leaderName: string;
  date: string;
  time: string;
  startAt: string;
  endAt: string;
  location: string;
  content: string;
  summary: string;
}

export interface ActivityAttendanceItem {
  userId: string;
  userName: string;
  signInAt: string | null;
  attendStatus: string;
  leaveApplyId?: string | null;
  isLate: boolean;
}

export const activityApi = {
  list: () => apiGet<ActivityItem[]>("/api/activities"),
  attendance: (id: number) => apiGet<ActivityAttendanceItem[]>(`/api/activities/${id}/attendance`),
};

export const getActivities = activityApi.list;