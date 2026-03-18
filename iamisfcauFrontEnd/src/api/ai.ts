import { apiGet } from "./http";

export interface AiConversationSummary {
  id: string
  title: string
  createdAt: string
}

export interface AiMessage {
  role: 'assistant' | 'user'
  content: string
  createdAt: string
}

export interface AiConversation {
  id: string
  title: string
  createdAt: string
  messages: AiMessage[]
}

export interface AiChatResponse {
  conversationId: string
  title: string
  answer: string
}

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const resp = await fetch(url, {
    headers: {
      'Content-Type': 'application/json'
    },
    ...options
  })

  if (!resp.ok) {
    throw new Error(await resp.text())
  }

  return resp.json() as Promise<T>
}

export const aiApi = {
  conversations: () =>
    request<AiConversationSummary[]>('/api/ai/conversations'),

  messages: (id: string) =>
    request<AiMessage[]>(`/api/ai/conversations/${id}/messages`),

  createConversation: () =>
    request<AiConversation>('/api/ai/conversations', {
      method: 'POST'
    }),

  sendMessage: (payload: {
    conversationId: string
    message: string
    userId?: string
    userName?: string
  }) =>
    request<AiChatResponse>('/api/ai/chat', {
      method: 'POST',
      body: JSON.stringify(payload)
    })
}