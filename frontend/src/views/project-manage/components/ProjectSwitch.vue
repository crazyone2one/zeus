<script setup lang="ts">
import { useRequest } from 'alova'
import { SelectOption } from 'naive-ui'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUserProjectList, switchProject } from '/@/apis/modules/project-api'
import { i18n } from '/@/i18n'
import { getCurrentProjectId, getCurrentUserId, getCurrentWorkspaceId } from '/@/utils/token'

const router = useRouter()
const route = useRoute()
const currentProject = ref('')
const projectId = ref('')
const searchString = ref('')
const options = ref<Array<SelectOption>>([])
const searchArray = ref<Array<SelectOption>>([])
const userId = computed(() => {
  return getCurrentUserId()
})
const { send: loadProject } = useRequest((url) => getUserProjectList(url), { immediate: false })
const { send: sw } = useRequest((param) => switchProject(param), { immediate: false })
const changeWs = (value: string) => {
  console.log(`output->value`, value)
  let currentProjectId = getCurrentProjectId()
  if (value === currentProjectId) {
    return
  }
  sw({ id: userId.value, lastProjectId: value }).then((res) => {
    sessionStorage.setItem('project_id', res.lastProjectId)
    // TODO emit
    // reloadPage()
    changeProjectName(value)
  })
}

const changeProjectName = (projectId: string) => {
  if (projectId) {
    const project = searchArray.value.filter((item) => item.value === projectId)
    if (project.length > 0) {
      currentProject.value = project[0].label as string
    }
  } else {
    currentProject.value = i18n.t('project.select')
  }
}
const init = () => {
  let data = {
    userId: getCurrentUserId(),
    workspaceId: route.params.workspaceId || getCurrentWorkspaceId(),
  }
  loadProject(data).then((res) => {
    options.value = res.map((item) => ({ label: item.name, value: item.id }))
    searchArray.value = res.map((item) => ({ label: item.name, value: item.id }))
    let projectId = getCurrentProjectId()
    if (projectId) {
      if (
        route.fullPath.startsWith('/api') &&
        route.params.projectId &&
        route.params.projectId !== projectId &&
        route.params.projectId !== 'all'
      ) {
        changeWs(route.params.projectId as string)
      }
      if (searchArray.value.length > 0 && searchArray.value.map((p) => p.value).indexOf(projectId) === -1) {
        changeWs(options.value[0].value as string)
      }
    } else {
      if (options.value.length > 0) {
        changeWs(options.value[0].value as string)
      }
    }
    changeProjectName(projectId)
  })
}
const handleCreate = () => router.push('/project/create')
const handleAll = () => router.push('/project/all')
onMounted(() => {
  init()
})
</script>
<template>
  <div class="py-2 ml-1">
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
          <template #action>
            <div v-permission="['WORKSPACE_PROJECT_MANAGER:READ']">
              <n-button
                v-permission="['WORKSPACE_PROJECT_MANAGER:READ+CREATE']"
                strong
                secondary
                type="tertiary"
                size="tiny"
                @click="handleCreate"
              >
                {{ $t('project.create') }}
              </n-button>
              <n-button
                v-permission="['WORKSPACE_PROJECT_MANAGER:READ']"
                strong
                secondary
                type="primary"
                size="tiny"
                class="ml-2"
                @click="handleAll"
              >
                {{ $t('commons.show_all') }}
              </n-button>
            </div>
          </template>
        </n-popselect>
      </template>
      {{ currentProject }}
    </n-tooltip>
  </div>
</template>

<style scoped></style>
