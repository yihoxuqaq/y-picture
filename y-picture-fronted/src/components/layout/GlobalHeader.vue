<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="130px">
        <RouterLink to="/">
          <div class="title-bar">
            <img class="logo" src="../../assets/logo.png" alt="logo" />
            <!--            <div class="title">图灵图库</div>-->
          </div>
        </RouterLink>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          @click="doMenuClick"
          :items="items"
        />
      </a-col>
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            {{ loginUserStore.loginUser.userName ?? '无名' }}
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { h, ref } from 'vue'
import { PictureOutlined, SearchOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores'

const loginUserStore = useLoginUserStore()

const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(PictureOutlined),
    label: '公共图库',
    title: '主页',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/yihoxuqaq/y-picture', target: '_blank' }, '关于'),
    icon: () => h(SearchOutlined),
    title: '关于',
  },
])

const router = useRouter()
// 路由跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}
// 当前选中菜单
const current = ref<string[]>([])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  current.value = [to.path]
})
</script>
<style scoped>
.title-bar {
  display: flex;
  align-items: center; /* 垂直居中 */
  height: 100%; /* 关键：继承父容器高度 */
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 52px;
  margin: 0 auto;
}
</style>
