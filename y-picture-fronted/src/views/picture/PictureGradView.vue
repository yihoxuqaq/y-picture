<template>
  <div id="pictureGradView">
    <a-typography-title :level="2" style="margin-left: 28px">
      <BugOutlined />
      爬取图片
    </a-typography-title>
    <a-row justify="center">
      <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="5">
        <a-card style="width: 300px; min-height: 75vh">
          <a-form
            :model="formState"
            name="basic"
            layout="vertical"
            @finish="handleSubmit"
            autocomplete="off"
          >
            <a-form-item label="爬取来源">
              <a-select style="width: 100%" placeholder="请选择爬取来源">
                <a-select-option :value="0">必应</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item
              name="searchText"
              label="图片关键词"
              :rules="[{ required: true, message: '请输入关键词' }]"
            >
              <a-input v-model:value="formState.searchText" placeholder="请输入关键词" />
            </a-form-item>
            <a-form-item name="count" label="爬取数量">
              <a-input-number
                style="width: 100%"
                v-model:value="formState.count"
                placeholder="请输入爬取数量"
                max="30"
              />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" style="width: 100%">开始爬取</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="17">
        <a-card style="height: 75vh" class="vertical-scroll">
          <a-list
            :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
            :data-source="dataList"
            :loading="loading"
          >
            <template #renderItem="{ item }">
              <a-list-item style="padding: 0">
                <a-card hoverable>
                  <template #cover>
                    <a-image
                      style="height: 180px; object-fit: cover"
                      :alt="item.title"
                      :src="item.thumbnailUrl"
                      loading="lazy"
                    />
                  </template>
                  <a-card-meta :title="item.title">
                    <template #description>
                      <a :href="item.pageUrl" target="_blank">
                        {{ item.pageUrl }}
                      </a>
                    </template>
                  </a-card-meta>
                  <template #actions>
                    <span @click="doUploadPictureGrad(item.mainImageUrl)">
                      <UploadOutlined :loading="loading" />
                      上传
                    </span>
                    <setting-outlined key="setting" />
                    <setting-outlined key="setting" />
                  </template>
                </a-card>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>
<script setup lang="ts">
import { BugOutlined, UploadOutlined } from '@ant-design/icons-vue'
import { reactive, ref } from 'vue'
import {
  uploadPictureByBatchUsingPost,
  uploadPictureByUrlUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'

const formState = reactive<API.PictureUploadByBatchRequest>({})
const dataList = ref<API.PictureGradVO[]>([])
const loading = ref<boolean>(false)
//抓取图片
const handleSubmit = async () => {
  loading.value = true
  const res = await uploadPictureByBatchUsingPost(formState)
  if (res.data.code === 0) {
    dataList.value = res.data.data
  } else {
    message.error('图片爬取失败,' + res.data.message)
  }
  loading.value = false
}
//上传图片
const doUploadPictureGrad = async (value) => {
  loading.value = true
  const res = await uploadPictureByUrlUsingPost({
    fileUrl: value,
  })
  if (res.data.code === 0) {
    message.success('上传成功')
    // 过滤已上传的图片（新增代码）
    dataList.value = dataList.value.filter(
      item => item.mainImageUrl !== value
    )
  } else {
    message.error('上传失败,' + res.data.message)
  }
  loading.value = false
}
</script>

<style scoped>
#pictureGradView {
  margin: 0 auto;
  background-color: #efefef;
  min-height: 100vh;
}

/* 新增样式 */
.vertical-scroll {
  height: 75vh; /* 与卡片高度保持一致 */
  overflow-y: auto; /* 启用垂直滚动 */
  padding: 8px; /* 增加内边距 */
}

/* 修复Ant Design Card header的定位问题 */
:deep(.ant-card-head) {
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
}
</style>
