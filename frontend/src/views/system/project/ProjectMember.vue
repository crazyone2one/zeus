<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { h, reactive, ref } from 'vue'
import ModifyMember from './ModifyMember.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { IProject, addProjectMember, delProjectMember } from '/@/apis/modules/project-api'
import { IUser, getProjectMemberPages } from '/@/apis/modules/user-api'
import { IWorkspace } from '/@/apis/modules/workspace-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import NPagination from '/@/components/NPagination.vue'
import NRolesTag from '/@/components/NRolesTag.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import { i18n } from '/@/i18n'
import AddMember from '/@/views/system/workspace/components/AddMember.vue'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const addMember = ref<InstanceType<typeof AddMember> | null>(null)
const modifyMember = ref<InstanceType<typeof ModifyMember> | null>(null)
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const columns: DataTableColumns<IUser> = [
  {
    type: 'selection',
    // disabled (row: RowData) {
    //   return row.name === 'Edward King 3'
    // }
  },
  {
    title: i18n.t('commons.username'),
    key: 'name',
  },

  {
    title: i18n.t('commons.email'),
    key: 'email',
  },
  {
    title: i18n.t('commons.phone'),
    key: 'phone',
  },
  {
    title: i18n.t('commons.group'),
    key: 'groups',
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
      return h(
        NTableOperator,
        {
          onEditClick: () => handleEdit(row),
          onDeleteClick: () => handleDeleteMember(row),
          tip2: i18n.t('commons.remove'),
          editPermission: ['WORKSPACE_PROJECT_MANAGER:READ+EDIT_USER'],
          deletePermission: ['WORKSPACE_PROJECT_MANAGER:READ+DELETE_USER'],
        },
        {},
      )
    },
  },
]
const groupScopeId = ref('')
const currentWorkspaceRow = ref<IWorkspace>({} as IWorkspace)
const rowKey = (row: IUser) => row.id
const checkedRowKeysRef = ref<DataTableRowKey[]>([])
const handleCheck = (rowKeys: DataTableRowKey[]) => (checkedRowKeysRef.value = rowKeys)
// const emit = defineEmits(['refresh'])
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
  (page, pageSize) => getProjectMemberPages(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<IUser>) => response.totalRow,
    data: (response: IPageResponse<IUser>) => response.records,
    initialPage: 1, // 初始页码，默认为1
    initialPageSize: 10, // 初始每页数据条数，默认为10
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handleList = () => {
  loadTableData()
}
const emit = defineEmits(['refresh'])
const open = (row: IProject): void => {
  condition.name = ''
  condition.workspaceId = row.workspaceId
  condition.projectId = row.id
  groupScopeId.value = row.id
  currentWorkspaceRow.value = row
  loadTableData()
  modalDialog.value?.show()
}
const handleAddMember = () => {
  addMember.value?.open()
}
/**
 * 编辑用户信息
 * @param row 用户信息
 */
const handleEdit = (row: IUser) => {
  modifyMember.value?.open(row)
}
const { send } = useRequest((param) => addProjectMember(param), { immediate: false })
const _addMember = (param: { userIds: Array<string>; groupIds: Array<string>; projectId?: string }) => {
  param.projectId = currentWorkspaceRow.value.id
  send(param).then(() => {
    loadTableData()
    addMember.value?.close()
  })
}
/**
 * 移除用户
 * @param row 用户信息
 */
const { send: deleteMember, loading: deleteMemberLoad } = useRequest((pId, uId) => delProjectMember(pId, uId), {
  immediate: false,
})
const handleDeleteMember = (row: IUser) => {
  window.$dialog.warning({
    title: '警告',
    content: i18n.t('member.remove_member'),
    positiveText: i18n.t('commons.confirm'),
    negativeText: i18n.t('commons.cancel'),
    onPositiveClick: () => {
      deleteMember(currentWorkspaceRow.value.id, row.id).then(() => {
        window.$message.success(i18n.t('commons.remove_success'))
        handleList()
        emit('refresh')
      })
    },
    onNegativeClick: () => {
      window.$message.info(i18n.t('commons.remove_cancel'))
    },
  })
}
const handleClose = () => {
  modalDialog.value?.toggleModal()
  emit('refresh')
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="loading || deleteMemberLoad">
    <n-modal-dialog ref="modalDialog" :title="$t('commons.member')" dialog-width="70%" @confirm="handleClose">
      <template #content>
        <n-table-header
          :condition="condition"
          :create-tip="$t('member.create')"
          :create-permission="['WORKSPACE_PROJECT_MANAGER:READ+ADD_USER']"
          @search="handleList"
          @create="handleAddMember"
        />
        <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
        <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
      </template>
    </n-modal-dialog>
  </n-spin>
  <add-member ref="addMember" :group-type="'PROJECT'" :group-scope-id="groupScopeId" @submit="_addMember" />
  <modify-member ref="modifyMember" @refresh="handleList" />
</template>

<style scoped></style>
