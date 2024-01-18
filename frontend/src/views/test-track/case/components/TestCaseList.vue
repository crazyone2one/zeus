<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { DataTableColumns, DataTableRowKey } from 'naive-ui'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { ITestCase, testCaseList } from '/@/apis/modules/test-case-api'
import NPagination from '/@/components/NPagination.vue'
import NSearch from '/@/components/NSearch.vue'
import NTableCountBar from '/@/components/table/NTableCountBar.vue'
import NVersionSelect from '/@/components/version/NVersionSelect.vue'
import { i18n } from '/@/i18n'
import { getCurrentProjectId, setCurrentProjectID } from '/@/utils/token'

interface IProps {
  defaultVersion: string | null
}
withDefaults(defineProps<IProps>(), {})
const route = useRoute()
const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const projectId = ref('')
const checkedRowKeysRef = ref<DataTableRowKey[]>([])
const columns: DataTableColumns<ITestCase> = [
  {
    type: 'selection',
  },
  {
    title: i18n.t('test_track.case.name'),
    key: 'name',
    minWidth: 120,
  },
  {
    title: i18n.t('test_track.case.case_desc'),
    key: 'desc',
    minWidth: 100,
  },
  {
    title: i18n.t('test_track.case.status'),
    key: 'reviewStatus',
    minWidth: '140px',
  },
  {
    title: i18n.t('test_track.plan_view.execute_result'),
    key: 'status',
    minWidth: '120px',
  },
  {
    title: i18n.t('commons.tag'),
    key: 'tags',
    minWidth: 180,
  },
  {
    title: i18n.t('test_track.case.module'),
    key: 'nodePath',
    minWidth: '150px',
  },

  {
    title: i18n.t('commons.operating'),
    key: 'operating',
    fixed: 'right',
    width: 200,
    align: 'center',
  },
]
const routeProjectId = computed(() => {
  return route.query.projectId
})
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
  (page, pageSize) => testCaseList(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<ITestCase>) => response.totalRow,
    data: (response: IPageResponse<ITestCase>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const rowKey = (row: ITestCase) => row.id

const handleCheck = (rowKeys: DataTableRowKey[]) => (checkedRowKeysRef.value = rowKeys)
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handleList = () => loadTableData()
// const handleCreate = () => {}
const checkCurrentProject = () => {
  if (routeProjectId.value) {
    if (getCurrentProjectId() !== routeProjectId.value) {
      setCurrentProjectID(routeProjectId.value as string)
      location.reload()
      return
    } else {
      projectId.value = routeProjectId.value as string
    }
  } else {
    projectId.value = getCurrentProjectId()
  }
}
onMounted(() => {
  checkCurrentProject()
})
</script>
<template>
  <div>
    <n-spin :show="loading">
      <n-card>
        <template #header>
          <div class="case-main-layout-left" style="float: left; display: inline-block">
            <n-table-count-bar :count-content="$t('case.all_case_content') + '(' + (total ?? 0) + ')'" />
          </div>
          <div class="case-main-layout-right" style="float: right; display: flex">
            <n-search style="float: left" @search="handleList" />
            <n-version-select :default-version="defaultVersion" :project-id="projectId" />
          </div>
        </template>
        <n-data-table :columns="columns" :data="data" :row-key="rowKey" @update:checked-row-keys="handleCheck" />
        <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
      </n-card>
    </n-spin>
  </div>
</template>
<style scoped></style>
