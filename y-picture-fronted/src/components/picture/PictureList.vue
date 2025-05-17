<template>
  <div id="pictureList">
    <a-list
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="pictureList"
    >
      <template #renderItem="{ item }">
        <a-list-item style="padding: 0">
          <a-card hoverable @click="doDetail(item)">
            <template #cover>
              <img
                style="height: 180px; object-fit: cover"
                :alt="item.name"
                :src="item.url"
                loading="lazy"
              />
            </template>
            <template #actions>
              <span v-if="canEditPicture" @click="(e) => editPicture(item.id, e)">
                <edit-outlined key="edit"  />
                编辑
              </span>
              <span v-if="canDeletePicture" @click="(e) => deletePicture(item.id, e)">
                <DeleteOutlined key="setting"  />
                删除
              </span>
              <span @click="(e) => doShare(item.id, e)">
                <ShareAltOutlined key="ellipsis"  />
                分享
              </span>
            </template>
            <a-card-meta>
              <template #description>
                <a-flex>
                  <a-tag color="green">
                    {{ item.category ?? '默认' }}
                  </a-tag>
                  <a-tag v-for="tag in item.tags" :key="tag">
                    {{ tag }}
                  </a-tag>
                </a-flex>
              </template>
            </a-card-meta>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
    <ShareModal :link="shareLink" ref="shareModalRef" />
  </div>
</template>
<script lang="ts" setup>
import { EditOutlined, DeleteOutlined, ShareAltOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { deletePictureUsingPost } from '@/api/pictureController.ts'
import { useRouter } from 'vue-router'
import ShareModal from '@/components/picture/ShareModal.vue'
import { computed, ref } from 'vue'
import { SPACE_PERMISSION_ENUM } from '@/constants/space.ts'

interface Props {
  pictureList: API.SpaceVO[]
  doReload: () => void
  spaceVO: API.SpaceVO
}

const props = defineProps<Props>()
//删除图片
const deletePicture = async (id, e) => {
  e.stopPropagation()
  const res = await deletePictureUsingPost({
    id: id,
  })
  if (res.data.code === 0) {
    props.doReload()
    message.success('删除成功')
  } else {
    message.error('删除失败' + res.data.message)
  }
}
//图片详情
const router = useRouter()
const doDetail = (item) => {
  router.push({
    name: '图片详情',
    query: {
      pictureId: item.id,
      spaceId: props.spaceVO.id,
      spaceType: props.spaceVO.spaceType,
    },
  })
}
//编辑图片
const editPicture = (id, e) => {
  e.stopPropagation()
  router.push({
    path: '/addPicture',
    query: {
      pictureId: id,
      spaceId: props.spaceVO.id,
      spaceType: props.spaceVO.spaceType,
    },
  })
}
//分享链接
const shareLink = ref<string>()
const shareModalRef = ref()
const doShare = (id, e) => {
  e.stopPropagation()
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture?pictureId=${id}`
  if (shareLink.value) {
    shareModalRef.value.openModal()
  }
}

// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (props.spaceVO?.permissionList ?? []).includes(permission)
  })
}

// 定义权限检查
const canEditPicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDeletePicture = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)
</script>

<style scoped>
#pictureList {
}
</style>
