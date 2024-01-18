<script setup lang="ts">
import { onMounted, ref } from 'vue'
interface IProp {
  marginLeft?: string
  marginRight?: string
  defaultVersion: string | null
  projectId: string
  versionId?: string | null
}
const props = withDefaults(defineProps<IProp>(), {
  marginLeft: '0',
  marginRight: '0',
  versionId: null,
})
const projectVersionEnable = ref(true)
const currentVersion = ref('')
const options = ref([])
onMounted(() => {
  if (props.defaultVersion) {
    currentVersion.value = props.defaultVersion
  }
  if (props.versionId) {
    currentVersion.value = props.versionId
  }
})
</script>
<template>
  <span v-if="projectVersionEnable" :style="{ marginLeft: marginLeft + 'px', marginRight: marginRight + 'px' }">
    <n-select
      v-model:value="currentVersion"
      size="tiny"
      :options="options"
      filterable
      :placeholder="$t('api_test.api_import.latest_version')"
      clearable
    />
  </span>
</template>

<style scoped></style>
