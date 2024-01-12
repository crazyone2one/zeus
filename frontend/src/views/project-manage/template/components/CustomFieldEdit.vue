<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { VNodeChild, computed, ref } from 'vue'
import { ICustomField } from '/@/apis/modules/custom-field-api'
import { modifyFieldTemplate, saveFieldTemplate } from '/@/apis/modules/template-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import NSingleHandleDrag from '/@/components/NSingleHandleDrag.vue'
import { i18n } from '/@/i18n'
import { CUSTOM_FIELD_SCENE_OPTION, CUSTOM_FIELD_TYPE_OPTION } from '/@/utils/table-constants'
import { getCurrentProjectId } from '/@/utils/token'

const props = withDefaults(
  defineProps<{
    scene?: string
  }>(),
  { scene: '' },
)

const title = ref(i18n.t('custom_field.create'))
const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const formRef = ref<FormInst | null>(null)

const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => (model.id ? modifyFieldTemplate(model) : saveFieldTemplate(model)), {
  // 初始化表单数据
  initialForm: {
    id: '',
    name: '',
    type: 'input',
    scene: 'TEST_CASE',
    remark: '',
    system: false,
    options: [],
    projectId: getCurrentProjectId(),
  },
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})
const rules: FormRules = {
  name: [
    { required: true, message: i18n.t('test_track.case.input_name'), trigger: 'blur' },
    { max: 64, message: i18n.t('test_track.length_less_than') + '64', trigger: 'blur' },
  ],
  scene: { max: 50, trigger: 'change' },
  type: { max: 50, trigger: 'change' },
}
// const systemNameMap = computed(() => {
//   return SYSTEM_FIELD_NAME_MAP
// })
const fieldTypeOptions = computed(() => {
  return CUSTOM_FIELD_TYPE_OPTION
})
const sceneOptions = computed(() => {
  return CUSTOM_FIELD_SCENE_OPTION
})
const showOptions = ref(['select', 'multipleSelect', 'radio', 'checkbox'].indexOf(model.type) > -1)
const isSystem = computed(() => {
  return model.value.system
})
const isTemplateEdit = computed(() => {
  return !!props.scene
})
const emit = defineEmits(['refresh'])
const renderSceneLabel = (option: SelectOption): VNodeChild => {
  return i18n.t(option.label as string)
}
const renderTypeLabel = (option: SelectOption): VNodeChild => {
  return i18n.t(option.label as string)
}
const handleUpdateTypeValue = (value: string) => {
  showOptions.value = ['select', 'multipleSelect', 'radio', 'checkbox'].indexOf(value) > -1
}

const handleSave = () => {
  formRef.value?.validate((err) => {
    if (!err) {
      if (model.value.name === i18n.t('custom_field.case_priority')) {
        for (let i = 0; i < model.value.options.length; i++) {
          if (model.value.options[i].text !== 'P' + i) {
            window.$message.warning(i18n.t('custom_field.case_priority_option_check_error') + i)
            return
          }
        }
      }
      model.value.projectId = getCurrentProjectId()
      if (['select', 'multipleSelect', 'radio', 'checkbox'].indexOf(model.value.type) > -1) {
        if (model.value.options) {
          if (model.value.options.length < 1) {
            window.$message.warning(i18n.t('custom_field.option_check'))
            return
          }
          for (const item of model.value.options) {
            if (!item.label || !item.value) {
              window.$message.warning(i18n.t('custom_field.option_value_check'))
              return
            }
          }
        }
      }
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
const open = (_title: string, row?: ICustomField): void => {
  title.value = _title
  if (row) {
    model.value = Object.assign({}, row)
  } else {
    model.value = {
      name: '',
      type: 'input',
      scene: 'TEST_CASE',
      remark: '',
      system: false,
      options: [],
      projectId: getCurrentProjectId(),
    }

    if (isTemplateEdit.value) {
      model.value.scene = props.scene
    }
  }
  modalDialog.value?.show()
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="title" dialog-width="600px" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('custom_field.field_name')" path="name">
            <!-- <n-input v-if="isSystem" :value="$t(systemNameMap[model.name])" :disabled="isSystem" /> -->
            <n-input v-model:value="model.name" :disabled="isSystem" />
          </n-form-item>
          <n-form-item :label="$t('custom_field.field_remark')" path="remark">
            <n-input
              v-model:value="model.remark"
              type="textarea"
              :autosize="{
                minRows: 3,
                maxRows: 5,
              }"
              maxlength="255"
              show-count
            />
          </n-form-item>
          <n-form-item :label="$t('custom_field.scene')" path="type">
            <n-select
              v-model:value="model.scene"
              :options="sceneOptions"
              :disabled="isSystem || isTemplateEdit"
              filterable
              :render-label="renderSceneLabel"
              :placeholder="$t('custom_field.scene')"
            />
          </n-form-item>
          <n-form-item :label="$t('custom_field.field_type')" path="type">
            <n-select
              v-model:value="model.type"
              :options="fieldTypeOptions"
              :disabled="isSystem"
              filterable
              :render-label="renderTypeLabel"
              :placeholder="$t('custom_field.field_type')"
              @update:value="handleUpdateTypeValue"
            />
          </n-form-item>
          <n-form-item v-if="showOptions" :label="$t('custom_field.field_option')" path="options">
            <n-single-handle-drag :is-kv="model.scene === 'ISSUE'" :data="model.options" />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
