import { apiDelete, apiGet, apiPost } from "./http";

export type CommunityUser = {
  id: string;
  name: string;
  role: string;
  department?: string;
  avatar?: string;
};

export type CommunityReply = {
  id: string;
  user: CommunityUser;
  createdAt: string;
  content: string;
};

export type CommunityComment = {
  id: string;
  user: CommunityUser;
  createdAt: string;
  content: string;
  replies: CommunityReply[];
};

export type CommunityPost = {
  id: string;
  author: CommunityUser;
  createdByUserId: string;
  createdAt: string;
  type: string;
  relatedActivityId: string;
  content: string;
  images: string[];
  stats: {
    likes: number;
    comments: number;
    favorites: number;
    views: number;
  };
  userState: {
    liked: boolean;
    favorited: boolean;
  };
  comments: CommunityComment[];
};

export type CommunityPostsResponse = CommunityPost[];

export type CreatePostReq = {
  content: string;
  type?: string;
  relatedActivityId?: string;
  images?: string[];
};

export type CreateCommentReq = {
  content: string;
};

export type ReactionUpdateDto = {
  liked: boolean;
  favorited: boolean;
  likes: number;
  favorites: number;
};

export function getCommunityPosts() {
  return apiGet<CommunityPostsResponse>("/api/community/posts");
}

export function getCommunityPost(id: string) {
  return apiGet<CommunityPost>(`/api/community/posts/${id}`);
}

export function createCommunityPost(data: CreatePostReq) {
  return apiPost<CommunityPost>("/api/community/posts", data);
}

export function deleteCommunityPost(id: string) {
  return apiDelete<void>(`/api/community/posts/${id}`);
}

export function toggleLike(id: string) {
  return apiPost<ReactionUpdateDto>(`/api/community/posts/${id}/like`);
}

export function toggleFavorite(id: string) {
  return apiPost<ReactionUpdateDto>(`/api/community/posts/${id}/favorite`);
}

export function createComment(postId: string, data: CreateCommentReq) {
  return apiPost<CommunityComment>(`/api/community/posts/${postId}/comments`, data);
}

export function createReply(commentId: string, data: CreateCommentReq) {
  return apiPost<CommunityComment>(`/api/community/comments/${commentId}/replies`, data);
}

/* 兼容旧调用 */
export const listPosts = getCommunityPosts;
export const getPost = getCommunityPost;
export const createPost = createCommunityPost;

export const communityApi = {
  listPosts,
  getPosts: getCommunityPosts,
  getCommunityPosts,

  getPost,
  getCommunityPost,

  createPost,
  createCommunityPost,

  deleteCommunityPost,
  toggleLike,
  toggleFavorite,
  createComment,
  createReply
};