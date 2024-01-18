<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '/@/store/modules/user-store'
import { getCurrentProjectId } from '/@/utils/token'

const store = useUserStore()
const moduleOptions = computed(() => {
  return store.testCaseModuleOptions
})
const projectId = computed(() => {
  return getCurrentProjectId()
})
</script>
<template>
  <div></div>
</template>

<style scoped></style>
