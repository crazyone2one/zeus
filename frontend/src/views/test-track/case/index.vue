<script setup lang="ts">
import { NSplit } from 'naive-ui'
import TestCaseNodeTree from '../modules/TestCaseNodeTree.vue'
import TestCaseList from '/@/views/test-track/case/components/TestCaseList.vue'
</script>
<template>
  <n-split direction="horizontal" :default-size="0.15">
    <template #1>
      <test-case-node-tree />
    </template>
    <template #2>
      <test-case-list />
    </template>
  </n-split>
</template>

<style scoped></style>
