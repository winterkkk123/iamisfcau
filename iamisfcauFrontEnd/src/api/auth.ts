import { apiPost } from "@/api/http";

export interface LoginPayload {
  id: string;
  password: string;
}

export interface LoginUser {
  isLogin?: boolean;
  id: string;
  name: string;
  role: string;
  college?: string;
  avatar?: string;
  joinedActivityIds?: string[];
  managedActivityIds?: string[];
  communityMyFavorites?: string[];
  communityMyPosts?: string[];
}

export const authApi = {
  login(data: LoginPayload) {
    return apiPost<LoginUser>("/api/auth/login", data);
  },
};