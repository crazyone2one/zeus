<script setup lang="ts">
import { FormRules } from 'naive-ui'
import { reactive, ref } from 'vue'
import FieldTemplateEdit from './FieldTemplateEdit.vue'
import { ITestCaseTemplate } from '/@/apis/modules/test-case-template-api'
import { i18n } from '/@/i18n'

const url = ref('')
const fieldTemplateEdit = ref<InstanceType<typeof FieldTemplateEdit> | null>(null)
let form = reactive<ITestCaseTemplate>({
  id: '',
  name: '',
  type: 'functional',
  description: '',
  caseName: '',
  prerequisite: '',
  stepDescription: '',
  expectedResult: '',
  actualResult: '',
  customFieldIds: [],
  stepModel: 'STEP',
  steps: [],
  global: false,
  system: false,
})
const rules: FormRules = {
  name: [
    { required: true, message: i18n.t('test_track.case.input_name'), trigger: 'blur' },
    { max: 64, message: i18n.t('test_track.length_less_than') + '64', trigger: 'blur' },
  ],
  type: [{ required: true, trigger: 'change' }],
}
const open = (data?: ITestCaseTemplate, isCopy?: boolean) => {
  if (data) {
    Object.assign(form, data)
    url.value = isCopy ? 'field/template/case/add' : 'field/template/case/update'
  } else {
    form = {
      id: '',
      name: '',
      type: 'functional',
      description: '',
      caseName: '',
      prerequisite: '',
      stepDescription: '',
      expectedResult: '',
      actualResult: '',
      customFieldIds: [],
      steps: [],
      stepModel: 'STEP',
      global: false,
      system: false,
    }
    url.value = 'field/template/case/add'
  }
  fieldTemplateEdit.value?.open()
}
defineExpose({ open })
</script>
<template>
  <field-template-edit ref="fieldTemplateEdit" :form="form" :rules="rules" scene="TEST_CASE" :url="url">
  </field-template-edit>
</template>

<style scoped></style>
