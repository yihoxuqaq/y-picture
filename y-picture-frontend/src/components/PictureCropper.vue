<template>
  <a-modal
    class="pictureCropper"
    v-model:visible="visible"
    title="图片编辑"
    :footer="false"
    @cancel="closeModal"
  >
    <vue-cropper
      ref="cropperRef"
      :img="pictureUrl"
      :autoCrop="true"
      :fixed-box="false"
      :center-box="true"
      :can-move="false"
      :can-move-box="true"
      :info="true"
      outputType="png"
    />
    <div></div>
    <div style="margin-bottom: 16px" />
    <div class="cropperButton">
      <a-space>
        <a-button @click="changeScale(1)">放大</a-button>
        <a-button @click="changeScale(-1)">缩小</a-button>
        <a-button @click="rotateLeft">左旋</a-button>
        <a-button @click="rotateRight">右旋</a-button>
        <a-button type="primary" @click="doCropper" :loading="loading">确定</a-button>
      </a-space>
    </div>
  </a-modal>
</template>
<script setup lang="ts">
import 'vue-cropper/dist/index.css'
import { VueCropper } from 'vue-cropper'
import { ref } from 'vue'
import { uploadPictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

const visible = ref<boolean>(false)
const cropperRef = ref()
const loading = ref<boolean>(false)

interface Props {
  pictureUrl: string
  picture: API.PictureVO
  spaceId: string
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

const openModal = () => {
  visible.value = true
}

const closeModal = () => {
  visible.value = false
}
defineExpose({
  openModal,
})

//缩放图片
const changeScale = (num) => {
  num = num || 1
  cropperRef.value.changeScale(num)
}
//旋转图片

const rotateLeft = () => {
  cropperRef.value.rotateLeft()
}
const rotateRight = () => {
  cropperRef.value.rotateRight()
}

const doCropper = () => {
  cropperRef.value.getCropBlob((blob: Blob) => {
    const fileName = (props.picture.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
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
    params.spaceId = props.spaceId
    const res = await uploadPictureUsingPost(params, {}, file)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
      closeModal()
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    message.error('图片上传失败' + error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style>
.pictureCropper .vue-cropper {
  height: 400px !important;
}

.cropperButton {
  text-align: center;
}
</style>
