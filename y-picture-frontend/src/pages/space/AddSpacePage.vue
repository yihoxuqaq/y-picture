<template>
  <div id="addSpacePage">
    <h2 style="margin-bottom: 16px">
      {{ route.query?.id ? '修改空间' : '创建空间' }}
    </h2>
    <a-form layout="vertical" :model="spaceForm" @finish="handleSubmit">
      <a-form-item label="名称" name="name">
        <a-input v-model:value="spaceForm.spaceName" placeholder="请输入空间名称" />
      </a-form-item>
      <a-form-item label="空间级别" name="spaceLevel">
        <a-select
          v-model:value="spaceForm.spaceLevel"
          :options="SPACE_LEVEL_OPTIONS"
          placeholder="请选择空间级别"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">提交</a-button>
      </a-form-item>
    </a-form>
    <a-card title="空间级别介绍">
      <a-typography-paragraph>
        * 目前仅支持开通普通版，如需升级空间，请联系
        <a href="https://www.4399.com" target="_blank">站长</a>。
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList" :key="spaceLevel.value">
        {{ spaceLevel.text }}： 大小 {{ formatSize(spaceLevel.maxSize) }}， 数量
        {{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  addSpaceUsingPost,
  getSpaceLevelListUsingGet,
  getSpaceVoByIdUsingGet,
  updateSpaceUsingPost,
} from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { SPACE_LEVEL_OPTIONS } from '@/constants/space.ts'
import { formatSize } from '@/utils'

const space = ref<API.SpaceVO>()
const spaceLevelList = ref<API.SpaceLevel[]>([])
const spaceForm = reactive<API.SpaceAddRequest>({})
const router = useRouter()

/**
 * 提交表单
 */
const handleSubmit = async () => {
  const id = space.value?.id
  let res
  if (!id) {
    res = await addSpaceUsingPost({
      ...spaceForm,
    })
    if (res.data.code === 0 && res.data.data) {
      message.success('创建成功')
      // 跳转到空间详情页
      router.replace({
        path: `/spaceDetail/${res.data.data}`,
      })
    } else {
      message.error('创建失败，' + res.data.message)
    }
  } else {
    res = await updateSpaceUsingPost({
      id: id,
      ...spaceForm,
    })
    if (res.data.code === 0 && res.data.data) {
      message.success('修改成功')
      // 跳转到空间详情页
      router.push({
        path: `/space/${res.data.data}`,
      })
    } else {
      message.error('修改失败，' + res.data.message)
    }
  }
}

const route = useRoute()

// 获取老数据
const getOldspace = async () => {
  // 获取数据
  const id = route.query?.id
  if (id) {
    const res = await getSpaceVoByIdUsingGet({
      id: id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      space.value = data
      spaceForm.spaceName = data.spaceName
      spaceForm.spaceLevel = data.spaceLevel
    }
  }
}

// 获取空间级别
const fetchSpaceLevelList = async () => {
  const res = await getSpaceLevelListUsingGet()
  if (res.data.code === 0 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('加载空间级别失败，' + res.data.message)
  }
}

onMounted(() => {
  fetchSpaceLevelList()
})

onMounted(() => {
  getOldspace()
})
</script>

<style scoped>
#addSpacePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
