<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="layoutHead">
        <GlobalHead />
      </a-layout-header>
      <a-layout>
        <GlobalSider class="layoutSider" />
        <a-layout-content class="layoutContent">
          <RouterView />
        </a-layout-content>
      </a-layout>
      <a-layout-footer class="layoutFooter">智能云图库</a-layout-footer>
    </a-layout>
  </div>
</template>
<script setup lang="ts">
import GlobalHead from '@/components/GlobalHead.vue'
import GlobalSider from '@/components/GlobalSider.vue'
</script>

<style scoped>
#basicLayout {
}

.layoutHead {
  background-color: white;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

.layoutContent {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 26px;
}

.layoutFooter {
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}

#basicLayout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
}

#basicLayout .layoutSider {
  background: #fff;
  padding-top: 20px;
  border-right: 0.5px solid #eee;
}
</style>
