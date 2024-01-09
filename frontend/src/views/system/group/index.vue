<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
import EditUserGroup from './components/EditUserGroup.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { IGroup, getUserGroupPages } from '/@/apis/modules/group-api'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import NTableOperatorButton from '/@/components/NTableOperatorButton.vue'
import { i18n } from '/@/i18n'
import { getCurrentWorkspaceId } from '/@/utils/token'

const editUserGroup = ref<InstanceType<typeof EditUserGroup> | null>(null)
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const columns: DataTableColumns<IGroup> = [
  {
    type: 'selection',
  },
  {
    title: i18n.t('commons.name'),
    key: 'name',
    maxWidth: 200,
  },
  {
    title: i18n.t('group.type'),
    key: 'type',
  },
  {
    title: i18n.t('commons.member'),
    key: 'memberSize',
    align: 'center',
  },
  {
    title: i18n.t('group.scope'),
    key: 'scopeName',
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
        {
          behind: () => {
            return h(
              NTableOperatorButton,
              {
                tip: i18n.t('member.edit_password'),
                icon: 'i-carbon:password',
                type: 'success',
                permission: ['SYSTEM_USER:READ+EDIT_PASSWORD'],
              },
              {},
            )
          },
        },
      )
    },
  },
]
const rowKey = (row: IGroup) => row.id
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
  (page, pageSize) => getUserGroupPages(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<IGroup>) => response.totalRow,
    data: (response: IPageResponse<IGroup>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const handleList = () => {
  loadTableData()
}
const handleCreate = () => {
  editUserGroup.value?.open('create', i18n.t('group.create'))
}
const handleEdit = (row: IGroup) => {
  if (row.id === 'admin') {
    window.$message.warning(i18n.t('group.admin_not_allow_edit'))
    return
  }
  editUserGroup.value?.open('edit', i18n.t('group.edit'), row)
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
          :create-tip="$t('group.create')"
          :create-permission="['SYSTEM_GROUP:READ+CREATE']"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <edit-user-group ref="editUserGroup" @refresh="handleList" />
</template>

<style scoped></style>
