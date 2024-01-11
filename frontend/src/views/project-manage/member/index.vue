<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
// import EditUser from './components/EditUser.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { getProjectMembers } from '/@/apis/modules/project-api'
import { IUser } from '/@/apis/modules/user-api'
import NPagination from '/@/components/NPagination.vue'
import NRolesTag from '/@/components/NRolesTag.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import { i18n } from '/@/i18n'
import { getCurrentProjectId } from '/@/utils/token'

// const editUser = ref<InstanceType<typeof EditUser> | null>(null)
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const columns: DataTableColumns<IUser> = [
  {
    type: 'selection',
  },
  {
    title: i18n.t('commons.username'),
    key: 'name',
  },
  {
    title: i18n.t('commons.email'),
    key: 'email',
    align: 'center',
  },
  {
    title: i18n.t('commons.phone'),
    key: 'phone',
    align: 'center',
  },
  {
    title: i18n.t('commons.group'),
    key: 'description',
    render(rowData) {
      return h(NRolesTag, { roles: rowData.groups })
    },
  },
  {
    title: i18n.t('commons.operating'),
    key: 'operating',
    fixed: 'right',
    width: 200,
    align: 'center',
    render(row) {
      return h(NTableOperator, {
        onEditClick: () => handleEdit(row),
        editPermission: ['PROJECT_USER:READ+EDIT'],
        deletePermission: ['PROJECT_USER:READ+DELETE'],
      })
    },
  },
]
const rowKey = (row: IUser) => row.id
const projectId = computed(() => {
  return getCurrentProjectId()
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
  (page, pageSize) => getProjectMembers(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<IUser>) => response.totalRow,
    data: (response: IPageResponse<IUser>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const handleList = () => {
  condition.projectId = projectId.value
  loadTableData()
}
const handleCreate = () => {
  // editUser.value?.open('Add', i18n.t('user.create'))
}
const handleEdit = (val: IUser) => {
  // editUser.value?.open('Edit', i18n.t('user.modify'), val)
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
          :create-tip="$t('member.create')"
          :create-permission="['PROJECT_USER:READ+CREATE']"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <!-- <edit-user ref="editUser" @refresh="loadTableData" /> -->
</template>

<style scoped></style>
