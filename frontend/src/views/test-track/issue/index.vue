<script setup lang="ts">
import { NResult } from 'naive-ui'
</script>
<template>
  <div>
    <n-result status="info" title="信息" description="track issue">
      <template #footer>
        <n-button>track issue</n-button>
      </template>
    </n-result>
  </div>
</template>
<style scoped></style>
