<template>加载中，请稍后。。。。。。</template>

<script setup lang="ts">
import { useLoginUserStore } from '@/stores/userStore.ts'
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'
import { listSpaceVoByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()

const router = useRouter()
const checkUserSpace = async () => {
  const id = loginUserStore.loginUser.id
  if (!id) {
    router.replace('/user/login')
    return
  }
  const res = await listSpaceVoByPageUsingPost({
    userId: id,
    current: 1,
    pageSize: 1,
  })
  if (res.data?.code === 0) {
    if (res.data?.data?.records.length > 0) {
      router.replace(`/spaceDetail/${res.data?.data?.records[0].id}`)
    } else {
      router.replace('/add_space')
      message.warn('请先创建个人空间')
    }
  } else {
    message.error('个人空间请求失败，' + res.data.message)
  }
}
onMounted(() => {
  checkUserSpace()
})
</script>

<style scoped></style>
