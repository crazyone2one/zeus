<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { ref } from 'vue'
import { getUserGroupList } from '/@/apis/modules/group-api'
import { IUser } from '/@/apis/modules/user-api'
import { IMember, updateWorkspaceMember } from '/@/apis/modules/workspace-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { GROUP_TYPE } from '/@/utils/constants'

interface IProp {
  groupType?: string
  groupScopeId?: string
  projectId?: string
  userResourceUrl?: string
}
const props = withDefaults(defineProps<IProp>(), {
  groupType: '',
  groupScopeId: '',
  projectId: '',
  userResourceUrl: '/user/list',
})
const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const workspaceGroups = ref<Array<SelectOption>>([])
const formRef = ref<FormInst | null>(null)
const rules: FormRules = {
  groupIds: { type: 'array', required: true, message: i18n.t('group.please_select_group'), trigger: 'blur' },
}
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => updateWorkspaceMember(model), {
  // 初始化表单数据
  initialForm: {
    id: '',
    name: '',
  } as IMember,
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})

const emit = defineEmits(['refresh'])
const handleSave = () => {
  formRef.value?.validate((err) => {
    if (!err) {
      submit()
        .then(() => {
          modalDialog.value?.hideModal()
          emit('refresh')
          window.$message.success(i18n.t('commons.save_success'))
        })
        .catch((e) => window.$message.error(e.message))
    } else {
      return
    }
  })
}
const { send } = useRequest((param) => getUserGroupList(param), { immediate: false })
const open = (row: IUser): void => {
  model.value.id = row.id
  model.value.name = row.name
  model.value.email = row.email
  model.value.groupIds = row.groups.map((g: { id: string }) => g.id)
  model.value.workspaceId = props.groupScopeId
  send({ type: GROUP_TYPE.WORKSPACE, resourceId: props.groupScopeId }).then((res) => {
    workspaceGroups.value = res.map((g) => ({
      label: g.name,
      value: g.id,
    }))
  })
  modalDialog.value?.show()
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="$t('member.modify')" dialog-width="30%" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('commons.name')" path="name">
            <n-input v-model:value="model.name" :disabled="true" />
          </n-form-item>
          <n-form-item :label="$t('commons.email')" path="email">
            <n-input v-model:value="model.email" :disabled="true" />
          </n-form-item>
          <n-form-item :label="$t('commons.group')" path="groupIds">
            <n-select
              v-model:value="model.groupIds"
              :options="workspaceGroups"
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
