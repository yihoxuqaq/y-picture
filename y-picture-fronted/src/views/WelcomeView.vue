<template>
  <Waterfall
    :list="pictureVO"
    :width="300"
    :gap="20"
    class="waterfall-container"
    :breakpoints="breakpoints"
  >
    <template #default="{ item }">
      <a-card hoverable style="margin-bottom: 20px" @click="doPictureDetail(item)">
        <template #cover>
          <LazyImg :url="item.url" class="lazy__img" />
        </template>
        <template #actions>
          <SettingOutlined key="setting" />
          <EditOutlined key="edit" />
          <EllipsisOutlined key="ellipsis" />
        </template>
        <a-card-meta
          :title="item.user?.userName || '未命名'"
          :description="item.description || '暂无描述'"
        >
          <template #avatar>
            <a-avatar :src="item.user?.userAvatar" />
          </template>
        </a-card-meta>
      </a-card>
    </template>
  </Waterfall>

  <div v-if="loading" class="loading-tip fixed-bottom">
    <a-spin />
    <span style="margin-left: 10px">加载中...</span>
  </div>
  <div v-if="noMore" class="no-more-tip fixed-bottom">没有更多内容了</div>
</template>

<script lang="ts" setup>
import { LazyImg, Waterfall } from 'vue-waterfall-plugin-next'
import 'vue-waterfall-plugin-next/dist/style.css'
import { onMounted, ref, reactive, onUnmounted } from 'vue'
import { listPictureVoByPageUsingPost } from '@/api/pictureController.ts'
import { SettingOutlined, EditOutlined, EllipsisOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const pictureVO = ref<API.PictureVO[]>([])
const loading = ref(false)
const noMore = ref(false)

const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 加载数据
const loadData = async () => {
  if (loading.value || noMore.value) return
  loading.value = true
  try {
    const res = await listPictureVoByPageUsingPost(searchParams)
    if (res.data.code === 0) {
      const newData = res.data.data?.records ?? []

      if (newData.length === 0) {
        noMore.value = true
        return
      }
      pictureVO.value = [...pictureVO.value, ...newData]
      searchParams.current++
      if (newData.length < searchParams.pageSize) {
        noMore.value = true
      }
    }
  } catch (error) {
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}
const router = useRouter()
const doPictureDetail = (item) => {
  // 跳转路由
  const r = router.resolve({
    path: `/picture/${item.id}`,
  })
  // 在新页面打开
  window.open(r.href, '_blank')
}
/**
 * 瀑布流布局
 */
const breakpoints = ref({
  3000: {
    //当屏幕宽度小于等于3000
    rowPerView: 7, // 一行8图
  },
  1800: {
    rowPerView: 6,
  },
  1500: {
    rowPerView: 5,
  },
  1200: {
    rowPerView: 4,
  },
  1000: {
    rowPerView: 3,
  },
  800: {
    rowPerView: 2,
  },
  700: {
    rowPerView: 2,
  },
  500: {
    rowPerView: 1,
  },
  300: {
    rowPerView: 1,
  },
})

// 滚动处理
const handleScroll = () => {
  const { scrollTop, clientHeight, scrollHeight } = document.documentElement
  // 距离底部100px时触发
  if (scrollHeight - (scrollTop + clientHeight) < 100) {
    loadData()
  }
}

onMounted(() => {
  loadData()
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style>
/* 全局样式 */
body {
  margin: 0;
  overflow-y: auto;
}

/* 瀑布流容器 */
.waterfall-container {
  padding: 20px;
  box-sizing: border-box;
  max-width: 2700px;
  margin: 0 auto;
}

/* 加载提示 */
.loading-tip.fixed-bottom,
.no-more-tip.fixed-bottom {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.9);
  padding: 10px 20px;
  border-radius: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  display: flex;
  align-items: center;
}

/* 图片加载样式 */
.lazy__img[lazy='loading'] {
  padding: 5em 0;
  width: 48px;
}

.lazy__img[lazy='loaded'] {
  width: 100%;
  transition: opacity 0.3s;
}

.lazy__img[lazy='error'] {
  padding: 5em 0;
  width: 48px;
}
</style>
