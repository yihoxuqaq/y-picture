<template>
  <div id="pictureManageView">
    <!--    <a-form layout="inline" :model="searchParams" @finish="doSearch">-->
    <!--      <a-form-item label="账号">-->
    <!--        <a-input v-model:value="searchParams.userAccount" placeholder="账号" />-->
    <!--      </a-form-item>-->
    <!--      <a-form-item label="用户名">-->
    <!--        <a-input v-model:value="searchParams.userName" placeholder="昵称" />-->
    <!--      </a-form-item>-->
    <!--      <a-form-item label="角色" name="userRole">-->
    <!--        <a-select v-model:value="searchParams.userRole" style="width: 120px" placeholder="角色">-->
    <!--          <a-select-option value="admin">管理员</a-select-option>-->
    <!--          <a-select-option value="user">用户</a-select-option>-->
    <!--        </a-select>-->
    <!--      </a-form-item>-->
    <!--      <a-form-item>-->
    <!--        <a-space>-->
    <!--          <a-button type="primary" html-type="submit">搜索</a-button>-->
    <!--          <a-button type="primary" ghost @click="resetForm">重新加载数据</a-button>-->
    <!--        </a-space>-->
    <!--      </a-form-item>-->
    <!--    </a-form>-->
    <!--    图片列表-->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :width="100" :src="record.url" />
        </template>
        <template v-else-if="column.dataIndex === 'tags'">
          <div v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">
            <a-tag color="blue">{{ tag }}</a-tag>
          </div>
        </template>
        <!-- 图片信息 -->
        <template v-if="column.dataIndex === 'pictureInfo'">
          <div>格式：{{ record.picFormat }}</div>
          <div>宽度：{{ record.picWidth }}</div>
          <div>高度：{{ record.picHeight }}</div>
          <div>宽高比：{{ record.picScale }}</div>
          <div>大小：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'updateTime'">
          {{ dayjs(record.updateTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button type="primary" danger @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUserUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { listPictureByPageUsingPost } from '@/api/pictureController.ts'

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
})
const dataList = ref<API.Picture[]>([])
const total = ref<number>(0)
const loadDataList = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams,
  })
  if (res.data.code === 0) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('数据加载失败' + res.data.message)
  }
}

//删除数据
const doDelete = async (id) => {
  const res = await deleteUserUsingPost({
    id: id,
  })
  if (res.data.code === 0) {
    loadDataList()
    message.success('删除成功')
  } else {
    message.error('删除失败' + res.data.message)
  }
}
//重新加载数据
// const resetForm = () => {
//   searchParams.userAccount = ''
//   searchParams.userName = ''
//   searchParams.userRole = undefined
//   // 重置分页参数
//   searchParams.current = 1
//   // 重新加载数据
//   loadDataList()
// }
// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: false,
    showTotal: (total) => `共 ${total} 条`,
  }
})
// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  loadDataList()
}
// 获取数据
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  loadDataList()
}
//页面加载时，加载数据
onMounted(() => {
  loadDataList()
})
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '图片',
    dataIndex: 'url',
  },
  {
    title: '名称',
    dataIndex: 'name',
  },
  {
    title: '分类',
    dataIndex: 'category',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '简介',
    dataIndex: 'introduction',
  },
  {
    title: '图片信息',
    dataIndex: 'pictureInfo',
  },
  {
    title: '用户id',
    dataIndex: 'userId',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '更新时间',
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]
</script>

<style scoped>
#pictureManageView {
  max-width: 80%;
  margin: 0 auto;
}
</style>
