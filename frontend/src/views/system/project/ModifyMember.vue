<script setup lang="ts">
import { useRequest } from 'alova'
import { FormInst, SelectOption } from 'naive-ui'
import { computed, ref } from 'vue'
import { getUserGroupList } from '/@/apis/modules/group-api'
import { modifyProjectMember } from '/@/apis/modules/project-api'
import { IUser } from '/@/apis/modules/user-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { GROUP_PROJECT } from '/@/utils/constants'
import { getCurrentProjectId, getCurrentWorkspaceId } from '/@/utils/token'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const formRef = ref<FormInst | null>(null)
const model = ref<IUser>({} as IUser)
const loading = ref(false)
const groupOptions = ref<Array<SelectOption>>([])
const currentProjectId = computed(() => {
  return getCurrentProjectId()
})
const emit = defineEmits(['refresh'])
const { send: sendGroupList } = useRequest((param) => getUserGroupList(param), { immediate: false })
const { send: modifyMember } = useRequest((param) => modifyProjectMember(param), { immediate: false })
const open = (record: IUser) => {
  modalDialog.value?.show()
  model.value = Object.assign({}, record)
  let groupIds = model.value.groups.map((item) => item.id)
  let param = {
    type: GROUP_PROJECT,
    resourceId: getCurrentWorkspaceId(),
    projectId: currentProjectId.value,
  }
  model.value.groupIds = groupIds
  sendGroupList(param).then((res) => (groupOptions.value = res.map((item) => ({ label: item.name, value: item.id }))))
}
const handleSave = () => {
  let param = {
    id: model.value.id,
    name: model.value.name,
    email: model.value.email,
    phone: model.value.phone,
    groupIds: model.value.groupIds,
    projectId: currentProjectId.value,
  }
  modifyMember(param).then(() => {
    window.$message.success(i18n.t('commons.modify_success'))
    modalDialog.value?.toggleModal()
    emit('refresh')
  })
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="loading">
    <n-modal-dialog ref="modalDialog" :title="$t('member.modify')" dialog-width="30%" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('commons.username')" path="name">
            <n-input v-model:value="model.name" disabled />
          </n-form-item>
          <n-form-item :label="$t('commons.email')" path="email">
            <n-input v-model:value="model.email" disabled />
          </n-form-item>
          <n-form-item :label="$t('commons.phone')" path="phone">
            <n-input v-model:value="model.phone" disabled />
          </n-form-item>
          <n-form-item
            :label="$t('commons.group')"
            path="groupIds"
            :rule="{
              required: true,
              message: $t('group.please_select_group'),
              trigger: ['change'],
            }"
          >
            <n-select
              v-model:value="model.groupIds"
              :options="groupOptions"
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
