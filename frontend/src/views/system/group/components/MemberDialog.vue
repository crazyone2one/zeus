<script setup lang="ts">
import { useRequest } from 'alova'
import { FormRules, SelectOption } from 'naive-ui'
import { reactive, ref } from 'vue'
import { IWorkspaceResource } from '/@/apis/interface'
import { IGroup } from '/@/apis/modules/group-api'
import { IUser, getUserListByResourceUrl } from '/@/apis/modules/user-api'
import { getGroupResource } from '/@/apis/modules/workspace-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { GROUP_PROJECT, GROUP_WORKSPACE } from '/@/utils/constants'
interface IProp {
  group: IGroup
}
const props = defineProps<IProp>()
const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const limitOptionCount = ref(400)
const showUserSearchGetMore = ref(false)
const showResourceSearchGerMore = ref(false)
const currentProject = ref({ id: '', name: '' })
const form = reactive({
  userIds: [],
  sourceIds: [],
})
const initUserUrl = ref('/user/list')
const userOptions = ref<Array<SelectOption>>([])
const usersCopy = ref<Array<SelectOption>>([])
const sourceData = ref<Array<SelectOption>>([])
const sourceDataCopy = ref<Array<SelectOption>>([])
const rules: FormRules = {
  userIds: [{ type: 'array', required: true, message: i18n.t('member.please_choose_member'), trigger: 'blur' }],
  sourceIds: { type: 'array', required: true, message: i18n.t('group.please_select_group'), trigger: 'blur' },
}
/**
 * 加载用户信息
 */
const { send: loadUser } = useRequest((url) => getUserListByResourceUrl(url), { immediate: false })
const { send: loadResource } = useRequest((groupId, groupType) => getGroupResource(groupId, groupType), {
  immediate: false,
})

const parallelRequest = async () => {
  const [userResponse, groupResponse] = await Promise.all([
    loadUser(initUserUrl.value),
    loadResource(props.group.id, props.group.type),
  ])
  // 并行请求完成，继续处理业务...
  handleUserOption(userResponse)
  usersCopy.value = userResponse.map((u) => ({ label: u.name, value: u.id }))
  _setResource(props.group.type, groupResponse)
  handleResourceOption(sourceData.value)
}
const _setResource = (type: string, data: IWorkspaceResource) => {
  switch (type) {
    case GROUP_WORKSPACE:
      sourceData.value = data.workspaces.map((w) => ({ label: w.name, value: w.id }))
      sourceDataCopy.value = data.workspaces.map((w) => ({ label: w.name, value: w.id }))
      break
    case GROUP_PROJECT:
      if (initUserUrl.value === 'user/ws/current/member/list') {
        if (!currentProject.value.id) {
          currentProject.value.id = sessionStorage.getItem('project_id') as string
          currentProject.value.name = sessionStorage.getItem('project_name') as string
        }
        sourceData.value = [{ label: currentProject.value.name, value: currentProject.value.id }]
        sourceDataCopy.value = [{ label: currentProject.value.name, value: currentProject.value.id }]
      } else {
        sourceData.value = data.projects.map((p) => ({ label: p.name, value: p.id }))
        sourceDataCopy.value = data.projects.map((p) => ({ label: p.name, value: p.id }))
      }
      break
    default:
      break
  }
}
const handleResourceOption = (resources: Array<SelectOption>) => {
  if (!resources) {
    return
  }
  showResourceSearchGerMore.value = resources.length > limitOptionCount.value
  sourceData.value = resources.slice(0, limitOptionCount.value)
  if (!form.sourceIds || form.sourceIds.length === 0) {
    return
  }
  _handleSelectOption(form.sourceIds, sourceData.value, sourceDataCopy.value)
}
const handleUserOption = (_users: Array<IUser>) => {
  if (!_users) {
    return
  }
  showUserSearchGetMore.value = _users.length > limitOptionCount.value
  userOptions.value = _users.slice(0, limitOptionCount.value).map((u) => ({ label: u.name, value: u.id }))
  if (!form.userIds || form.userIds.length === 0) {
    return
  }
  _handleSelectOption(form.userIds, userOptions.value, usersCopy.value)
}
const _handleSelectOption = (ids: Array<string>, options: Array<SelectOption>, origins: Array<SelectOption>) => {
  for (let id of ids) {
    let index = options.findIndex((o) => o.value === id)
    if (index <= -1) {
      let obj = origins.find((d) => d.value === id)
      if (obj) {
        options.unshift(obj)
      }
    }
  }
}
const open = (): void => {
  parallelRequest()
  modalDialog.value?.show()
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="false">
    <n-modal-dialog ref="modalDialog" :title="$t('member.create')">
      <template #content>
        <n-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('commons.member')" path="userIds">
            <n-select
              v-model:value="form.userIds"
              :options="userOptions"
              filterable
              multiple
              :placeholder="$t('member.please_choose_member')"
            />
          </n-form-item>
          <n-form-item :label="$t('commons.group')" path="groupIds">
            <n-select
              v-model:value="form.sourceIds"
              :options="sourceData"
              filterable
              multiple
              :placeholder="$t('group.please_select_group')"
            />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
