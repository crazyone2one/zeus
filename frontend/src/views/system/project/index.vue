<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey, NButton } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EditProject from './EditProject.vue'
import ProjectMember from './ProjectMember.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { IProject, getProjectPages } from '/@/apis/modules/project-api'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import { i18n } from '/@/i18n'
import { getCurrentWorkspaceId } from '/@/utils/token'

const route = useRoute()
const router = useRouter()
const editProject = ref<InstanceType<typeof EditProject> | null>(null)
const projectMember = ref<InstanceType<typeof ProjectMember> | null>(null)
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const columns: DataTableColumns<IProject> = [
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
    render(row) {
      return h(NButton, { text: true, onClick: () => handleCellClick(row) }, { default: () => row.memberSize })
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
          editPermission: ['SYSTEM_USER:READ+EDIT'],
          deletePermission: ['SYSTEM_USER:READ+DELETE'],
          showDelete: workspaceId.value !== row.id,
        },
        {},
      )
    },
  },
]
const rowKey = (row: IProject) => row.id
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
  (page, pageSize) => getProjectPages(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<IProject>) => response.totalRow,
    data: (response: IPageResponse<IProject>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const handleList = () => {
  condition.workspaceId = getCurrentWorkspaceId()
  loadTableData()
}
const handleCreate = () => {
  let workspaceId = getCurrentWorkspaceId()
  if (!workspaceId) {
    window.$message.warning(i18n.t('project.please_choose_workspace'))
    return false
  }
  editProject.value?.open()
}
const handleEdit = (val: IProject) => {
  editProject.value?.open(val)
}
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handleCellClick = (val: IProject) => {
  projectMember.value?.open(val)
}
onMounted(() => {
  if (route.path.split('/')[1] === 'project' && route.path.split('/')[2] === 'create') {
    router.replace({ path: '/project/all' })
    setTimeout(() => {
      handleCreate()
    }, 200)
  }
  handleList()
})
</script>
<template>
  <n-spin :show="loading">
    <n-card>
      <template #header>
        <n-table-header
          :condition="condition"
          :create-tip="$t('project.create')"
          :create-permission="['WORKSPACE_PROJECT_MANAGER:READ+CREATE']"
          @search="handleList"
          @create="handleCreate"
        />
      </template>
      <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
      <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
    </n-card>
  </n-spin>
  <edit-project ref="editProject" @refresh="handleList" />
  <project-member ref="projectMember" />
</template>

<style scoped></style>
