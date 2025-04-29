<template>
  <a-config-provider :locale="zhCN">
    <BasicLayout />
  </a-config-provider>
</template>

<script setup lang="ts">
import zhCN from 'ant-design-vue/es/locale/zh_CN'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'

dayjs.locale('zh-cn')
import BasicLayout from '@/components/layout/BasicLayout.vue'
import { okUsingGet } from '@/api/healthController.ts'
import { useLoginUserStore } from '@/stores'

okUsingGet().then((res) => {
  console.log(res)
})

const loginUserStore = useLoginUserStore()
loginUserStore.fetchLoginUser()
</script>

<style scoped></style>
