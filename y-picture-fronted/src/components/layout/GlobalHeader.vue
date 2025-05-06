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
            <a-dropdown>
              <ASpace>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '游客' }}
              </ASpace>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                  <a-menu-item @click="doUserCenter">
                    <UserOutlined />
                    个人中心
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>

          <div v-else>
            <a-button type="primary" href="/loginUser">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import {
  PictureOutlined,
  SearchOutlined,
  LogoutOutlined,
  PicCenterOutlined,
  AlignCenterOutlined,
  VerticalAlignTopOutlined,
  UserOutlined,
  CloudOutlined,
  BugOutlined,
  ExpandAltOutlined,
} from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores'
import { logoutUserUsingPost } from '@/api/userController.ts'

const loginUserStore = useLoginUserStore()

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
// 用户注销
const doLogout = async () => {
  const res = await logoutUserUsingPost()
  console.log(res)
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/loginUser')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
//路由到个人信息
const doUserCenter = () => {
  router.push({
    path: '/userCenter',
  })
}
//过滤菜单
const filterItems = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    if (menu?.key.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}
//展示菜单数据
const items = computed<MenuProps['items']>(() => filterItems(originItems))

//菜单列表
const originItems = [
  {
    key: '/',
    icon: () => h(PictureOutlined),
    label: '公共图库',
    title: '主页',
  },
  {
    key: '/addPicture',
    icon: () => h(VerticalAlignTopOutlined),
    label: '上传图片',
    title: '上传图片',
  },
  {
    key: '/getUserSpace',
    icon: () => h(CloudOutlined),
    label: '个人空间',
    title: '个人空间',
  },
  {
    key: '/admin/userManage',
    icon: () => h(AlignCenterOutlined),
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/pictureManage',
    icon: () => h(PicCenterOutlined),
    label: '图片管理',
    title: '图片管理',
  },
  {
    key: '',
    icon: () => h(ExpandAltOutlined),
    label: '图片扩展',
    title: '图片扩展',
    children: [
      {
        label: '爬取图片',
        title: '图片扩展',
        key: '/pictureGrad',
        icon: () => h(BugOutlined),
      },
    ],
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/yihoxuqaq/y-picture', target: '_blank' }, '关于'),
    icon: () => h(SearchOutlined),
    title: '关于',
  },
]
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
