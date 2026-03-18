<template>
  <div class="communityShell">
    <div class="header">
      <div class="left">
        <h2>社区分享</h2>

        <div class="subnav">
          <router-link
            to="/home/communitySharing/hall"
            class="subnav-item"
            active-class="active"
          >
            社区大厅
          </router-link>

          <router-link
            to="/home/communitySharing/favorites"
            class="subnav-item"
            active-class="active"
          >
            我的收藏
          </router-link>

          <router-link
            to="/home/communitySharing/mine"
            class="subnav-item"
            active-class="active"
          >
            我的发布
          </router-link>
        </div>
      </div>

      <button class="publishBtn" @click="openPublish">
        <span class="plus">＋</span>
        去发布
      </button>
    </div>

    <div class="panel">
      <router-view />
    </div>

    <div v-if="publish.visible" class="mask" @click.self="closePublish">
      <div class="dialog">
        <div class="dialogTop">
          <div class="dialogTitle">发布帖子</div>
          <button class="iconBtn" @click="closePublish">关闭</button>
        </div>

        <textarea
          v-model="publish.text"
          class="textarea"
          placeholder="输入你想分享的内容...(回车换行)"
        />

        <div class="uploadRow">
          <button class="pickBtn" @click="fileInput?.click()">选择照片</button>

          <input
            ref="fileInput"
            class="fileInput"
            type="file"
            accept="image/*"
            multiple
            @change="onPickFiles"
          />

          <div class="tip">最多 9 张图片（当前仅本地预览，未接图片上传接口）</div>
        </div>

        <div v-if="publish.previews.length" class="gridWrap">
          <div class="grid">
            <div v-for="(src, idx) in publish.previews" :key="src + idx" class="thumb">
              <img class="grid-img" :src="src" alt="preview" />
              <button class="rm" title="移除" @click="removePicked(idx)">×</button>
            </div>
          </div>
        </div>

        <div class="dialogActions">
          <button class="btn" @click="closePublish">取消</button>
          <button class="btn primary" :disabled="!canSubmit || submitting" @click="submitPost">
            {{ submitting ? '发布中...' : '发布' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup name="CommunitySharing">
import { computed, reactive, ref, onBeforeUnmount } from 'vue'
import { createCommunityPost } from '@/api/community'

const fileInput = ref<HTMLInputElement | null>(null)
const submitting = ref(false)

const publish = reactive({
  visible: false,
  text: '',
  files: [] as File[],
  previews: [] as string[]
})

const canSubmit = computed(() => {
  return publish.text.trim().length > 0 || publish.previews.length > 0
})

function openPublish() {
  publish.visible = true
}

function closePublish() {
  publish.previews.forEach((u) => URL.revokeObjectURL(u))
  publish.visible = false
  publish.text = ''
  publish.files = []
  publish.previews = []
  if (fileInput.value) fileInput.value.value = ''
}

function onPickFiles(e: Event) {
  const input = e.target as HTMLInputElement
  const files = Array.from(input.files || [])

  const remain = Math.max(0, 9 - publish.files.length)
  const picked = files.slice(0, remain)

  picked.forEach((f) => {
    publish.files.push(f)
    publish.previews.push(URL.createObjectURL(f))
  })

  input.value = ''
}

function removePicked(idx: number) {
  const url = publish.previews[idx]
  if (url) URL.revokeObjectURL(url)
  publish.previews.splice(idx, 1)
  publish.files.splice(idx, 1)
}

async function submitPost() {
  if (!canSubmit.value || submitting.value) return

  try {
    submitting.value = true

    await createCommunityPost({
      content: publish.text.trim(),
      type: 'user_post',
      relatedActivityId: '',
      images: [] // 目前 API 文档没有上传接口，这里先传空
    })

    closePublish()
    window.dispatchEvent(new Event('community-post-updated'))
  } catch (e: any) {
    alert(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}

onBeforeUnmount(() => {
  publish.previews.forEach((u) => URL.revokeObjectURL(u))
})
</script>

<style scoped>
.communityShell {
  height: 100%;
  background: #fff;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 16px 30px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
}

.left {
  display: flex;
  align-items: flex-end;
  justify-content: flex-start;
  gap: 16px;
}

.header h2 {
  margin: 0;
}

.subnav {
  display: flex;
  gap: 10px;
  margin-left: 14px;
}

.subnav-item {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 999px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  user-select: none;
  text-decoration: none;
}

.subnav-item.active {
  border-color: #4c98d4;
  color: #4c98d4;
  background: #e8f1fb;
}

.panel {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.publishBtn {
  height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid #4c98d4;
  background: #e8f1fb;
  color: #4c98d4;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.plus {
  font-size: 16px;
  line-height: 1;
  font-weight: 700;
}

.mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.25);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  z-index: 999;
}

.dialog {
  width: 640px;
  max-width: 94vw;
  background: #fff;
  border-radius: 12px;
  padding: 14px;
  box-shadow: 0 10px 30px rgba(0,0,0,.15);
}

.dialogTop {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
}

.dialogTitle {
  font-size: 16px;
  font-weight: 700;
}

.textarea {
  width: 100%;
  height: 140px;
  margin-top: 10px;
  border-radius: 10px;
  border: 1px solid #ddd;
  padding: 10px;
  box-sizing: border-box;
  outline: none;
  resize: none;
}

.uploadRow {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.tip {
  font-size: 12px;
  color: #999;
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

.thumb {
  position: relative;
}

.grid-img {
  width: 100%;
  aspect-ratio: 1 / 1;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #eee;
}

.rm {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  border-radius: 999px;
  border: 1px solid #eee;
  background: #fff;
  cursor: pointer;
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

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.iconBtn {
  height: 32px;
  padding: 0 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}

.pickBtn {
  height: 34px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid #4c98d4;
  background: #e8f1fb;
  color: #4c98d4;
  cursor: pointer;
  font-size: 13px;
}

.fileInput {
  display: none;
}
</style>