<template>
  <div class="mine">
    <div class="feed">
      <div v-for="post in myPosts" :key="post.id" class="post">
        <div class="post-head">
          <img class="avatar" :src="post.author.avatar" alt="avatar" />

          <div class="info">
            <div class="nameRow">
              <span class="name">{{ post.author.name }}</span>
              <span class="badge">{{ post.author.role }}</span>
              <span class="dept" v-if="post.author.department">· {{ post.author.department }}</span>
            </div>
            <div class="time">{{ formatTime(post.createdAt) }}</div>
          </div>

          <div class="rightBox">
            <div class="activity" v-if="post.relatedActivityId">
              <span class="activityTag">活动</span>
              <span class="activityTitle">{{ activityTitle(post.relatedActivityId) }}</span>
            </div>

            <button v-if="canDelete(post)" class="iconBtn danger" @click="removePost(post)">
              删除
            </button>
          </div>
        </div>

        <div class="content">{{ post.content }}</div>

        <div v-if="post.images?.length" class="gridWrap">
          <div class="grid">
            <img
              v-for="(img, idx) in post.images.slice(0, 9)"
              :key="img + idx"
              class="grid-img"
              :src="img"
              alt="img"
              @click="openPreview(post.images, Number(idx))"
            />
          </div>
        </div>

        <div class="actions">
          <button class="action" :class="{ active: post.userState.liked }" @click="onToggleLike(post)">
            <img
              class="actionIcon"
              :src="post.userState.liked ? '/icons/like-active.png' : '/icons/like.png'"
              alt="like"
            />
            <span>点赞 {{ post.stats.likes }}</span>
          </button>

          <button class="action" @click="openComment(post)">
            <img class="actionIcon" src="/icons/comment.png" alt="comment" />
            <span>评论 {{ post.stats.comments }}</span>
          </button>

          <button class="action" :class="{ active: post.userState.favorited }" @click="onToggleFavorite(post)">
            <img
              class="actionIcon"
              :src="post.userState.favorited ? '/icons/fav-active.png' : '/icons/fav.png'"
              alt="fav"
            />
            <span>收藏 {{ post.stats.favorites }}</span>
          </button>
        </div>

        <div v-if="post.comments?.length" class="comments">
          <div v-for="c in post.comments.slice(0, 2)" :key="c.id" class="comment">
            <div class="commentLine">
              <span class="cname">{{ c.user.name }}：</span>
              <span class="ctext">{{ c.content }}</span>
              <button class="replyBtn" @click="openReply(post, c)">回复</button>
            </div>

            <div v-if="c.replies?.length" class="replies">
              <div v-for="r in c.replies.slice(0, 2)" :key="r.id" class="reply">
                <span class="rname">{{ r.user.name }}</span>
                <span class="rtxt">回复：</span>
                <span class="rcontent">{{ r.content }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && myPosts.length === 0" class="empty">
        你还没有发布任何帖子～
      </div>
      <div v-if="loading" class="empty">加载中...</div>
    </div>

    <div v-if="preview.visible" class="mask" @click.self="closePreview">
      <div class="preview">
        <div class="previewTop">
          <div class="previewIndex">{{ preview.index + 1 }} / {{ preview.images.length }}</div>
          <button class="iconBtn" @click="closePreview">关闭</button>
        </div>

        <img class="previewImg" :src="preview.images[preview.index]" alt="preview" />

        <div class="previewBottom">
          <button class="iconBtn" :disabled="preview.index === 0" @click="prevImg">上一张</button>
          <button class="iconBtn" :disabled="preview.index === preview.images.length - 1" @click="nextImg">
            下一张
          </button>
        </div>
      </div>
    </div>

    <div v-if="commentDialog.visible" class="mask" @click.self="closeComment">
      <div class="dialog">
        <div class="dialogTitle">{{ commentDialog.replyTo ? '回复评论' : '发表评论' }}</div>

        <div class="dialogSub">
          <template v-if="commentDialog.replyTo">
            回复 {{ commentDialog.replyTo.user.name }}：{{ commentDialog.replyTo.content }}
          </template>
          <template v-else>
            {{ commentDialog.post?.author.name }}：{{ commentDialog.post?.content }}
          </template>
        </div>

        <textarea v-model="commentDialog.text" class="textarea" placeholder="请输入内容"></textarea>

        <div class="dialogActions">
          <button class="btn" @click="closeComment">取消</button>
          <button class="btn primary" @click="submitComment">发布</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="CommunityMine">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useAppStore } from '@/stores/app'
