<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
import CustomFieldEdit from './CustomFieldEdit.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { ICustomField, getCustomFieldPages } from '/@/apis/modules/custom-field-api'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import { i18n } from '/@/i18n'
import { FIELD_TYPE_MAP, SCENE_MAP, SYSTEM_FIELD_NAME_MAP } from '/@/utils/table-constants'

const customFieldEditRef = ref<InstanceType<typeof CustomFieldEdit> | null>(null)
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const columns: DataTableColumns<ICustomField> = [
  {
    type: 'selection',
  },
  {
    title: i18n.t('custom_field.field_name'),
    key: 'name',
    ellipsis: {
      tooltip: true,
    },
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => (row.system ? i18n.t(systemNameMap.value[row.name]) : row.name),
        },
      )
    },
  },
  {
    title: i18n.t('custom_field.scene'),
    key: 'scene',
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => i18n.t(sceneMap.value[row.scene]),
        },
      )
    },
  },
  {
    title: i18n.t('custom_field.field_type'),
    key: 'type',
    align: 'center',
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => i18n.t(fieldTypeMap.value[row.type]),
        },
      )
    },
  },
  {
    title: i18n.t('custom_field.system_field'),
    key: 'system',
    align: 'center',
    render(rowData) {
      return h(
        'span',
        {},
        {
          default: () => {
            return rowData.system ? i18n.t('commons.yes') : i18n.t('commons.no')
          },
        },
      )
    },
  },
  {
    title: i18n.t('custom_field.field_remark'),
    key: 'remark',
    ellipsis: {
      tooltip: true,
    },
  },
  {
    title: i18n.t('commons.operating'),
    key: 'operating',
    fixed: 'right',
    width: 200,
    align: 'center',
    render(row) {
      return h(
        NTableOperator,
        {
          onEditClick: () => handleEdit(row),
        },
        {},
      )
    },
  },
]

const systemNameMap = computed(() => {
  return SYSTEM_FIELD_NAME_MAP
})
const sceneMap = computed(() => {
  return SCENE_MAP
})
const fieldTypeMap = computed(() => {
  return FIELD_TYPE_MAP
})
const rowKey = (row: ICustomField) => row.id
const checkedRowKeysRef = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => (checkedRowKeysRef.value = rowKeys)

const {
  // 加载状态
  loading,
  data,
  page,
  pageSize,
  total,
  send: loadTableData,
} = usePagination(
  // Method实例获取函数，它将接收page和pageSize，并返回一个Method实例
  (page, pageSize) => getCustomFieldPages(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<ICustomField>) => response.totalRow,
    data: (response: IPageResponse<ICustomField>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const handleList = () => {
  loadTableData()
}
const handleCreate = () => {
  customFieldEditRef.value?.open(i18n.t('custom_field.create'))
}
const handleEdit = (row: ICustomField) => {
  customFieldEditRef.value?.open(i18n.t('custom_field.update'), row)
}

const handlePrevPage = (val: number) => {
  pageSize.value = val
}

onMounted(() => {
  handleList()
})
</script>
<template>
  <n-spin :show="loading">
    <n-card>
      <template #header>
        <n-table-header
          :condition="condition"
          :create-tip="$t('custom_field.create')"
          :create-permission="[]"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <custom-field-edit ref="customFieldEditRef" @refresh="handleList" />
</template>

<style scoped></style>
