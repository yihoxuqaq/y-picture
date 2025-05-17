<template>
  <div id="pictureOutPaintingView">
    <a-card size="small"  style="margin: 0 auto">
      <a-space size="large">
        <PictureUpload :picture="pictureVO" :onSuccess="onSuccess" />
        <a-button @click="createTask" type="dashed">等比扩图</a-button>
        <a-spin tip="Loading..." :spinning="loading">
          <a-empty v-if="!resultImageUrl" />
          <a-image :src="resultImageUrl" v-else alt="扩展图片" />
        </a-spin>
      </a-space>
      <template #title>
        <div style="font-size: 24px;text-align: center;width: 100%">
          ai扩图
        </div>
      </template>
      <template #actions>
        <a-button v-if="resultImageUrl" @click="handleUpload" :loading="uploadLoading">
          <UploadOutlined />
          保存结果
        </a-button>
      </template>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import PictureUpload from '@/components/picture/PictureUpload.vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import { onUnmounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  createOutPaintingTaskUsingPost, editPictureUsingPost,
  getOutPaintingTaskUsingGet,
  uploadPictureByUrlUsingPost
} from '@/api/pictureController.ts'

const pictureVO = ref<API.PictureVO>()
const onSuccess = (newPicture: API.PictureVO) => {
  pictureVO.value = newPicture
}

// 任务 id
let taskId = ref<string>()

/**
 * 创建任务
 */
const createTask = async () => {
  if (!pictureVO.value?.id) {
    message.info('请传入图片')
    return
  }
  loading.value = true
  const res = await createOutPaintingTaskUsingPost({
    pictureId: pictureVO.value?.id,
    // 可以根据需要设置扩图参数
    parameters: {
      xScale: 2,
      yScale: 2,
    },
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('创建任务成功，请耐心等待，不要退出界面')
    console.log(res.data.data.output.taskId)
    taskId.value = res.data.data.output.taskId
    // 开启轮询
    startPolling()
  } else {
    message.error('创建任务失败，' + res.data.message)
  }
}
// 轮询定时器
let pollingTimer: NodeJS.Timeout = null

// 清理轮询定时器
const clearPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
    taskId.value = null
  }
}

// 开始轮询
const resultImageUrl = ref<string>()
const loading = ref<boolean>(false)
const startPolling = () => {
  if (!taskId.value) return
  pollingTimer = setInterval(async () => {
    try {
      const res = await getOutPaintingTaskUsingGet({
        taskId: taskId.value,
      })
      if (res.data.code === 0 && res.data.data) {
        const taskResult = res.data.data.output
        if (taskResult.taskStatus === 'SUCCEEDED') {
          message.success('扩图任务成功')
          resultImageUrl.value = taskResult.outputImageUrl
          loading.value = false
          clearPolling()
        } else if (taskResult.taskStatus === 'FAILED') {
          message.error('扩图任务失败')
          clearPolling()
          loading.value = false
        }
      }
    } catch (error) {
      console.error('轮询任务状态失败', error)
      message.error('检测任务状态失败，请稍后重试')
      clearPolling()
    }
  }, 3000) // 每隔 3 秒轮询一次
}

// 清理定时器，避免内存泄漏
onUnmounted(() => {
  clearPolling()
})

const uploadLoading = ref<boolean>(false)

const handleUpload = async () => {
  uploadLoading.value = true
  try {
    const params: API.PictureEditRequest = {
      url: resultImageUrl.value,
      id:pictureVO.value?.id,
    }
    const res = await editPictureUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败')
  } finally {
    uploadLoading.value = false
  }
}
</script>

<style scoped>
/* 添加父容器全宽和居中保障 */
#pictureOutPaintingView {
  display: flex;
  justify-content: center; /* 水平居中 */
  width: 100%;
}

/* 原有样式保留 */
#pictureOutPaintingView :deep(.ant-upload) {
  max-width: 800px;
}

#pictureOutPaintingView img {
  max-width: 100%;
  max-height: 480px;
}
</style>
