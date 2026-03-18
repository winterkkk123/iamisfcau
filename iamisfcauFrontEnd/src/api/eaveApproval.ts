import { apiDelete, apiGet, apiPost } from "./http";

export type LeaveStatus = "待审核" | "已同意" | "已拒绝";

export interface LeaveApprovalItem {
  applyId: string;
  activityId: number;
  activityTitle: string;
  teacherId: string;
  teacherName: string;
  applicantId: string;
  applicantName: string;
  reason: string;
  submittedAt: string;
  status: LeaveStatus;
  approvalComment: string;
}

export interface ApprovalRequest {
  approvalComment?: string;
}

export interface LeaveApplyCreateRequest {
  activityId: number;
  applicantId: string;
  reason: string;
}

export const leaveApprovalApi = {
  list: () => apiGet<LeaveApprovalItem[]>("/api/leave-approvals"),

  create: (data: LeaveApplyCreateRequest) =>
    apiPost<LeaveApprovalItem>("/api/leave-approvals", data),

  approve: (applyId: string, data?: ApprovalRequest) =>
    apiPost<LeaveApprovalItem>(`/api/leave-approvals/${applyId}/approve`, data),

  reject: (applyId: string, data?: ApprovalRequest) =>
    apiPost<LeaveApprovalItem>(`/api/leave-approvals/${applyId}/reject`, data),

  withdraw: (applyId: string, applicantId: string) =>
    apiDelete<void>(`/api/leave-approvals/${applyId}`, {
      params: { applicantId },
    }),
};