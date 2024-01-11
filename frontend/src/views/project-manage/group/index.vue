<script setup lang="ts">
import { NResult } from 'naive-ui'
</script>
<template>
  <div>
    <n-result status="info" title="信息" description="project user group">
      <template #footer>
        <n-button>我需要信息</n-button>
      </template>
    </n-result>
  </div>
</template>
<style scoped></style>
