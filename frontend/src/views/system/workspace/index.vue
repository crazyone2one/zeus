<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
import EditWorkspace from './components/EditWorkspace.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { IWorkspace, queryWsPage } from '/@/apis/modules/workspace-api'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import { i18n } from '/@/i18n'
import { getCurrentWorkspaceId } from '/@/utils/token'

const editWorkspace = ref<InstanceType<typeof EditWorkspace> | null>(null)
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const columns: DataTableColumns<IWorkspace> = [
  {
    type: 'selection',
  },
  {
    title: i18n.t('commons.name'),
    key: 'name',
    minWidth: 100,
  },
  {
    title: i18n.t('commons.description'),
    key: 'description',
    minWidth: 100,
  },
  {
    title: i18n.t('commons.member'),
    key: 'memberSize',
    width: 150,
    align: 'center',
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
          editPermission: ['SYSTEM_USER:READ+EDIT'],
          deletePermission: ['SYSTEM_USER:READ+DELETE'],
          showDelete: workspaceId.value !== row.id,
        },
        {},
      )
    },
  },
]
const rowKey = (row: IWorkspace) => row.id
const workspaceId = computed(() => {
  return getCurrentWorkspaceId()
})
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
  (page, pageSize) => queryWsPage(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<IWorkspace>) => response.totalRow,
    data: (response: IPageResponse<IWorkspace>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const handleList = () => {
  loadTableData()
}
const handleCreate = () => {
  editWorkspace.value?.open()
}
const handleEdit = (val: IWorkspace) => {
  editWorkspace.value?.open(val)
}
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
onMounted(() => {
  loadTableData()
})
</script>
<template>
  <n-spin :show="loading">
    <n-card>
      <template #header>
        <n-table-header
          :condition="condition"
          :create-tip="$t('workspace.create')"
          :create-permission="['SYSTEM_WORKSPACE:READ+CREATE']"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <edit-workspace ref="editWorkspace" @refresh="handleList" />
</template>

<style scoped></style>
