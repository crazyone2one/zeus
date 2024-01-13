<script setup lang="ts">
import { computed } from 'vue'
import { ICustomField } from '../apis/modules/custom-field-api'
import { ICustomFieldTemplateDao } from '../apis/modules/template-api'

interface IProps {
  formProp?: string
  isTemplateEdit: boolean
  data: ICustomField | ICustomFieldTemplateDao
  prop: string
  disabled?: boolean
}
const props = withDefaults(defineProps<IProps>(), { formProp: 'name', disabled: false })
const emits = defineEmits(['update:data'])
const _data = computed({
  get: () => props.data,
  set: (val) => {
    emits('update:data', val)
  },
})
</script>
<template>
  <span>
    <n-select
      v-if="data.type === 'select' || data.type === 'multipleSelect'"
      v-model:value="_data[prop]"
      :options="_data.options"
      :disabled="disabled"
      :multiple="data.type === 'multipleSelect'"
      clearable
      filterable
      :placeholder="$t('commons.default')"
    />
    <n-input v-else v-model:value="_data[prop]" maxlength="450" show-count clearable :disabled="disabled" />
  </span>
</template>

<style scoped></style>
