<template>
  <div id="userUpdateModal">
    <a-modal v-model:open="open" title="更新用户信息" @ok="handleOk">
      <a-form
        :model="formState"
        layout="vertical"
        name="basic"
        class="login-form"
        autocomplete="off"
      >
        <a-form-item name="userName" label="昵称">
          <a-input v-model:value="formState.userName" placeholder="请输入账号" />
        </a-form-item>
        <a-form-item label="角色" name="userRole">
          <a-select v-model:value="formState.userRole" style="width: 120px" placeholder="角色">
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="user">用户</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item name="userProfile" label="用户简介">
          <a-textarea v-model:value="formState.userProfile" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { LockOutlined, UserOutlined } from '@ant-design/icons-vue'
import { updateUserUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'

interface Props {
  oleUser: API.UserVO
  onSuccess: () => void
}

const props = defineProps<Props>()
const formState = reactive<API.UserVO>({
  ...props.oleUser,
})
// 监听props变化，初始化表单数据
watch(
  () => props.oleUser,
  (newValue) => {
    formState.userName = newValue.userName
    formState.userProfile = newValue.userProfile
    formState.userRole = newValue.userRole
  },
)
const open = ref<boolean>(false)

const showModal = () => {
  open.value = true
}

const handleOk = async (e: MouseEvent) => {
  const res = await updateUserUsingPost({
    id: props.oleUser.id,
    ...formState,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('用户信息更新成功')
    props.onSuccess()
  } else {
    message.error(res.data.message)
  }
  open.value = false
}

//对外暴露方法
defineExpose({
  showModal,
})
</script>
<style scoped>
#userUpdateModal {
}
</style>
