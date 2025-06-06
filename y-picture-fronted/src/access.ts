import { message } from 'ant-design-vue'
import router from '@/router'
import { useLoginUserStore } from '@/stores'

// 是否为首次获取登录用户
let firstFetchLoginUser = true

/**
 * 全局权限校验
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  // 确保页面刷新，首次加载时，能够等后端返回用户信息后再校验权限
  if (firstFetchLoginUser) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }
  const toUrl = to.fullPath
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('没有权限')
      next(`/loginUser?redirect=${to.fullPath}`)
      return
    }
  }
  if (toUrl.startsWith('/userCenter')) {
    if (!loginUser) {
      message.error('请登录！')
      next(`/loginUser?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
