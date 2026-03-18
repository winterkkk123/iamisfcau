import { apiGet, apiPost } from "./http";

export const approvalsApi = {
  leaveList: () => apiGet<any[]>("/api/leave-approvals"),
  activityCreateList: () => apiGet<any[]>("/api/activity-create-approvals"),
  profileChangeList: () => apiGet<any[]>("/api/profile-change-applications"),

  leaveApprove: (applyId: string, approvalComment?: string) =>
    apiPost<any>(`/api/leave-approvals/${applyId}/approve`, { approvalComment }),
  leaveReject: (applyId: string, approvalComment?: string) =>
    apiPost<any>(`/api/leave-approvals/${applyId}/reject`, { approvalComment }),
};