import type { CommunityComment, CommunityPost } from '@/api/community'
import {
  createComment,
  createReply,
  deleteCommunityPost,
  getCommunityPosts,
  toggleFavorite,
  toggleLike
} from '@/api/community'

const appStore = useAppStore()
const user = computed(() => appStore.user)

const posts = ref<CommunityPost[]>([])
const loading = ref(false)

const myPosts = computed(() => posts.value.filter((p) => p.createdByUserId === user.value.id))

function activityTitle(id: string) {
  return ''
}

function formatTime(iso: string) {
  return iso.replace('T', ' ').slice(0, 16)
}

function canDelete(post: CommunityPost) {
  const isAdmin = user.value.role === '管理员'
  const isOwner = post.createdByUserId === user.value.id
  return isAdmin || isOwner
}

function patchReaction(post: CommunityPost, data: { liked: boolean; favorited: boolean; likes: number; favorites: number }) {
  post.userState.liked = data.liked
  post.userState.favorited = data.favorited
  post.stats.likes = data.likes
  post.stats.favorites = data.favorites
}

function appendReplyToTree(comments: CommunityComment[], targetId: string, reply: CommunityComment): boolean {
  for (const c of comments) {
    if (c.id === targetId) {
      c.replies.push(reply)
      return true
    }
    if (c.replies?.length) {
      const found = appendReplyToTree(c.replies as CommunityComment[], targetId, reply)
      if (found) return true
    }
  }
  return false
}

