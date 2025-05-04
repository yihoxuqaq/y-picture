<template>
  <div id="getUserSpace">请稍后。。。。</div>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserSpaceUsingGet } from '@/api/spaceController.ts'

const router = useRouter()
const getUserSpace = async () => {
  const res = await getUserSpaceUsingGet()
  if (res.data.code === 0) {
    await router.replace(`/spaceDetail/${res.data.data}`)
  } else {
    await router.push({
      path: '/spaceAdd',
    })
  }
}
onMounted(() => {
  getUserSpace()
})
</script>

<style scoped>
#getUserSpace {
}
</style>
