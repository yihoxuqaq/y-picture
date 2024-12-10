<template>
  <div id="layout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="layoutHead">
        <GlobalHead />
      </a-layout-header>
      <a-layout-content class="layoutContent">
        <RouterView />
      </a-layout-content>
      <a-layout-footer class="layoutFooter">智能云图库</a-layout-footer>
    </a-layout>
  </div>
</template>
<script setup lang="ts">
import GlobalHead from '@/components/GlobalHead.vue'
</script>

<style scoped>
#layout {
}

.layoutHead {
  background-color: white;
}
.layoutContent{
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 20px;
}
.layoutFooter{
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
