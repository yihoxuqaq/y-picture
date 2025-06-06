<template>
  <div id="spaceDetailView">
    <a-row type="flex" justify="space-between" align="middle">
      <a-col>
        <h2 style="margin: 0">
          <CloudOutlined />
          {{ spaceVO?.spaceName }}-{{ SPACE_TYPE_MAP[spaceVO?.spaceType] }}
        </h2>
      </a-col>
      <a-col>
        <a-space>
          <a-button v-if="canUploadPicture" type="primary" @click="doUploadPicture">
            <UploadOutlined />
            上传图片
          </a-button>
          <a-button
            v-if="spaceVO?.spaceType === 1 && canManageSpaceUser"
            type="primary"
            ghost
            :href="`/spaceUserManage/${id}`"
          >
            <ApartmentOutlined />
            成员管理
          </a-button>
          <a-button v-if="spaceVO?.spaceType === 1" @click="openDrawer" type="primary" ghost>
            <CommentOutlined />
            加入的空间
          </a-button>
        </a-space>
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
    <div style="margin-bottom: 28px" />
    <a-form
      :model="searchParams"
      name="basic"
      layout="inline"
      autocomplete="off"
      @finish="doSearch"
    >
      <a-form-item label="关键词" name="searchText">
        <a-input v-model:value="searchParams.searchText" placeholder="从名称、简介中搜索" />
      </a-form-item>
      <a-form-item label="图片名称" name="searchText">
        <a-input v-model:value="searchParams.name" placeholder="请输入图片名称" />
      </a-form-item>
      <a-form-item name="picFormat" label="图片格式">
        <a-select v-model:value="searchParams.picFormat" placeholder="请选择图片格式">
          <a-select-option value="JPG">JPG</a-select-option>
          <a-select-option value="JPEG">JPEG</a-select-option>
          <a-select-option value="PNG">PNG</a-select-option>
          <a-select-option value="WEBP">WEBP</a-select-option>
          <a-select-option value="">其他</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="图片上传时间">
        <a-range-picker
          style="width: 400px"
          v-model:value="timeRange"
          :placeholder="[`开始上传时间`, `结束上传时间`]"
          show-time
          format="YYYY/MM/DD HH:mm:ss"
          :presets="rangePresets"
          @change="onRangeChange"
        />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button html-type="reset" @click="doClear">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 28px" />
    <hr />
    <PictureList :pictureList="pictureDataList" :doReload="doReload" :spaceVO="spaceVO" />
    <a-row justify="end" style="margin-top: 16px">
      <a-col>
        <a-pagination
          v-model:current="searchParams.current"
          v-model:pageSize="searchParams.pageSize"
          :showTotal="shoTotal"
          :total="total"
          @change="change"
        />
      </a-col>
    </a-row>
    <JoinTeamSpaceDrawer ref="joinTeamSpaceDrawerRef" :spaceUserVO="spaceUserVOList" />
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import {
  FolderOpenOutlined,
  UploadOutlined,
  CloudOutlined,
  ApartmentOutlined,
  CommentOutlined,
} from '@ant-design/icons-vue'
import { getUserSpaceByIdUsingGet } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { formatSize } from '@/util'
import { useRouter } from 'vue-router'
import { listPictureVoByPageUsingPost } from '@/api/pictureController.ts'
import PictureList from '@/components/picture/PictureList.vue'
import dayjs from 'dayjs'
import { SPACE_PERMISSION_ENUM, SPACE_TYPE_MAP } from '@/constants/space.ts'
import JoinTeamSpaceDrawer from '@/components/spaceuser/JoinTeamSpaceDrawer.vue'
import { listMyTeamSpaceUsingPost } from '@/api/spaceUserController.ts'

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
      spaceType: spaceVO.value?.spaceType,
    },
  })
}

//加载图片列表
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
})
const total = ref(0)
const pictureDataList = ref<API.SpaceVO[]>()
const loadPictureList = async () => {
  const res = await listPictureVoByPageUsingPost({
    spaceId: props.id,
    nullSpaceId: false,
    ...searchParams,
  })
  if (res.data.code === 0 && res.data.data) {
    pictureDataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error(res.data.message)
  }
}
const shoTotal = (total, range) => {
  return `共 ${total} 条`
}
const change = (page, pageSize) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  loadPictureList()
}
//图片搜索
const doSearch = () => {
  searchParams.current = 1
  loadPictureList()
}
//清理
const doClear = () => {
  //重置所有查询选项
  Object.keys(searchParams).forEach((key) => {
    searchParams[key] = undefined
  })
  //日期单独清理
  timeRange.value = []
  loadPictureList()
}

const timeRange = ref<[]>([])
const onRangeChange = (dates: [], dateStrings: string[]) => {
  if (dates.length >= 2) {
    searchParams.startCreateTime = dates[0].toDate()
    searchParams.endCreateTime = dates[1].toDate()
  } else {
    searchParams.startCreateTime = undefined
    searchParams.endCreateTime = undefined
  }
}

//预设时间
const rangePresets = ref([
  { label: '最近7天', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '最近14天', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '最近30天', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '最近90天', value: [dayjs().add(-90, 'd'), dayjs()] },
])

//重新加载数据
const doReload = () => {
  loadPictureList()
  loadSpaceData()
}
const activeKey = ref(['1'])

//加入的团队空间抽屉
const spaceUserVOList = ref<API.SpaceUserVO[]>()
const joinTeamSpaceDrawerRef = ref()
const openDrawer = async () => {
  if (joinTeamSpaceDrawerRef.value) {
    joinTeamSpaceDrawerRef.value.showDrawer()
  }
  const res = await listMyTeamSpaceUsingPost()
  if (res.data.code === 0) {
    spaceUserVOList.value = res.data.data
  } else {
    message.error(res.data.message)
  }
}

// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (spaceVO.value?.permissionList ?? []).includes(permission)
  })
}

// 定义权限检查
const canManageSpaceUser = createPermissionChecker(SPACE_PERMISSION_ENUM.SPACE_USER_MANAGE)
const canUploadPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_UPLOAD)
</script>

<style scoped>
/* 可选：添加容器间距 */
#spaceDetailView {
  padding: 16px;
}
</style>
