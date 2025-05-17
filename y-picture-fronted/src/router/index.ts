import { createRouter, createWebHistory } from 'vue-router'
import WelcomeView from '@/views/WelcomeView.vue'
import LoginUserView from '@/views/user/LoginUserView.vue'
import RegisterUserView from '@/views/user/RegisterUserView.vue'
import PictureAddView from '@/views/picture/PictureAddView.vue'
import PictureDetailView from '@/views/picture/PictureDetailView.vue'
import PictureManageView from '@/views/picture/PictureManageView.vue'
import UserManageView from '@/views/user/UserManageView.vue'
import UserCenterView from '@/views/user/UserCenterView.vue'
import GetUserSpaceTeam from '@/components/space/getUserSpaceTeam.vue'
import SpaceDetailView from '@/views/space/SpaceDetailView.vue'
import SpaceAddView from '@/views/space/SpaceAddView.vue'
import PictureGradView from '@/views/picture/PictureGradView.vue'
import GetUserSpacePrivate from '@/components/space/getUserSpacePrivate.vue'
import SpaceUserManageView from '@/views/spaceuser/SpaceUserManageView.vue'
import PictureOutPaintingView from '@/views/picture/PictureOutPaintingView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: WelcomeView,
    },
    {
      path: '/loginUser',
      name: '用户登录',
      component: LoginUserView,
    },
    {
      path: '/userCenter',
      name: '用户中心',
      component: UserCenterView,
    },
    {
      path: '/registerUser',
      name: '用户注册',
      component: RegisterUserView,
    },
    {
      path: '/addPicture',
      name: '上传图片',
      component: PictureAddView,
    },
    {
      path: '/picture',
      name: '图片详情',
      component: PictureDetailView,
    },
    {
      path: '/pictureGrad',
      name: '图片抓取',
      component: PictureGradView,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManageView,
    },
    {
      path: '/admin/pictureManage',
      name: '图片管理',
      component: PictureManageView,
    },
    {
      path: '/getUserSpacePrivate',
      name: '获取用户个人空间',
      component: GetUserSpacePrivate,
    },
    {
      path: '/getUserSpaceTeam',
      name: '获取用户团队空间',
      component: GetUserSpaceTeam,
    },
    {
      path: '/spaceDetail/:id',
      name: '空间详情',
      component: SpaceDetailView,
      props: true,
    },
    {
      path: '/spaceAdd',
      name: '创建空间',
      component: SpaceAddView,
    },
    {
      path: '/pictureOutPainting',
      name: 'ai扩图',
      component: PictureOutPaintingView,
    },
    {
      path: '/spaceUserManage/:spaceId',
      name: '空间成员管理',
      component: SpaceUserManageView,
      props: true,
    },
  ],
})

export default router
