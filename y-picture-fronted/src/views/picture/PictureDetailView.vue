<template>
  <div id="pictureDetailView">
    <a-row :gutter="[16, 16]">
      <!-- 图片展示区 -->
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="图片预览" class="image-preview-card">
          <template #extra>
            <a-button type="dashed" @click="doDownload">
              <DownloadOutlined />
              图片下载
            </a-button>
          </template>
          <div class="image-container">
            <a-image
              style="max-height: 600px; object-fit: contain"
              class="centered-image"
              :src="pictureVO?.url"
            />
          </div>
        </a-card>
      </a-col>
      <!-- 图片信息区 -->
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="pictureVO?.user?.userAvatar" />
                <div>{{ pictureVO?.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ pictureVO?.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ pictureVO?.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ pictureVO?.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in pictureVO?.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ pictureVO?.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ pictureVO?.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ pictureVO?.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ pictureVO?.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(pictureVO?.picSize) }}
            </a-descriptions-item>
          </a-descriptions>
          <a-space wrap>
            <a-button type="primary" @click="doDownload">
              免费下载
              <template #icon>
                <DownloadOutlined />
              </template>
            </a-button>
            <a-button type="default" @click="doEdit">
              编辑
              <template #icon>
                <EditOutlined />
              </template>
            </a-button>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { getPictureVoByIdUsingGet } from '@/api/pictureController.ts'
import { DownloadOutlined, EditOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { downloadImage, formatSize } from '@/util/index.ts'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const pictureVO = ref<API.PictureVO>()
const getPictureDetail = async () => {
  const res = await getPictureVoByIdUsingGet({
    id: route.query.pictureId,
  })
  if (res.data.code === 0) {
    pictureVO.value = res.data.data
  } else {
    message.error(res.data.message)
  }
}
const router = useRouter()
const doEdit = () => {
  router.push({
    path: '/addPicture',
    query: {
      pictureId: pictureVO.value?.id,
      spaceId: route.query.spaceId,
      spaceType: route.query.spaceType,
    },
  })
}
// 处理下载
const doDownload = () => {
  downloadImage(pictureVO.value?.url, pictureVO.value?.name)
}

onMounted(() => {
  getPictureDetail()
})
</script>

<style scoped>
#pictureDetailView {
  max-width: 900px;
  margin: 0 auto;
}

.image-preview-card {
  height: 100%;
}

.image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 600px; /* 固定高度，根据需要调整 */
  overflow: hidden;
}

.centered-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  display: block;
}
</style>
