import { apiGet } from './http'

export interface NoticeDto {
  id: number
  title: string
  content: string
  tags: string[]
  time: string
}

export function getNotices() {
  return apiGet<NoticeDto[]>('/api/notices')
}

export function getNoticeById(id: number) {
  return apiGet<NoticeDto>(`/api/notices/${id}`)
}

export const noticeApi = {
  list: getNotices,
  getNotices,
  getNoticeById
}