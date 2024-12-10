<template>
  <div id="globalHead">
    <a-row :wrap="false">
      <a-col flex="150px">
        <img src="../assets/logo.png" alt="logo" />
        <span style="font-size: 15px">智能云图库</span>
      </a-col>
      <a-col flex="auto" style="margin-left: 35px">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          v-on:click="useClickMen"
        />
      </a-col>
      <a-col flex="100px">
        <a-button type="primary">登录</a-button>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { AppstoreOutlined, MailOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const current = ref<string[]>([''])
const router = useRouter()
const useClickMen = ({ key }) => {
  router.push(key)
}
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(MailOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/test',
    icon: () => h(AppstoreOutlined),
    label: '测试',
    title: '测试',
  },
])
</script>
<style scoped>
#globalHead {
}

img {
  width: 48px;
  margin-right: 18px;
}
</style>
