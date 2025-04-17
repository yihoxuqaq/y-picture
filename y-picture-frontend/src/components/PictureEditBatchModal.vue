<template>
  <div>
    <a-modal v-model:visible="visible" title="批量编辑图片" :footer="false" @cancel="closeModal">
      <a-typography-paragraph type="secondary">* 只对当前页面的图片生效</a-typography-paragraph>
      <a-form layout="vertical" :model="dataForm" @finish="handleSubmit">
        <a-form-item label="分类" name="category">
          <a-auto-complete
            v-model:value="dataForm.category"
            :options="categoryOptions"
            placeholder="请输入分类"
            allowClear
          />
        </a-form-item>
        <a-form-item label="标签" name="tagList">
          <a-select
            v-model:value="dataForm.tagList"
            :options="tagOptions"
            mode="tags"
            placeholder="请输入标签"
            allowClear
          />
        </a-form-item>
        <a-form-item label="命名规则" name="nameRule">
          <a-input
            v-model:value="dataForm.nameRule"
            placeholder="请输入命名规则,输入{序号}可动态生成"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">提交</a-button>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  pictureEditByBatchUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

const visible = ref<boolean>(false)
const dataForm = ref<API.PictureEditByBatchRequest>({})

interface Props {
  pictureId: API.PictureVO[]
  spaceId: number | string
  onSuccess: () => void
}

const props = defineProps<Props>()
const openModal = () => {
  visible.value = true
}
/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const res = await pictureEditByBatchUsingPost({
    spaceId: props.spaceId as number,
    pictureIdList: props.pictureId.map((i) => i.id),
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    props.onSuccess()
    closeModal()
  } else {
    message.success('操作失败' + res.data.message)
  }
}
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('加载选项失败，' + res.data.message)
  }
}
onMounted(() => {
  getTagCategoryOptions()
})
defineExpose({
  openModal,
})
const closeModal = () => {
  visible.value = false
}
</script>
