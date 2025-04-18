<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? '修改图片' : '创建图片' }}
    </h2>
    <a-typography-paragraph v-if="spaceId" type="secondary">
      保存至空间：<a :href="`/space/${spaceId}`" target="_blank">{{ spaceId }}</a>
    </a-typography-paragraph>

    <a-tabs v-model:activeKey="activeKey">
      <a-tab-pane key="file" tab="图片上传">
        <PictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="URL上传" force-render>
        <PictureUploadUrl :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
    </a-tabs>
    <!--    编辑图片-->
    <div class="btnCropper">
      <a-space size="middle">
        <a-button type="primary" v-if="picture" @click="openPictureCropper">编辑图片</a-button>
        <a-button type="primary" v-if="picture" @click="openPicturePainting">ai扩图</a-button>
      </a-space>
      <PictureCropper
        :pictureUrl="picture?.url"
        ref="pictureCropperRef"
        :picture="picture"
        :onSuccess="onCropperSuccess"
        :spaceId="spaceId"
      />
      <PicturePainting
        ref="picturePaintingRef"
        :picture-url="picture?.url"
        :picture="picture"
        :space-id="spaceId"
        :onSuccess="onPaintingSuccess"
      />
    </div>
    <!--    图片表单-->
    <a-form v-if="picture" layout="vertical" :model="pictureForm" @finish="handleSubmit">
      <a-form-item label="名称" name="name">
        <a-input v-model:value="pictureForm.name" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="请输入简介"
          :rows="2"
          autoSize
          allowClear
        />
      </a-form-item>
      <a-form-item label="分类" name="category">
        <a-auto-complete
          v-model:value="pictureForm.category"
          :options="categoryOptions"
          placeholder="请输入分类"
          allowClear
        />
      </a-form-item>
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="pictureForm.tags"
          :options="tagOptions"
          mode="tags"
          placeholder="请输入标签"
          allowClear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">创建</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import PictureUploadUrl from '@/components/PictureUploadUrl.vue'
import PictureCropper from '@/components/PictureCropper.vue'
import PicturePainting from '@/components/PicturePainting.vue'

const picture = ref<API.PictureVO>()
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

const pictureForm = reactive<API.PictureEditRequest>({})
const router = useRouter()
const activeKey = ref<'file' | 'url'>('file')
const pictureCropperRef = ref()
// 空间 id
const spaceId = computed(() => {
  return route.query?.spaceId
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    spaceId: spaceId.value,
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('创建成功')
    // 跳转到图片详情页
    router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error('创建失败，' + res.data.message)
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
const route = useRoute()
const picturePaintingRef = ref()

// 获取老数据
const getOldPicture = async () => {
  // 获取数据
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id: id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}
//打开编辑图片弹框
const openPictureCropper = () => {
  if (pictureCropperRef.value) {
    pictureCropperRef.value.openModal()
  }
}

//打开编辑图片弹框
const openPicturePainting = () => {
  if (picturePaintingRef.value) {
    picturePaintingRef.value.openModal()
  }
}
onMounted(() => {
  getOldPicture()
})

onMounted(() => {
  getTagCategoryOptions()
})

const onPaintingSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}
const onCropperSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}
</script>

<style scoped>
#addPicturePage {
  max-width: 720px;
  margin: 0 auto;
}

#addPicturePage .btnCropper {
  text-align: center;
  margin: 16px 0;
}
</style>
