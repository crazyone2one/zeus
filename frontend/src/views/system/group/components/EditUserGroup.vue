<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { ref } from 'vue'
import { IGroup, createUserGroup, modifyUserGroup } from '/@/apis/modules/group-api'
import { getProjectList } from '/@/apis/modules/project-api'
import { getWorkspaces } from '/@/apis/modules/workspace-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const createUserForm = ref<FormInst | null>(null)
const dialogType = ref('')
const showLabel = ref('')
const show = ref(true)
const isSystem = ref(false)
const workspaceOptions = ref<Array<SelectOption>>([])
const title = ref(i18n.t('group.create'))
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => (dialogType.value === 'create' ? createUserGroup(model) : modifyUserGroup(model)), {
  // 初始化表单数据
  initialForm: {
    id: '',
    name: '',
    global: false,
  } as IGroup,
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})
const rules: FormRules = {
  name: [
    { required: true, message: i18n.t('commons.input_name'), trigger: 'blur' },
    { min: 2, max: 50, message: i18n.t('commons.input_limit', [2, 50]), trigger: 'blur' },
  ],
  type: [{ required: true, message: i18n.t('group.select_type'), trigger: 'blur' }],
  scopeId: [{ required: true, message: i18n.t('group.select_belong_organization'), trigger: 'blur' }],
  description: { min: 2, max: 90, message: i18n.t('commons.input_limit', [2, 90]), trigger: 'blur' },
}
const typeOptions = [
  { label: i18n.t('group.system'), value: 'SYSTEM' },
  { label: i18n.t('group.workspace'), value: 'WORKSPACE' },
  { label: i18n.t('group.project'), value: 'PROJECT' },
]
const emit = defineEmits(['refresh'])
const handleSave = () => {
  createUserForm.value?.validate((err) => {
    if (!err) {
      // console.log(`output->model.value`, model.value)
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
const { send: loadWs } = useRequest(() => getWorkspaces(), { immediate: false })
const { send: loadProject } = useRequest(() => getProjectList(), { immediate: false })
// const { send: loadGroupResource } = useRequest((id, type) => getGroupResource(id, type), { immediate: false })
const initWorkspace = (scopeId: string | undefined) => {
  loadWs().then((res) => {
    const data = res
    if (data) {
      workspaceOptions.value = data.map((item) => ({
        label: item.name,
        value: item.id,
      }))
      let workspace = workspaceOptions.value.find((item) => item.value === scopeId)
      if (workspace || !scopeId) {
        showLabel.value = i18n.t('project.owning_workspace')
      } else {
        showLabel.value = i18n.t('project.owning_project')
        loadProject().then(
          (res) =>
            (workspaceOptions.value = res.map((item) => ({
              label: item.name,
              value: item.id,
            }))),
        )
      }
    }
  })
}
const open = (_type: string, _title: string, row?: IGroup): void => {
  workspaceOptions.value = []
  initWorkspace(row?.id)
  showLabel.value = ''
  title.value = _title ?? title.value
  isSystem.value = false
  show.value = true
  dialogType.value = _type ?? dialogType.value
  model.value = Object.assign({}, row)
  if (_type !== 'create') {
    if (model.value.type === 'SYSTEM') {
      model.value.global = true
      show.value = false
    } else {
      model.value.global = model.value.scopeId === 'global'
      show.value = !model.value.global
    }
  }
  modalDialog.value?.show()
}
const handleUpdateValue = (value: string) => {
  console.log(`output->value`, value)
  if (value === 'SYSTEM') {
    isSystem.value = true
    model.global = true
    handleChange(true)
  } else {
    isSystem.value = false
  }
}
const handleChange = (value: boolean) => {
  show.value = isSystem.value ? false : value
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="title" dialog-width="50%" @confirm="handleSave">
      <template #content>
        <n-form
          ref="createUserForm"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('commons.name')" path="name">
            <n-input v-model:value="model.name" :placeholder="$t('user.input_name')" />
          </n-form-item>
          <n-form-item :label="$t('group.type')" path="type">
            <n-select
              v-model:value="model.type"
              :options="typeOptions"
              :placeholder="$t('group.select_type')"
              :disabled="dialogType === 'edit'"
              @update:value="handleUpdateValue"
            />
          </n-form-item>
          <n-form-item :label="$t('group.description')" path="description">
            <n-input v-model:value="model.description" type="textarea" />
          </n-form-item>
          <n-form-item :label="$t('group.global_group')">
            <n-switch
              v-model:value="model.global"
              :disabled="dialogType === 'edit' || model.type === 'SYSTEM'"
              @update:value="handleChange"
            />
          </n-form-item>
          <n-form-item v-if="show" :label="showLabel" path="scopeId">
            <n-select
              v-model:value="model.scopeId"
              :options="workspaceOptions"
              :placeholder="$t('project.please_choose_workspace')"
              :disabled="dialogType === 'edit'"
              filterable
              clearable
            />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
