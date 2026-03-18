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

export function getLeaveApprovals() {
  return apiGet<LeaveApprovalItem[]>("/api/leave-approvals");
}

export function createLeaveApproval(data: LeaveApplyCreateRequest) {
  return apiPost<LeaveApprovalItem>("/api/leave-approvals", data);
}

export function approveLeave(applyId: string, data?: ApprovalRequest) {
  return apiPost<LeaveApprovalItem>(`/api/leave-approvals/${applyId}/approve`, data);
}

export function rejectLeave(applyId: string, data?: ApprovalRequest) {
  return apiPost<LeaveApprovalItem>(`/api/leave-approvals/${applyId}/reject`, data);
}

export function withdrawLeaveApproval(applyId: string, applicantId: string) {
  return apiDelete<void>(`/api/leave-approvals/${applyId}`, {
    params: { applicantId }
  });
}