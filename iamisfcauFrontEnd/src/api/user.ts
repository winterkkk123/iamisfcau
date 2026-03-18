import { apiDelete, apiGet, apiPost, apiPut } from './http'

export type UserRole = '管理员' | '教师' | '辅导员' | '学生'
export type Gender = '男' | '女'

export type UserItem = {
  id: string
  name: string
  role: UserRole
  password: string
  gender: Gender
  avatar?: string
  department: string
  college?: string | null
  phone: string
}

export type UserMe = {
  isLogin: boolean
  name: string
  id: string
  role: UserRole
  college?: string | null
  avatar?: string
  joinedActivityIds: number[]
  managedActivityIds: number[]
  communityMyFavorites: string[]
  communityMyPosts: string[]
}

export type CreateUserRequest = {
  id: string
  name: string
  role: UserRole
  password: string
  gender: Gender
  avatar?: string
  department: string
  phone: string
}

export type UpdateUserRequest = {
  name: string
  role: UserRole
  password: string
  gender: Gender
  avatar?: string
  department: string
  phone: string
}

export function me() {
  return apiGet<UserMe>('/api/users/me')
}

export function getUsers(params?: { name?: string; role?: UserRole | '' }) {
  return apiGet<UserItem[]>('/api/users', { params })
}

export function createUser(data: CreateUserRequest) {
  return apiPost<UserItem>('/api/users', data)
}

export function updateUser(id: string, data: UpdateUserRequest) {
  return apiPut<UserItem>(`/api/users/${id}`, data)
}

export function deleteUser(id: string) {
  return apiDelete<void>(`/api/users/${id}`)
}

export const userApi = {
  me,
  getUsers,
  createUser,
  updateUser,
  deleteUser,
}