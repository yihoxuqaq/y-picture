<template>
  <div id="pictureUploadByURL">
    <div class="url-picture-upload">
      <a-input-group compact style="margin-bottom: 16px">
        <a-input
          v-model:value="fileUrl"
          style="width: calc(100% - 120px)"
          placeholder="请输入图片 URL"
        />
        <a-button type="primary" :loading="loading" @click="handleUpload" style="width: 120px">
          提交
        </a-button>
      </a-input-group>
      <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
    </div>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { uploadPictureByUrlUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRoute } from 'vue-router'

interface Props {
  pictureVO: API.PictureVO
  onSuccess: (newPicture) => void
}

const props = defineProps<Props>()

const loading = ref<boolean>(false)
const fileUrl = ref<string>()
const picture = ref<string>()
const route = useRoute()
const handleUpload = async () => {
  loading.value = true
  const params = {
    fileUrl: fileUrl.value,
    spaceId: route.query.spaceId ? route.query.spaceId : null,
    id: props.pictureVO.id ? props.pictureVO.id : null,
  }
  const res = await uploadPictureByUrlUsingPost(params)
  if (res.data.code === 0 && res.data.data) {
    picture.value = res.data.data
    props.onSuccess(res.data.data)
  } else {
    message.error('图片上传失败,' + res.data.message)
  }
  loading.value = false
}
</script>

<style scoped>
#pictureUploadByURL {
}
</style>
