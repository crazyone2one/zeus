<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, h, reactive, ref } from 'vue'
import MemberDialog from './MemberDialog.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { IGroup, getUserGroupByResourceUrlAndPage } from '/@/apis/modules/group-api'
import { IUser } from '/@/apis/modules/user-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import { i18n } from '/@/i18n'
import { GROUP_SYSTEM } from '/@/utils/constants'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const memberDialog = ref<InstanceType<typeof MemberDialog> | null>(null)
const group = ref<IGroup>({} as IGroup)
const title = ref('')
const submitType = ref('')

// const typeLabel = computed(() => {
//   let type = group.value.type
//   if (type === GROUP_WORKSPACE) {
//     return i18n.t('group.belong_workspace')
//   }
//   if (type === GROUP_PROJECT) {
//     return i18n.t('group.belong_project')
//   }
//   return ''
// })
const showTypeLabel = computed(() => {
  return group.value.type !== GROUP_SYSTEM
})
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
  // {
  //   title: typeLabel.value,
  //   key: 'sourceIds',
  //   cellProps: () => {
  //     console.log(`output->showTypeLabel.value`, showTypeLabel.value)
  //     return { hidden: !showTypeLabel.value }
  //   },
  // },
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
          onDeleteClick: () => handleDelete(row),
          showEdit: showTypeLabel.value,
          tip2: i18n.t('commons.remove'),
          editPermission: ['SYSTEM_GROUP:READ+EDIT'],
          deletePermission: ['SYSTEM_GROUP:READ+EDIT'],
        },
        {},
      )
    },
  },
]
const emit = defineEmits(['refresh'])
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
  (page, pageSize) => getUserGroupByResourceUrlAndPage(page, pageSize, condition),
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
  condition.userGroupId = group.value.id
  loadTableData()
}
const open = (row: IGroup): void => {
  group.value = row
  handleList()
  modalDialog.value?.show()
}

const handleAddMember = () => {
  // addMember.value?.open()
  title.value = i18n.t('member.create')
  submitType.value = 'Add'
  memberDialog.value?.open()
}
/**
 * 编辑用户信息
 * @param row 用户信息
 */
const handleEdit = (row: IUser) => {
  // editWorkspaceMember.value?.open(row)
}
/**
 * 移除用户
 * @param row 用户信息
 */
const handleDelete = (row: IUser) => {
  window.$message.info(row.name)
}

const handleClose = () => {
  modalDialog.value?.toggleModal()
  emit('refresh')
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="loading">
    <n-modal-dialog ref="modalDialog" :title="$t('commons.member')" dialog-width="70%" @confirm="handleClose">
      <template #content>
        <n-table-header
          :condition="condition"
          :create-tip="$t('member.create')"
          :create-permission="['SYSTEM_GROUP:READ+EDIT']"
          @search="handleList"
          @create="handleAddMember"
        />
        <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
        <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
      </template>
    </n-modal-dialog>
  </n-spin>
  <member-dialog ref="memberDialog" :group="group" />
</template>

<style scoped></style>
