import { apiGet, apiPost } from './http'

export type AuditStatus = '待审核' | '已同意' | '已拒绝'

export type AuditItem = {
  applyId: string
  userName: string
  userId: string
  applicantName: string
  applicantId: string
  changeType: string
  before: string
  after: string
  submittedAt: string
  status: AuditStatus
  approvalComment: string
}

type ProfileChangeDtoRaw = {
  applyId: string
  targetUserId: string
  targetUserName: string
  applicantId: string
  applicantName: string
  changeType: string
  beforeValue: string
  afterValue: string
  submittedAt: string
  status: AuditStatus
  approvalComment: string
}

function mapAuditItem(item: ProfileChangeDtoRaw): AuditItem {
  return {
    applyId: item.applyId,
    userId: item.targetUserId,
    userName: item.targetUserName,
    applicantId: item.applicantId,
    applicantName: item.applicantName,
    changeType: item.changeType,
    before: item.beforeValue,
    after: item.afterValue,
    submittedAt: item.submittedAt,
    status: item.status,
    approvalComment: item.approvalComment ?? '',
  }
}

export async function getProfileChangeApplications() {
  const data = await apiGet<ProfileChangeDtoRaw[]>('/api/profile-change-applications')
  return data.map(mapAuditItem)
}

export async function approveProfileChange(applyId: string, approvalComment?: string) {
  const data = await apiPost<ProfileChangeDtoRaw>(
    `/api/profile-change-applications/${applyId}/approve`,
    { approvalComment: approvalComment ?? '' }
  )
  return mapAuditItem(data)
}

export async function rejectProfileChange(applyId: string, approvalComment?: string) {
  const data = await apiPost<ProfileChangeDtoRaw>(
    `/api/profile-change-applications/${applyId}/reject`,
    { approvalComment: approvalComment ?? '' }
  )
  return mapAuditItem(data)
}