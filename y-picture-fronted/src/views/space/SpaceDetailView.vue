<template>
  <div id="spaceDetailView">
    <a-row type="flex" justify="space-between" align="middle">
      <a-col>
        <h2 style="margin: 0">
          <CloudOutlined />
          个人空间
        </h2>
      </a-col>
      <a-col>
        <a-button type="primary" @click="doUploadPicture">
          <UploadOutlined />
          上传图片
        </a-button>
      </a-col>
    </a-row>
    <div style="margin-bottom: 28px" />
    <a-collapse v-model:activeKey="activeKey">
      <a-collapse-panel key="1">
        <template #header>
          <h3>
            <FolderOpenOutlined />
            空间使用请求
          </h3>
        </template>
        存储空间：{{ formatSize(spaceVO?.totalSize) }}/{{ formatSize(spaceVO?.maxSize) }}
        <a-progress
          :stroke-color="{
            '0%': '#87d068',
            '100%': '#FF0000',
          }"
          :percent="((spaceVO?.totalSize * 100) / spaceVO?.maxSize).toFixed(1)"
        />
        图片数量：{{ spaceVO?.totalCount }}/{{ spaceVO?.maxCount }}张
        <a-progress
          :stroke-color="{
            from: '#87d068',
            to: '#FF0000',
          }"
          :percent="((spaceVO?.totalCount * 100) / spaceVO?.maxCount).toFixed(1)"
          status="active"
        />
      </a-collapse-panel>
    </a-collapse>
    <PictureList :pictureList="pictureDataList" :doReload="doReload" />
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { FolderOpenOutlined, UploadOutlined, CloudOutlined } from '@ant-design/icons-vue'
import { getUserSpaceByIdUsingGet } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { formatSize } from '@/util'
import { useRouter } from 'vue-router'
import { listPictureVoByPageUsingPost } from '@/api/pictureController.ts'
import PictureList from '@/components/picture/PictureList.vue'

const props = defineProps<{
  id: number
}>()
const spaceVO = ref<API.SpaceVO>()
const loadSpaceData = async () => {
  const res = await getUserSpaceByIdUsingGet({
    id: props.id,
  })
  if (res.data.code === 0) {
    spaceVO.value = res.data.data
  } else {
    message.error('数据加载失败，' + res.data.message)
  }
}
onMounted(() => {
  loadSpaceData()
  loadPictureList()
})
//上传图片
const router = useRouter()
const doUploadPicture = () => {
  router.push({
    path: '/addPicture',
    query: {
      spaceId: props.id,
    },
  })
}

//加载图片列表
const pictureDataList = ref<API.SpaceVO[]>()
const loadPictureList = async () => {
  const res = await listPictureVoByPageUsingPost({
    spaceId: props.id,
    nullSpaceId: false,
  })
  if (res.data.code === 0 && res.data.data) {
    pictureDataList.value = res.data.data.records ?? []
  } else {
    message.error(res.data.message)
  }
}
//重新加载数据
const doReload = () => {
  loadPictureList()
  loadSpaceData()
}
const activeKey = ref(['1'])
</script>

<style scoped>
/* 可选：添加容器间距 */
#spaceDetailView {
  padding: 16px;
}
</style>
