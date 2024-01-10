<script setup lang="ts">
import { useRequest } from 'alova'
import { SelectOption } from 'naive-ui'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getUserProjectList } from '/@/apis/modules/project-api'
import { getCurrentProjectId, getCurrentUserId, getCurrentWorkspaceId } from '/@/utils/token'

const route = useRoute()
const currentProject = ref('')
const projectId = ref('')
const searchString = ref('')
const options = ref<Array<SelectOption>>([])
const { send: loadProject } = useRequest((url) => getUserProjectList(url), { immediate: false })
const changeWs = (value: string, option: SelectOption) => {
  let currentProjectId = getCurrentProjectId()
  if (value === currentProjectId) {
    return
  }
}
onMounted(() => {
  let data = {
    userId: getCurrentUserId(),
    workspaceId: route.params.workspaceId || getCurrentWorkspaceId(),
  }
  loadProject(data).then((res) => (options.value = res.map((item) => ({ label: item.name, value: item.id }))))
})
</script>
<template>
  <div>
    <n-tooltip trigger="hover">
      <template #trigger>
        <n-popselect v-model:value="projectId" :options="options" trigger="click" scrollable @update-value="changeWs">
          <n-button text>{{ $t('commons.project') }}: {{ currentProject || '弹出选择' }}</n-button>
          <template #header>
            <n-input
              v-model:value="searchString"
              type="text"
              :placeholder="$t('project.search_by_name')"
              clearable
              size="tiny"
            >
              <template #prefix>
                <n-icon>
                  <span class="i-carbon:search" />
                </n-icon>
              </template>
            </n-input>
          </template>
          <template #empty> {{ $t('project.no_data') }} </template>
          <!-- <template #action> 如果你点开了这个例子，你可能需要它 </template> -->
        </n-popselect>
      </template>
      {{ currentProject }}
    </n-tooltip>
  </div>
</template>

<style scoped></style>
