<template>
  <a-modal
    v-model:open="open"
    class="image-cropper"
    :footer="false"
    title="图片裁剪"
    @ok="handleOk"
  >
    <vue-cropper
      ref="cropperRef"
      :img="picture?.url"
      :autoCrop="true"
      :fixedBox="false"
      :centerBox="true"
      :canMoveBox="true"
      :info="true"
      outputType="png"
    />
    <div style="margin-bottom: 16px" />
    <!-- 图片操作 -->
    <div class="image-cropper-actions">
      <a-space>
        <a-button @click="rotateLeft">向左旋转</a-button>
        <a-button @click="rotateRight">向右旋转</a-button>
        <a-button @click="changeScale(1)">放大</a-button>
        <a-button @click="changeScale(-1)">缩小</a-button>
        <a-button type="primary" :loading="loading" @click="handleConfirm">确认</a-button>
      </a-space>
    </div>
  </a-modal>
</template>
<script setup lang="ts">
import 'vue-cropper/dist/index.css'
import { VueCropper } from 'vue-cropper'

import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { uploadPictureUsingPost } from '@/api/pictureController.ts'

const open = ref<boolean>(false)

const showModal = () => {
  open.value = true
}
const handleOk = (e: MouseEvent) => {
  console.log(e)
  open.value = false
}
defineExpose({
  showModal,
})

interface Props {
  picture?: API.PictureVO
  onSuccess: (newPicture?: API.PictureVO) => void
}

const props = defineProps<Props>()
const loading = ref<boolean>(false)

// 确认裁剪
const handleConfirm = () => {
  cropperRef.value.getCropBlob((blob: Blob) => {
    const fileName = (props.picture?.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
    // 上传图片
    handleUpload({ file })
  })
}
/**
 * 上传
 * @param file
 */
const handleUpload = async ({ file }: any) => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = props.picture ? { id: props.picture.id } : {}
    const res = await uploadPictureUsingPost(params, {}, file)
    if (res.data.code === 0 && res.data.data) {
      props.onSuccess(res.data.data)
      message.success('图片上传成功')
      open.value = false
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败')
  } finally {
    loading.value = false
  }
}

// 编辑器组件的引用
const cropperRef = ref()

// 向左旋转
const rotateLeft = () => {
  cropperRef.value.rotateLeft()
}

// 向右旋转
const rotateRight = () => {
  cropperRef.value.rotateRight()
}

// 缩放
const changeScale = (num: number) => {
  cropperRef.value.changeScale(num)
}
</script>

<style scoped>
.image-cropper {
  text-align: center;
}

.image-cropper .vue-cropper {
  height: 400px;
}
</style>
