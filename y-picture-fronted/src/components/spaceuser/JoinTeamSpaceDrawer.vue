<template>
  <a-drawer
    title="已加入的团队空间"
    :placement="placement"
    :closable="false"
    :open="open"
    @close="onClose"
  >
    <a-list item-layout="horizontal" :data-source="props.spaceUserVO">
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta :description="`空间角色:${SPACE_ROLE_MAP[item.spaceRole]}`">
            <template #title>
              <a :href="`/spaceDetail/${item.spaceId}`">空间名称:{{ item.space.spaceName }}</a>
            </template>
          </a-list-item-meta>
        </a-list-item>
      </template>
    </a-list>
  </a-drawer>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import type { DrawerProps } from 'ant-design-vue'
import { SPACE_ROLE_MAP } from '@/constants/space.ts'

const placement = ref<DrawerProps['placement']>('right')
const open = ref<boolean>(false)

interface Props {
  spaceUserVO: API.SpaceUserVO[]
}

const props = defineProps<Props>()
const showDrawer = () => {
  open.value = true
}

const onClose = () => {
  open.value = false
}
defineExpose({
  showDrawer,
})
</script>
