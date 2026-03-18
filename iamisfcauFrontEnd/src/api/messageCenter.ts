import { apiGet, apiPost } from './http'

export interface MessageDto {
  role: 'system' | 'other' | 'self'
  content: string
  time: string
}

export interface MessageConversationDto {
  id: string
  name: string
  avatar?: string
  updatedAt: string
  lastMessage?: string
}

export interface SendMessageRequest {
  role: 'self'
  content: string
}

export function getMessageConversations() {
  return apiGet<MessageConversationDto[]>('/api/messages/conversations')
}

export function getConversationMessages(id: string) {
  return apiGet<MessageDto[]>(`/api/messages/conversations/${id}/messages`)
}

export function sendConversationMessage(
  id: string,
  data: SendMessageRequest
) {
  return apiPost<MessageDto>(`/api/messages/conversations/${id}/messages`, data)
}

/* 兼容旧调用 */
export const conversations = getMessageConversations
export const messages = getConversationMessages
export const sendMessage = sendConversationMessage

export const messageCenterApi = {
  conversations,
  messages,
  sendMessage,

  getMessageConversations,
  getConversationMessages,
  sendConversationMessage
}