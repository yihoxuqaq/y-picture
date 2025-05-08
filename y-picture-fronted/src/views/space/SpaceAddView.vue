<template>
  <div id="spaceAddView">
    <a-typography-title :level="3">
      目前暂未开通{{ SPACE_TYPE_MAP[spaceType] }}，请先{{ SPACE_TYPE_MAP[spaceType] }}
    </a-typography-title>
   <a-card>
     <a-form
       :model="formState"
       name="basic"
       autocomplete="off"
       @finish="handleSubmit"
       layout="vertical"
     >
       <a-form-item
         label="空间名称"
         name="spaceName"
         :rules="[{ required: true, message: '请输入空间名称 !' }]"
       >
         <a-input v-model:value="formState.spaceName" placeholder="请输入空间名称" />
       </a-form-item>

       <a-form-item
         label="空间类型"
         name="spaceType"
         :rules="[{ required: true, message: '请选择空间类型' }]"
       >
         <a-select v-model:value="formState.spaceType" placeholder="请选择空间类型">
           <a-select-option value="0">私有空间</a-select-option>
           <a-select-option value="1">团队空间</a-select-option>
         </a-select>
       </a-form-item>
       <a-form-item label="空间级别" name="spaceLevel">
         <a-select v-model:value="formState.spaceLevel" placeholder="请选择空间级别">
           <a-select-option value="0">普通版</a-select-option>
           <a-select-option value="1">专业版</a-select-option>
           <a-select-option value="2">旗舰版</a-select-option>
         </a-select>
       </a-form-item>

       <a-form-item>
         <a-button type="primary" html-type="submit">创建空间</a-button>
       </a-form-item>
     </a-form>
   </a-card>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import { spaceAddUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { useRoute, useRouter } from 'vue-router'
import { SPACE_TYPE_MAP } from '../../constants/space.ts'

const router = useRouter()
const route = useRoute()
const spaceType = route.query.spaceType
const formState = reactive<API.SpaceAddRequest>({
  spaceType: spaceType,
})
const handleSubmit = async () => {
  const res = await spaceAddUsingPost({
    ...formState,
  })
  if (res.data.code === 0) {
    message.success('创建成功')
    await router.replace(`/spaceDetail/${res.data.data}`)
  } else {
    message.error('创建失败,' + res.data.message)
  }
}
</script>

<style scoped>
#spaceAddView {
  max-width: 980px;
  margin: 0 auto;
}
</style>
