<template>
  <div id="pictureSearchForm">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="关键词" name="searchText">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="从名称和简介搜索"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="类型" name="category">
        <a-input v-model:value="searchParams.category" placeholder="请输入类型" allow-clear />
      </a-form-item>
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="请输入标签"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="图片名称" name="name">
        <a-input v-model:value="searchParams.name" placeholder="请输入图片名称" allow-clear />
      </a-form-item>
      <a-form-item label="图片简介" name="introduction">
        <a-input
          v-model:value="searchParams.introduction"
          placeholder="请输入图片简介"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="图片长度" name="picHeight">
        <a-input-number
          v-model:value="searchParams.picHeight"
          placeholder="请输入图片长度"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="图片宽度" name="picWidth">
        <a-input-number
          v-model:value="searchParams.picWidth"
          placeholder="请输入图片宽度"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="图片格式" name="picFormat">
        <a-input v-model:value="searchParams.picFormat" placeholder="请输入图片格式" allow-clear />
      </a-form-item>
      <a-form-item label="图片编辑时间">
        <a-range-picker
          style="width: 400px"
          v-model:value="timeRange"
          :placeholder="[`开始编辑时间`, `结束编辑时间`]"
          show-time
          format="YYYY/MM/DD HH:mm:ss"
          :presets="rangePresets"
          @change="onRangeChange"
        />
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button html-type="reset" @click="doClear">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import dayjs from 'dayjs'

const timeRange = ref<[]>([])

interface Props {
  onSearch: (searchParams: API.PictureQueryRequest) => void
}

const props = defineProps<Props>()

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({})

//清理
const doClear = () => {
  //重置所有查询选项
  Object.keys(searchParams).forEach((key) => {
    searchParams[key] = undefined
  })
  //日期单独清理
  timeRange.value = []
  props.onSearch(searchParams)
}
const doSearch = () => {
  props.onSearch(searchParams)
}

const onRangeChange = (dates: [], dateStrings: string[]) => {
  if (dates.length >= 2) {
    searchParams.startEditTime = dates[0].toDate()
    searchParams.endEditTime = dates[1].toDate()
  } else {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  }
}

//预设时间
const rangePresets = ref([
  { label: '最近7天', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '最近14天', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '最近30天', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '最近90天', value: [dayjs().add(-90, 'd'), dayjs()] },
])
</script>

<style scoped>
#pictureSearchForm {
}

#pictureSearchForm .ant-form-item {
  margin-top: 16px;
}
</style>
