<template>
  <div>
    <a-modal v-model:open="open" title="图片审核" @ok="handleOk">
      审核图片id:{{ picture.id }}
      <a-form :model="formState" name="basic" autocomplete="off">
        <a-form-item label="审核状态" name="reviewStatus">
          <a-select
            v-model:value="formState.reviewStatus"
            style="width: 120px"
            placeholder="审核状态"
          >
            <a-select-option :value="0">审核中</a-select-option>
            <a-select-option :value="1">通过</a-select-option>
            <a-select-option :value="2">拒绝</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="审核信息" name="reviewMessage">
          <a-input v-model:value="formState.reviewMessage" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { pictureReviewUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

const open = ref<boolean>(false)

interface Props {
  picture: API.Picture
  onSuccess: () => void
}

const props = defineProps<Props>()
const formState = reactive<API.Picture>({
  ...props.picture,
})
// 监听props变化，初始化表单数据
watch(
  () => props.picture,
  (newValue) => {
    formState.reviewStatus = newValue.reviewStatus
    formState.reviewMessage = newValue.reviewMessage
  },
)

const showModal = () => {
  open.value = true
}

const handleOk = async (e: MouseEvent) => {
  const res = await pictureReviewUsingPost({
    id: props.picture.id,
    ...formState,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('编辑成功')
    props.onSuccess()
  } else {
    message.error(res.data.message)
  }
  open.value = false
}
defineExpose({
  showModal,
})
</script>
<style scoped>
#pictureReviewModal {
}
</style>
