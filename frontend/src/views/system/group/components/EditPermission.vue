<script setup lang="ts">
import { useRequest } from 'alova'
import { DataTableColumns, NCheckbox } from 'naive-ui'
import { computed, h, ref } from 'vue'
import GroupPermission from './GroupPermission.vue'
import {
  IGroup,
  IGroupPermission,
  IGroupResource,
  IGroupResourceDto,
  getUserGroupPermission,
} from '/@/apis/modules/group-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { GROUP_TYPE } from '/@/utils/constants'
import { PROJECT_GROUP_SCOPE, USER_GROUP_SCOPE } from '/@/utils/table-constants'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const title = ref(i18n.t('group.set_permission'))
const readOnly = ref(false)
const group = ref<IGroup>({} as IGroup)
const userGroupType = computed(() => {
  return USER_GROUP_SCOPE
})
const columns: DataTableColumns<IGroupResourceDto> = [
  {
    title: i18n.t('permission.common.first_level_menu'),
    key: 'type',
    width: 180,
    render(row) {
      return h(
        'span',
        {},
        {
          default: () => {
            if (row.type !== GROUP_TYPE.PROJECT) {
              return userGroupType.value[row.type] ? i18n.t(userGroupType.value[row.type]) : i18n.t(row.type)
            }
            return _computedMenuName(row.resource)
          },
        },
      )
    },
  },
  {
    title: i18n.t('permission.common.second_level_menu'),
    key: 'resource',
    width: 180,
    render(rowData) {
      return h('span', {}, i18n.t(rowData.resource.name))
    },
  },
  {
    title: i18n.t('group.permission'),
    key: 'permissions',
    render(row) {
      return h(GroupPermission, { permissions: row.permissions, readOnly: readOnly.value, group: group.value }, {})
    },
  },
  {
    title: i18n.t('group.check_all'),
    width: '60px',
    key: 'check',
    align: 'center',
    render(rowData) {
      return h(
        NCheckbox,
        { disabled: isReadOnly(rowData), onUpdateChecked: (event) => handleSelectAll(event, rowData.permissions) },
        {},
      )
    },
  },
]
const tableData = ref<IGroupResourceDto[]>([])
const { loading, send: loadGroupJson } = useRequest((model) => getUserGroupPermission(model), {
  immediate: false,
})
const handleSelectAll = (val: boolean, permissions: Array<IGroupPermission>) => {
  permissions.map((p) => (p.checked = val))
}
const handleSave = () => {
  let param: { userGroupId: string; permissions: Array<IGroupPermission> } = { userGroupId: '', permissions: [] }
  let permissions: Array<IGroupPermission> = []
  tableData.value.forEach((td) => {
    permissions.push(...td.permissions)
  })
  param.userGroupId = group.value.id
  param.permissions = permissions
  console.log(`output->model.value`, param)
}
const getGroupJson = () => {
  loadGroupJson(group.value).then((res) => (tableData.value = res.permissions))
}
const open = (row: IGroup, _title?: string, _readOnly?: boolean): void => {
  title.value = _title || i18n.t('group.set_permission')
  readOnly.value = _readOnly || false
  group.value = Object.assign({}, row)
  getGroupJson()
  modalDialog.value?.show()
}
const _computedMenuName = (resource: IGroupResource) => {
  return i18n.t(PROJECT_GROUP_SCOPE[resource.id.split('_')[1]])
    ? i18n.t(PROJECT_GROUP_SCOPE[resource.id.split('_')[1]])
    : i18n.t('permission.other.project')
}
const isReadOnly = (data: IGroupResourceDto) => {
  if (group.value.id === 'super_group') {
    return true
  }
  const isDefaultSystemGroup = group.value.id === 'admin' && data.resource.id === 'SYSTEM_GROUP'
  return readOnly.value || isDefaultSystemGroup
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="loading">
    <n-modal-dialog ref="modalDialog" :title="title" dialog-width="70%" @confirm="handleSave">
      <template #content>
        <n-data-table :columns="columns" :data="tableData" />
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped>
.dialog {
  width: 65%;
}
</style>
