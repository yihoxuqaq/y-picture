<template>
  <div id="userManageView">
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="昵称" />
      </a-form-item>
      <a-form-item label="角色" name="userRole">
        <a-select v-model:value="searchParams.userRole" style="width: 120px" placeholder="角色">
          <a-select-option value="admin">管理员</a-select-option>
          <a-select-option value="user">用户</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button type="primary" ghost @click="resetForm">重新加载数据</a-button>
      </a-form-item>
    </a-form>
    <!--    用户列表-->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :width="100" :src="record.userAvatar" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="pink">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.dataIndex === 'updateTime'">
          {{ dayjs(record.updateTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="primary" @click="updateUser(record)">编辑</a-button>
            <a-button type="primary" danger @click="doDelete(record.id)">删除</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
    <UserUpdateModal ref="userUpdateModalRef" :oleUser="oleUser" :onSuccess="onSuccess" />
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUserUsingPost, listUsersByPageUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import UserUpdateModal from '@/components/user/UserUpdateModal.vue'

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
})
const dataList = ref<API.UserVO[]>([])
const total = ref<number>(0)
const loadDataList = async () => {
  const res = await listUsersByPageUsingPost({
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
const resetForm = () => {
  searchParams.userAccount = ''
  searchParams.userName = ''
  searchParams.userRole = undefined
  // 重置分页参数
  searchParams.current = 1
  // 重新加载数据
  loadDataList()
  message.success('刷新成功')
}
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
//-------用户编辑--------
const userUpdateModalRef = ref()
const oleUser = ref<API.UserVO>()
const updateUser = (value) => {
  userUpdateModalRef.value.showModal()
  oleUser.value = value
}
const onSuccess = () => {
  loadDataList()
}
const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '昵称',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '角色',
    dataIndex: 'userRole',
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
#userManageView {
  max-width: 80%;
  margin: 0 auto;
}
</style>
