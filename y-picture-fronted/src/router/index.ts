import { createRouter, createWebHistory } from 'vue-router'
import WelcomeView from '@/views/WelcomeView.vue'
import LoginUserView from '@/views/user/LoginUserView.vue'
import RegisterUserView from '@/views/user/RegisterUserView.vue'
import AddPictureView from '@/views/picture/AddPictureView.vue'
import PictureDetailView from '@/views/picture/PictureDetailView.vue'
import PictureManageView from '@/views/picture/PictureManageView.vue'
import UserManageView from '@/views/user/UserManageView.vue'
import UserCenterView from '@/views/user/UserCenterView.vue'

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
      component: AddPictureView,
    },
    {
      path: '/picture/:id',
      name: '图片详情',
      component: PictureDetailView,
      props: true,
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
  ],
})

export default router
