<template>
  <div id="userEditModal">
    <a-modal v-model:open="open" title="编辑个人信息" width="800px" :footer="null">
      <a-form :model="formState" layout="vertical" autocomplete="off">
        <!-- 用户名和昵称行 -->
        <div class="form-row">
          <a-form-item label="账号" class="form-item">
            <a-input v-model:value="formState.userAccount" disabled />
          </a-form-item>
          <a-form-item label="昵称" class="form-item">
            <a-input v-model:value="formState.userName" placeholder="请输入昵称" />
          </a-form-item>
        </div>

        <!--        &lt;!&ndash; 邮箱 &ndash;&gt;-->
        <!--        <a-form-item label="邮箱">-->
        <!--          <a-input v-model:value="formState.email" />-->
        <!--        </a-form-item>-->

        <!--        &lt;!&ndash; 手机号和出生日期行 &ndash;&gt;-->
        <!--        <div class="form-row">-->
        <!--          <a-form-item label="手机号" class="form-item">-->
        <!--            <a-input v-model:value="formState.phone" placeholder="请输入手机号" />-->
        <!--          </a-form-item>-->
        <!--          <a-form-item label="出生日期" class="form-item">-->
        <!--            <a-date-picker-->
        <!--              v-model:value="formState.birthday"-->
        <!--              style="width: 100%"-->
        <!--              placeholder="选择出生日期"-->
        <!--            />-->
        <!--          </a-form-item>-->
        <!--        </div>-->

        <!-- 个人简介 -->
        <a-form-item label="个人简介">
          <a-textarea
            v-model:value="formState.userProfile"
            placeholder="介绍一下自己..."
            :maxlength="200"
            :auto-size="{ minRows: 3, maxRows: 5 }"
          />
          <div class="char-count">{{ formState.userProfile?.length || 0 }} / 200</div>
        </a-form-item>

        <!-- 操作按钮 -->
        <div class="form-actions">
          <a-button @click="handleCancel">取消</a-button>
          <a-button type="primary" @click="handleOk">保存更改</a-button>
        </div>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import type { Dayjs } from 'dayjs'
import { editPictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { userEditUsingPost } from '@/api/userController.ts'

interface Props {
  userInfo: API.UserVO
  onSuccess: () => void
}

const props = defineProps<Props>()
const open = ref<boolean>(false)

const formState = reactive({
  ...props.userInfo,
})

// 监听props变化，初始化表单数据
watch(
  () => props.userInfo,
  (newValue) => {
    formState.userName = newValue.userName
    formState.userProfile = newValue.userProfile
  },
)

const showModal = () => {
  open.value = true
}

const handleOk = async () => {
  const res = await userEditUsingPost({
    id: props.userInfo.id,
    ...formState,
  })
  if (res.data.code === 0 && res.data.data) {
    props.onSuccess()
  } else {
    message.error(res.data.message)
  }
  open.value = false
}

const handleCancel = () => {
  open.value = false
}

defineExpose({
  showModal,
})
</script>

<style scoped>
#userEditModal {
  /* 模态框全局样式 */
}

/* 表单行布局 */
.form-row {
  display: flex;
  gap: 16px;
}

.form-item {
  flex: 1;
  margin-bottom: 0;
}

/* 字符计数 */
.char-count {
  text-align: right;
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

/* 调整Ant Design默认样式 */
:deep(.ant-form-item-label) {
  font-weight: 500;
}

:deep(.ant-input-disabled) {
  background-color: #f5f5f5;
  color: rgba(0, 0, 0, 0.85);
}
</style>
