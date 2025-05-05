<template>
  <div>
    <a-modal v-model:visible="visible" title="图片分享" @cancel="closeModal" :footer="false">
      <h4>复制分享链接</h4>
      <a-typography-link copyable>
        {{ link }}
      </a-typography-link>
      <div style="margin-bottom: 16px" />
      <h4>手机扫码查看</h4>
      <a-qrcode :value="link" />
    </a-modal>
  </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'

interface Props {
  link: string
}

const props = withDefaults(defineProps<Props>(), {
  link: 'https://www.4399.com',
})
// 是否可见
const visible = ref(false)

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}

//对外暴露方法
defineExpose({
  openModal
})
</script>
