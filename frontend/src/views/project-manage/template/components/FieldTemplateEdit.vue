<script setup lang="ts">
import { useRequest } from 'alova'
import { FormInst, FormRules, NDrawer, NDrawerContent } from 'naive-ui'
import { computed, ref } from 'vue'
import CustomFieldFormList from './CustomFieldFormList.vue'
import TemplateComponentEditHeader from './ext/TemplateComponentEditHeader.vue'
import { ICustomField } from '/@/apis/modules/custom-field-api'
import {
  ICustomFieldTemplateDao,
  getCustomFieldDefaultTemplates,
  getCustomFieldTemplates,
} from '/@/apis/modules/template-api'
import { ITestCaseTemplate } from '/@/apis/modules/test-case-template-api'
import NFormDivider from '/@/components/NFormDivider.vue'
import { i18n } from '/@/i18n'
import { getCurrentProjectId } from '/@/utils/token'

interface IProps {
  form?: ITestCaseTemplate
  rules?: FormRules
  scene: string
  url: string
}
const props = withDefaults(defineProps<IProps>(), {
  form: () => ({}) as ITestCaseTemplate,
  rules: () => ({}),
})
const formRef = ref<FormInst | null>(null)
const relateFields = ref<Array<ICustomFieldTemplateDao | ICustomField>>([])
const templateContainIds = ref<Array<string>>([])
const emit = defineEmits(['update:form'])
const model = computed({
  get: () => props.form,
  set: (val) => {
    emit('update:form', val)
  },
})

const show = ref(false)
const isSystem = computed(() => {
  return props.form.system
})
const { send: loadCustomFieldTemplate } = useRequest((param) => getCustomFieldTemplates(param), { immediate: false })
const { send: loadDefaultCustomFieldTemplate } = useRequest((param) => getCustomFieldDefaultTemplates(param), {
  immediate: false,
})
const appendDefaultFiled = () => {
  let condition = {
    projectId: getCurrentProjectId(),
    scene: props.scene,
  }
  loadDefaultCustomFieldTemplate(condition).then((res) => {
    const data = res
    data.forEach((item) => {
      if (item.name === '用例状态' && item.system && item.scene === 'TEST_CASE') {
        if (item.options) {
          item.options = item.options.map((option) => {
            return {
              ...option,
              label: i18n.t(option.label as string),
              value: option.value,
            }
          })
        }
      }

      if (item.name === '用例等级' && item.system && item.scene === 'TEST_CASE') {
        item.required = true
        item.disabled = true
      }
      if (item.id) {
        templateContainIds.value.push(item.id)
      }
      item.fieldId = item.id
      item.id = ''
      if (item.type === 'checkbox') {
        item.defaultValue = []
      }
    })
    relateFields.value.push(...data)
  })
}
const getRelateFields = () => {
  let condition = { templateId: '' }
  condition.templateId = props.form.id
  if (props.form.id) {
    loadCustomFieldTemplate(condition).then((res) => {
      relateFields.value = res
      relateFields.value.forEach((item) => {
        if (item.name === '用例等级' && item.system) {
          item.disabled = true
        }
        if (item.type === 'int' && item.defaultValue === null) {
          // el-input-number 需要设置成 undefined，默认值才能设置为空
          item.defaultValue = undefined
        }
        templateContainIds.value.push(item.fieldId as string)
      })
    })
  } else {
    appendDefaultFiled()
  }
}
const open = () => {
  show.value = true
  getRelateFields()
}
const handleSave = () => {
  formRef.value?.validate((err) => {
    if (!err) {
      console.log(`output->model.value`, model.value)
    }
  })
}
const handleClose = () => (show.value = false)
const relateField = () => {}
const addField = () => {}
defineExpose({ open })
</script>
<template>
  <n-drawer v-model:show="show" width="100%">
    <n-drawer-content :native-scrollbar="false">
      <template #header>
        <template-component-edit-header :template="form" @cancel="handleClose" @save="handleSave" />
      </template>
      <n-form-divider :title="$t('test_track.plan_view.base_info')" />
      <n-form
        ref="formRef"
        :model="model"
        :rules="rules"
        label-placement="left"
        require-mark-placement="right-hanging"
        label-width="auto"
      >
        <n-form-item :label="$t('commons.template_name')" path="name">
          <n-input v-model:value="model.name" :disabled="isSystem" />
        </n-form-item>
        <slot name="base"></slot>
        <n-form-item :label="$t('commons.description')" path="description">
          <n-input v-model:value="model.description" type="textarea" />
        </n-form-item>
        <n-form-divider :title="$t('custom_field.template_setting')" />
        <slot></slot>
        <n-form-item :label="$t('table.selected_fields')">
          <n-button type="primary" :disabled="true" @click="relateField">{{ $t('custom_field.add_field') }}</n-button>
          <n-button type="primary" :disabled="true" @click="addField">
            {{ $t('custom_field.custom_field_setting') }}
          </n-button>
        </n-form-item>
        <n-form-item>
          <custom-field-form-list
            :table-data="relateFields"
            :scene="scene"
            :template-contain-ids="templateContainIds"
          />
        </n-form-item>
      </n-form>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped></style>
