<template>
  <div id="globalSider">
    <a-layout-sider v-if="loginUserStore.loginUser.id" breakpoint="lg" collapsed-width="0">
      <a-menu
        v-model:selectedKeys="current"
        :items="items"
        mode="inline"
        style="height: 100%"
        v-on:click="useClickMen"
      >
      </a-menu>
    </a-layout-sider>
  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { PictureOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/userStore.ts'

const current = ref<string[]>([''])
const router = useRouter()
const useClickMen = ({ key }) => {
  router.push(key)
}
router.afterEach((to, from, next) => {
  current.value = [to.path]
})
const loginUserStore = useLoginUserStore()

// 菜单列表
const items = [
  {
    key: '/',
    icon: () => h(PictureOutlined),
    label: '公共图库',
  },
  {
    key: '/checkSpaceAuth',
    icon: () => h(UserOutlined),
    label: '我的空间',
  },
]
</script>
<style scoped>
#globalSider {
}
</style>
