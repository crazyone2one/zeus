<script setup lang="ts">
import { DataTableColumns, NCheckbox } from 'naive-ui'
import { computed, h } from 'vue'
import { ICustomField } from '/@/apis/modules/custom-field-api'
import { ICustomFieldTemplateDao } from '/@/apis/modules/template-api'
import CustomFiledComponent from '/@/components/CustomFiledComponent.vue'
import { i18n } from '/@/i18n'
import { SYSTEM_FIELD_NAME_MAP } from '/@/utils/table-constants'

interface IProps {
  tableData?: Array<ICustomFieldTemplateDao | ICustomField>
  scene: string
  platform?: string
  templateContainIds: string[]
}
const props = withDefaults(defineProps<IProps>(), {
  tableData: () => [],
  platform: '',
})
const emits = defineEmits(['update:tableData'])
const data = computed({
  get: () => props.tableData,
  set: (val) => {
    emits('update:tableData', val)
  },
})

const columns: DataTableColumns<ICustomFieldTemplateDao> = [
  {
    title: i18n.t('commons.name'),
    key: 'name',
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => {
            return row.system ? i18n.t(systemNameMap.value[row.name]) : row.name
          },
        },
      )
    },
  },
  {
    title: i18n.t('commons.default'),
    key: 'type',
    minWidth: 200,
    render(rowData) {
      return h(CustomFiledComponent, { data: rowData, isTemplateEdit: true, prop: 'defaultValue' })
    },
  },
  {
    title: i18n.t('api_test.definition.document.table_coloum.is_required'),
    key: 'type',
    width: 80,
    render(row) {
      return h(NCheckbox, { disabled: row.disabled, checked: row.required }, {})
    },
  },
  {
    title: i18n.t('custom_field.system_field'),
    key: 'system',
    width: 80,
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => {
            return row.system ? i18n.t('commons.yes') : i18n.t('commons.no')
          },
        },
      )
    },
  },
  {
    title: i18n.t('commons.remark'),
    key: 'remark',
  },
]
const systemNameMap = computed(() => {
  return SYSTEM_FIELD_NAME_MAP
})
</script>

<template>
  <n-data-table :key="(row: ICustomFieldTemplateDao) => row.fieldId" :columns="columns" :data="data" />
</template>

<style scoped></style>
