<template>
  <div id="userCenterView">
    <a-card title="个人资料" class="centered-card">
      <template #extra>
        <a-space>
          <a-button type="primary" ghost @click="doUserEdit">
            <EditOutlined />
            编辑资料
          </a-button>
          <a-button type="primary" ghost>
            <LockOutlined />
            修改密码
          </a-button>
        </a-space>
      </template>

      <div class="profile-container">
        <!-- 头像上传区域 -->
        <div class="avatar-section">
          <a-upload
            list-type="picture-card"
            :show-upload-list="false"
            :custom-request="handleUpload"
            :before-upload="beforeUpload"
            class="avatar-uploader"
            :loading="loading"
          >
            <a-avatar
              class="avatar"
              :src="avatarUrl ? avatarUrl : loginUser.userAvatar"
              v-if="avatarUrl ? avatarUrl : loginUser.userAvatar"
            />
            <div v-else class="upload-placeholder">
              <div>
                <CloudUploadOutlined />
              </div>
              <div class="upload-text">点击上传头像</div>
            </div>
          </a-upload>
        </div>

        <!-- 用户信息 -->
        <div class="info-section">
          <div class="username">{{ loginUser.userAccount }}</div>
          <div class="user-handle">{{ loginUser.userName }}</div>

          <div class="info-row">
            <span class="info-label">邮箱</span>
            <span class="info-value">2207039249@qq.com</span>
          </div>

          <div class="info-row">
            <span class="info-label">注册时间</span>
            <span class="info-value">
              {{ dayjs(loginUser.createTime).format('YYYY-MM-DD HH:mm:ss') }}
            </span>
          </div>

          <div class="info-row">
            <span class="info-label">手机</span>
            <span class="info-value">未设置</span>
          </div>

          <div class="info-row">
            <span class="info-label">生日</span>
            <span class="info-value">未设置</span>
          </div>
        </div>

        <!-- 会员升级区域 -->
        <div class="upgrade-section">
          <div class="upgrade-title">升级为会员享受更多特权</div>
          <a-button type="primary" class="upgrade-button">立即升级</a-button>
        </div>
      </div>
    </a-card>
    <UserEditModal ref="userEditModalRef" :userInfo="loginUser" :onSuccess="onSuccess" />
  </div>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { EditOutlined, LockOutlined, CloudUploadOutlined } from '@ant-design/icons-vue'
import { message, type UploadProps } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores'
import dayjs from 'dayjs'
import { getLoginUserUsingGet, uploadUserAvatarUsingPost } from '@/api/userController.ts'
import UserEditModal from '@/components/user/UserEditModal.vue'

let loginUser = computed(() => {
  return useLoginUserStore().loginUser
})

/**
 * 上传
 * @param file
 */
const loading = ref<boolean>(false)
const avatarUrl = ref<string>()
const handleUpload = async ({ file }: any) => {
  loading.value = true
  try {
    const res = await uploadUserAvatarUsingPost({}, file)
    if (res.data.code === 0 && res.data.data) {
      avatarUrl.value = res.data.data
      message.success('图片更新成功')
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败')
  } finally {
    loading.value = false
  }
}
//上传前校验参数
const beforeUpload = (file: UploadProps['fileList'][number]) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('不支持上传该格式的图片，推荐 jpg 或 png')
  }
  const isLt3M = file.size / 1024 / 1024 < 3
  if (!isLt3M) {
    message.error('不能上传超过 3M 的图片')
  }
  return isJpgOrPng && isLt3M
}
//用户编辑信息
const userEditModalRef = ref()
const doUserEdit = () => {
  userEditModalRef.value.showModal()
}
const onSuccess = async () => {
  location.reload()
}
</script>

<style scoped>
/* 容器样式 */
#userCenterView {
  max-width: 950px;
  margin: 0 auto;
  padding: 20px;
}

/* 卡片样式 */
.centered-card {
  width: 850px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 整体布局 */
.profile-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 头像上传区域 */
.avatar-section {
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #999;
}

.upload-placeholder :deep(.anticon) {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 14px;
}

/* 用户信息区域 */
.info-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 0 20px;
}

.username {
  font-size: 20px;
  font-weight: 600;
  text-align: center;
}

.user-handle {
  color: #666;
  text-align: center;
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-label {
  color: #666;
}

.info-value {
  font-weight: 500;
}

/* 会员升级区域 */
.upgrade-section {
  margin-top: 24px;
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
  text-align: center;
}

.upgrade-title {
  font-size: 16px;
  margin-bottom: 12px;
  color: #666;
}

.upgrade-button {
  width: 120px;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .centered-card {
    width: 100%;
  }
}

@media (max-width: 576px) {
  .profile-container {
    gap: 16px;
  }

  .info-section {
    padding: 0 10px;
  }
}
</style>
