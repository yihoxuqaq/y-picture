import { createRouter, createWebHistory } from 'vue-router'
import WelcomeView from '@/views/WelcomeView.vue'
import LoginUserView from '@/views/LoginUserView.vue'
import RegisterUserView from '@/views/RegisterUserView.vue'
import userManageView from '@/views/admin/UserManageView.vue'

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
      path: '/registerUser',
      name: '用户注册',
      component: RegisterUserView,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: userManageView,
    },

  ],
})

export default router
