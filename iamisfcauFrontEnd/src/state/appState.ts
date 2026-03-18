// src/state/appState.ts
import { reactive, inject, type InjectionKey } from 'vue'

export type NoticeTag = 'top' | 'normal'
export type ChatRole = 'assistant' | 'user'
export type MessageRole = 'system' | 'other' | 'self'

export interface UserInfo {
  isLogin: boolean
  name: string
  Id: string // 先保持你的字段不变；后续建议改成 studentId
  role: string
  college: string
  avatar: string
}

export interface Notice {
  id: number
  tags: NoticeTag[]
  title: string
  time: string
  content: string
}

export interface Activity {
  id: number
  title: string
  organizer: string
  date: string
  time: string
  location: string
  content: string
}

export interface ChatMessage {
  role: ChatRole
  content: string
}

export interface Conversation {
  id: string
  title: string
  createdAt: string
  messages: ChatMessage[]
}

export interface ThreadMessage {
  role: MessageRole
  content: string
  time: string
}

export interface MessageConversation {
  id: string
  name: string
  avatar: string
  updatedAt: string
  messages: ThreadMessage[]
}

export interface AppState {
  user: UserInfo
  notices: Notice[]
  activities: Activity[]
  conversations: Conversation[]
  activeConversationId: string
  messageConversations: MessageConversation[]
  activeMessageId: string
}

export const appStateKey: InjectionKey<AppState> = Symbol('appState')

/**
 * 可传入 seed（如 mock 数据），后续接接口时也可以传入服务端初始数据
 */
export function createAppState(seed: AppState): AppState {
  // reactive 返回的对象就是 AppState（这里保持结构一致）
  return reactive(seed) as AppState
}

export function useAppState(): AppState {
  const state = inject(appStateKey)
  if (!state) {
    throw new Error('appState not provided. Did you forget provide(appStateKey, appState)?')
  }
  return state
}
