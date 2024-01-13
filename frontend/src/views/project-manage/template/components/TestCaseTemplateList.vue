<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { h, onMounted, reactive, ref } from 'vue'
import TestCaseTemplateEdit from './TestCaseTemplateEdit.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { ITestCaseTemplate, getCaseFieldTemplatePages } from '/@/apis/modules/test-case-template-api'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import { i18n } from '/@/i18n'

const testCaseTemplateEdit = ref<InstanceType<typeof TestCaseTemplateEdit> | null>(null)
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const columns: DataTableColumns<ITestCaseTemplate> = [
  {
    type: 'selection',
  },
  {
    title: i18n.t('commons.name'),
    key: 'name',
    ellipsis: {
      tooltip: true,
    },
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => (row.system ? i18n.t('custom_field.default_template') : row.name),
        },
      )
    },
  },
  {
    title: i18n.t('api_test.home_page.failed_case_list.table_coloum.case_type'),
    key: 'type',
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => caseTypeMap[row.type],
        },
      )
    },
  },

  {
    title: i18n.t('custom_field.system_template'),
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
    title: i18n.t('commons.description'),
    key: 'description',
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

const caseTypeMap: { [key: string]: string } = {
  functional: i18n.t('api_test.home_page.failed_case_list.table_value.case_type.functional'),
}

const rowKey = (row: ITestCaseTemplate) => row.id
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
  (page, pageSize) => getCaseFieldTemplatePages(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<ITestCaseTemplate>) => response.totalRow,
    data: (response: IPageResponse<ITestCaseTemplate>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const handleList = () => {
  loadTableData()
}
const handleCreate = () => {
  testCaseTemplateEdit.value?.open()
}
const handleEdit = (row: ITestCaseTemplate) => {
  testCaseTemplateEdit.value?.open(row)
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
          :create-tip="$t('custom_field.template_create')"
          :create-permission="[]"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <test-case-template-edit ref="testCaseTemplateEdit" />
</template>

<style scoped></style>
