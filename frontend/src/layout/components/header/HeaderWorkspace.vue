<script setup lang="ts">
import { useRequest } from 'alova'
import { SelectOption } from 'naive-ui'
import { onMounted, ref, watch } from 'vue'
import { IUserDto } from '/@/apis/modules/user-api'
import { getUserWorkspaceList, switchWorkspace } from '/@/apis/modules/workspace-api'
import { useEmitter } from '/@/composables/use-emitter'
import { useUserStore } from '/@/store/modules/user-store'
import { getCurrentWorkspaceId } from '/@/utils/token'

const emitter = useEmitter()
const userStore = useUserStore()
const currentWorkspaceName = ref(sessionStorage.getItem('workspace_name'))
const workspaceOption = ref<Array<SelectOption>>([])
const workspaceOptionCopy = ref<Array<SelectOption>>([])
const workspaceId = ref(getCurrentWorkspaceId())
const searchString = ref('')
const { send } = useRequest(() => getUserWorkspaceList(), {
  immediate: false,
})
const { send: switchWs } = useRequest((wsId) => switchWorkspace(wsId), {
  immediate: false,
})
const changeWs = (value: string, option: SelectOption) => {
  if (!value || workspaceId.value === value) {
    return false
  }
  currentWorkspaceName.value = option.label as string
  _changeWs(value)
}
const _changeWs = (wsId: string) => {
  if (wsId) {
    switchWs(wsId).then((res: IUserDto) => {
      emitter.emit('projectChange', wsId)
      userStore.switchWorkspace(res)
    })
  }
}
/**
 * 查询方法
 * @param s
 */
const query = (s: string | null) => {
  workspaceOption.value = s
    ? workspaceOptionCopy.value.filter((item) => {
        const tmp = item.label as string
        return tmp.toLowerCase().includes(s.toLowerCase())
      })
    : workspaceOptionCopy.value
}
watch(
  () => [currentWorkspaceName.value, searchString.value],
  ([wsName, s]) => {
    /* ... */
    sessionStorage.setItem('workspace_name', wsName as string)
    // 监听查询
    query(s)
  },
)
onMounted(() => {
  send().then((res) => {
    workspaceOption.value = res.map((item) => ({ label: item.name, value: item.id }))
    workspaceOptionCopy.value = res.map((item) => ({ label: item.name, value: item.id }))
    const workspace = workspaceOption.value.filter((w) => w.value === workspaceId.value)
    if (workspace.length > 0) {
      currentWorkspaceName.value = workspace[0].label as string
      workspaceOption.value = workspaceOption.value.filter((w) => w.value !== workspaceId.value)
      workspaceOption.value.unshift(workspace[0])
    } else {
      currentWorkspaceName.value = res[0].name
      _changeWs(res[0].id)
    }
  })
})
</script>
<template>
  <div>
    <n-tooltip trigger="hover">
      <template #trigger>
        <n-popselect
          v-model:value="workspaceId"
          :options="workspaceOption"
          trigger="click"
          scrollable
          @update-value="changeWs"
        >
          <n-button text>{{ currentWorkspaceName || '弹出选择' }}</n-button>
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
          <template #empty> 没啥看的，这里是空的 </template>
          <!-- <template #action> 如果你点开了这个例子，你可能需要它 </template> -->
        </n-popselect>
      </template>
      {{ currentWorkspaceName }}
    </n-tooltip>
  </div>
</template>

<style scoped></style>