async function loadPosts() {
  try {
    loading.value = true
    posts.value = await getCommunityPosts()
  } catch (e: any) {
    alert(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function removePost(post: CommunityPost) {
  const ok = confirm('确定要删除这条帖子吗？')
  if (!ok) return

  try {
    await deleteCommunityPost(post.id)
    posts.value = posts.value.filter((p) => p.id !== post.id)
  } catch (e: any) {
    alert(e.message || '删除失败')
  }
}

async function onToggleLike(post: CommunityPost) {
  try {
    const res = await toggleLike(post.id)
    patchReaction(post, res)
  } catch (e: any) {
    alert(e.message || '点赞失败')
  }
}

async function onToggleFavorite(post: CommunityPost) {
  try {
    const res = await toggleFavorite(post.id)
    patchReaction(post, res)
  } catch (e: any) {
    alert(e.message || '收藏失败')
  }
}

const preview = reactive({
  visible: false,
  images: [] as string[],
  index: 0
})

function openPreview(images: string[], index: number) {
  preview.images = images
  preview.index = index
  preview.visible = true
}

function closePreview() {
  preview.visible = false
}

function prevImg() {
  if (preview.index > 0) preview.index -= 1
}

function nextImg() {
  if (preview.index < preview.images.length - 1) preview.index += 1
}

const commentDialog = reactive({
  visible: false,
  post: null as CommunityPost | null,
  text: '',
  replyTo: null as CommunityComment | null
})

function openComment(post: CommunityPost) {
  commentDialog.post = post
  commentDialog.replyTo = null
  commentDialog.text = ''
  commentDialog.visible = true
}

function openReply(post: CommunityPost, comment: CommunityComment) {
  commentDialog.post = post
  commentDialog.replyTo = comment
  commentDialog.text = ''
  commentDialog.visible = true
}

function closeComment() {
  commentDialog.visible = false
  commentDialog.post = null
  commentDialog.replyTo = null
  commentDialog.text = ''
}

async function submitComment() {
  const text = commentDialog.text.trim()
  if (!text || !commentDialog.post) return

  try {
    if (commentDialog.replyTo) {
      const reply = await createReply(commentDialog.replyTo.id, { content: text })
      appendReplyToTree(commentDialog.post.comments, commentDialog.replyTo.id, reply)
      commentDialog.post.stats.comments += 1
    } else {
      const comment = await createComment(commentDialog.post.id, { content: text })
      commentDialog.post.comments.push(comment)
      commentDialog.post.stats.comments += 1
    }
    closeComment()
  } catch (e: any) {
    alert(e.message || '评论失败')
  }
}

function refreshByEvent() {
  loadPosts()
}

onMounted(() => {
  loadPosts()
  window.addEventListener('community-post-updated', refreshByEvent)
})

onBeforeUnmount(() => {
  window.removeEventListener('community-post-updated', refreshByEvent)
})
</script>

<style scoped>
.mine {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.feed {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 14px 18px;
  box-sizing: border-box;
}

.post {
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 12px;
  background: #fff;
}

.post-head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  object-fit: cover;
  border: 1px solid #eee;
}

.info {
  flex: 1;
  min-width: 0;
}

.nameRow {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.name {
  font-weight: 600;
  color: #222;
}

.badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid #d7e6f7;
  color: #2b6cb0;
  background: #e8f1fb;
}

.dept {
  font-size: 12px;
  color: #777;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.rightBox {
  display: flex;
  align-items: center;
  gap: 10px;
}

.activity {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  max-width: 240px;
}

.activityTag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid #ffe2b7;
  color: #9a6b1a;
  background: #fff7e6;
}

.activityTitle {
  font-size: 12px;
  color: #666;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.content {
  margin-top: 10px;
  line-height: 22px;
  font-size: 14px;
  color: #333;
  white-space: pre-wrap;
}

.gridWrap {
  margin-top: 10px;
  display: flex;
  justify-content: flex-start;
}

.grid {
  width: 330px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
}

.grid-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
  cursor: zoom-in;
}

.actions {
  margin-top: 12px;
  display: flex;
  gap: 10px;
}

.action {
  height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 13px;
  color: #555;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.actionIcon {
  width: 18px;
  height: 18px;
  object-fit: contain;
}

.action.active {
  border-color: #4c98d4;
  color: #4c98d4;
  background: #e8f1fb;
}

.comments {
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  background: #fafafa;
  border: 1px solid #eee;
}

.comment {
  font-size: 13px;
  line-height: 20px;
  color: #555;
}

.commentLine {
  display: flex;
  align-items: center;
  gap: 6px;
}

.cname {
  font-weight: 600;
  color: #333;
}

.replyBtn {
  margin-left: auto;
  border: none;
  background: transparent;
  color: #4c98d4;
  cursor: pointer;
  font-size: 12px;
}

.replies {
  margin-top: 6px;
  padding-left: 10px;
  border-left: 2px solid #eee;
}

.reply {
  font-size: 12px;
  line-height: 18px;
  color: #666;
  margin-top: 4px;
}

.rname {
  font-weight: 600;
  color: #333;
}

.rtxt {
  color: #999;
  margin: 0 4px;
}

.rcontent {
  color: #555;
}

.empty {
  padding: 24px;
  color: #999;
  text-align: center;
}

.mask {
  position: fixed;
  inset: 0;
  background: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  z-index: 999;
}

.preview {
  width: 860px;
  max-width: 92vw;
  background: #111;
  border-radius: 12px;
  padding: 12px;
}

.previewTop {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #fff;
  margin-bottom: 10px;
}

.previewIndex {
  font-size: 13px;
  color: #ddd;
}

.previewImg {
  width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 8px;
}

.previewBottom {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.iconBtn {
  height: 32px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}

.iconBtn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.iconBtn.danger {
  border-color: #f1b1b1;
  color: #b53a3a;
}

.dialog {
  width: 520px;
  max-width: 92vw;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.dialogTitle {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
}

.dialogSub {
  font-size: 12px;
  color: #777;
  margin-bottom: 12px;
  line-height: 18px;
}

.textarea {
  width: 100%;
  height: 120px;
  border-radius: 8px;
  border: 1px solid #ddd;
  padding: 10px;
  box-sizing: border-box;
  outline: none;
  resize: none;
}

.dialogActions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 12px;
}

.btn {
  height: 34px;
  padding: 0 14px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}

.btn.primary {
  background-color: #4c98d4;
  color: #fff;
  border-color: #4c98d4;
}
</style>