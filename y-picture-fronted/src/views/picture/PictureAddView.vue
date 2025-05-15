<template>
  <div id="addPictureView">
    <div class="center-container">
      <div>
        <div style="text-align: center">
          <a-typography v-if="picture">
            <a-typography-title :level="3">
              <CloudUploadOutlined />
              修改图片
            </a-typography-title>
            <a-typography-text type="secondary">
              当前图片属于{{
                SPACE_TYPE_MAP[route.query.spaceType]
                  ? SPACE_TYPE_MAP[route.query.spaceType]
                  : '公共图库'
              }}
              <router-link v-if="route.query.spaceId" :to="`/spaceDetail/${route.query.spaceId}`"
                >{{ loginUser.userName }}的{{ SPACE_TYPE_MAP[route.query.spaceType] }}
                （点击回到空间）
              </router-link>
            </a-typography-text>
          </a-typography>
          <a-typography v-else>
            <a-typography-title :level="3">
              <SendOutlined />
              发布图片
            </a-typography-title>
            <a-typography-text type="secondary">
              图片将上传至{{
                SPACE_TYPE_MAP[route.query.spaceType]
                  ? SPACE_TYPE_MAP[route.query.spaceType]
                  : '公共图库'
              }}
              <router-link v-if="route.query.spaceId" :to="`/spaceDetail/${route.query.spaceId}`"
                >{{ loginUser.userName }}的{{ SPACE_TYPE_MAP[route.query.spaceType] }}
                （点击回到空间）
              </router-link>
            </a-typography-text>
          </a-typography>
        </div>
        <a-flex wrap="wrap" gap="small" class="content-wrapper">
          <a-card style="width: 600px">
            <a-tabs v-model:activeKey="activeKey">
              <a-tab-pane key="1" tab="图片上传">
                <PictureUpload :picture="picture" :onSuccess="onSuccess" />
              </a-tab-pane>
              <a-tab-pane key="2" tab="地址上传">
                <PictureUploadByURL :onSuccess="onSuccess" :pictureVO="picture" />
              </a-tab-pane>
            </a-tabs>
            <template #actions v-if="picture">
              <a-button type="primary" ghost @click="openCropperModel">
                <ToolTwoTone />
                图片裁剪
              </a-button>
            </template>
          </a-card>
          <a-card size="small" style="width: 500px" title="图片信息" v-if="picture">
            <a-form layout="vertical" :model="pictureForm" @finish="handleSubmit">
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
                  placeholder="请输入分类"
                  allowClear
                />
              </a-form-item>
              <a-form-item label="标签" name="tags">
                <a-select
                  v-model:value="pictureForm.tags"
                  mode="tags"
                  placeholder="请输入标签"
                  allowClear
                />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" html-type="submit" style="width: 100%">
                  <span v-if="route.query.pictureId">修改</span>
                  <span v-else>创建</span>
                </a-button>
              </a-form-item>
            </a-form>
          </a-card>
        </a-flex>
      </div>
    </div>
    <PictureCropper ref="pictureCropperRef" :onSuccess="onSuccessCropper" :picture="picture" />
  </div>
</template>
<script lang="ts" setup>
import PictureUpload from '@/components/picture/PictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { editPictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController.ts'
import { ToolTwoTone } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { SendOutlined, CloudUploadOutlined } from '@ant-design/icons-vue'
import PictureUploadByURL from '@/components/picture/PictureUploadByURL.vue'
import PictureCropper from '@/components/picture/PictureCropper.vue'
import { SPACE_TYPE_MAP } from '@/constants/space.ts'
import { useLoginUserStore } from '@/stores'

const activeKey = ref('1')
const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureVO>({})
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

const loginUserStore = useLoginUserStore()
let loginUser = loginUserStore.loginUser

const router = useRouter()
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
    ...values,
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('创建成功')
    // 跳转到图片详情页
    router.push({
      path: '/picture',
      query: {
        pictureId: pictureId,
      },
    })
  } else {
    message.error('操作失败，' + res.data.message)
  }
}
//编辑图片
const route = useRoute()
// 获取老数据
const getOldPicture = async () => {
  // 获取数据
  const pictureId = route.query?.pictureId
  if (pictureId) {
    const res = await getPictureVoByIdUsingGet({
      id: pictureId,
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
const onSuccessCropper = (value) => {
  picture.value = value
}
//个人空间上传图片
onMounted(() => {
  getOldPicture()
})
// 打开图片裁剪框
const pictureCropperRef = ref()
const openCropperModel = () => {
  if (pictureCropperRef.value) {
    pictureCropperRef.value.showModal()
  }
}
</script>
<style scoped>
#addPictureView {
  width: 100%;
  min-height: auto; /* 改为自动高度 */
  display: flex;
  justify-content: center;
  align-items: flex-start; /* 顶部对齐 */
  padding: 20px 0; /* 增加上下内边距 */
}

.center-container {
  display: flex;
  justify-content: center;
  width: 100%;
}

.content-wrapper {
  display: flex;
  justify-content: center;
  max-width: 100%;
  margin: 0 auto; /* 确保水平居中 */
}
</style>
