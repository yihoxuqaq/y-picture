<template>
  <div id="registerUserView">
    <div class="login-container">
      <h2 class="title">图灵图库 - 用户注册</h2>
      <div class="desc">企业级智能协同云图库</div>
      <a-form
        :model="formState"
        name="basic"
        class="login-form"
        autocomplete="off"
        @finish="handleSubmit"
      >
        <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
          <a-input v-model:value="formState.userAccount" placeholder="账号" >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item
          name="userPassword"
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 5, message: '密码不能小于 5 位' },
          ]"
        >
          <a-input-password v-model:value="formState.userPassword" placeholder="密码" >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item
          name="checkPassword"
          :rules="[
            { required: true, message: '请输入密码' },
            { min: 5, message: '密码不能小于 5 位' },
          ]"
        >
          <a-input-password v-model:value="formState.checkPassword" placeholder="确认密码" >
            <template #prefix>
              <CheckOutlined />
            </template>
          </a-input-password>
        </a-form-item>
        <div class="tips">
          已有账号？
          <RouterLink to="/loginUser">立即登录</RouterLink>
        </div>
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { registerUserUsingPost } from '@/api/userController.ts'
import { UserOutlined,LockOutlined,CheckOutlined } from '@ant-design/icons-vue'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})
const router = useRouter()

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  // 判断两次输入的密码是否一致
  if (formState.userPassword !== formState.checkPassword) {
    message.error('二次输入的密码不一致')
    return
  }
  const res = await registerUserUsingPost(values)
  // 登录成功，把登录态保存到全局状态中
  if (res.data.code === 0 && res.data.data) {
    message.success('注册成功')
    router.push({
      path: '/loginUser',
      replace: true,
    })
  } else {
    message.error('注册失败，' + res.data.message)
  }
}
</script>
<style scoped>
#registerUserView {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa, #c3cfe2);
}

.login-container {
  width: 100%;
  max-width: 400px;
  padding: 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}

.login-form {
  width: 100%;
}
</style>
