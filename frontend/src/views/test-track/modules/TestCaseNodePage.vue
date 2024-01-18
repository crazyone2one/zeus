<script setup lang="ts">
import { usePagination } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { DataTableColumns, NSplit, TreeOption } from 'naive-ui'
import { computed, h, onMounted, reactive, ref } from 'vue'
import AddNode from '../../project-manage/node/components/AddNode.vue'
import { IPageResponse, IQueryParam } from '/@/apis/interface'
import { ITestCaseNode, getTestCaseNodePage, getTestCaseNodesByCaseFilter } from '/@/apis/modules/test-case-api'
import { ITestPlanNode } from '/@/apis/modules/test-plan-node-api'
import NPagination from '/@/components/NPagination.vue'
import NTableHeader from '/@/components/NTableHeader.vue'
import NTableOperator from '/@/components/NTableOperator.vue'
import NTableOperatorButton from '/@/components/NTableOperatorButton.vue'
import { i18n } from '/@/i18n'
import { getCurrentProjectId } from '/@/utils/token'
import TestPlanTree from '/@/views/project-manage/node/components/NNodeTree.vue'

const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const addNode = ref<InstanceType<typeof AddNode> | null>(null)
const { send: sendTreeData, loading: treeLoading } = useRequest((pId) => getTestCaseNodesByCaseFilter(pId), {
  immediate: false,
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
  (page, pageSize) => getTestCaseNodePage(page, pageSize, condition),
  {
    // 请求前的初始数据（接口返回的数据格式）
    initialData: {
      total: 0,
      data: [],
    },
    total: (response: IPageResponse<ITestCaseNode>) => response.totalRow,
    data: (response: IPageResponse<ITestCaseNode>) => response.records,
    // watchingStates: [condition],
    debounce: 300, // 防抖参数，单位为毫秒数，也可以设置为数组对watchingStates单独设置防抖时间
    immediate: false,
  },
)
const treeData = ref<TreeOption[]>([])
const columns: DataTableColumns<ITestCaseNode> = [
  { title: '名称', key: 'name' },
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
          showEdit: false,
          deletePermission: ['SYSTEM_USER:READ+DELETE'],
        },
        {
          front: () => {
            return h(NTableOperatorButton, {
              tip: i18n.t('test_track.module.add_submodule'),
              onClick: () => handleCreateSub(row),
            })
          },
        },
      )
    },
  },
]
const buildTree = (data: ITestPlanNode[]) => {
  if (data) {
    data.forEach((item) => {
      item.key = item.id
      item.label = item.name
      // if (item.parent) {
      //   item.parent = item.parent
      // }
      if (item.children && item.children.length > 0) {
        buildTree(item.children)
      }
    })
  }
  return data
}
const parallelRequest = async () => {
  condition.projectId = projectId.value
  const [treeResponse] = await Promise.all([sendTreeData(projectId.value), loadTableData()])
  // 并行请求完成，继续处理业务...
  // buildTestPlanNodeTree(treeResponse, treeData.value)
  treeData.value = buildTree(treeResponse)
}
const projectId = computed(() => {
  return getCurrentProjectId()
})
const handlePrevPage = (val: number) => {
  pageSize.value = val
}
const handleList = () => {
  condition.projectId = projectId.value
  loadTableData()
}
const handleCreate = () => addNode.value?.open()
const handleCreateSub = (val: ITestPlanNode) => addNode.value?.open(val)
const handleCaseNodeSelect = (node: ITestPlanNode, nodeIds: string[], pNodes: Array<ITestPlanNode>) => {
  console.log(`output->node`, node)
  console.log(`output->pNodes`, pNodes)
  condition.nodeIds = nodeIds
  loadTableData()
}
onMounted(() => {
  parallelRequest()
})
</script>

<template>
  <n-split direction="horizontal" :default-size="0.15">
    <template #1>
      <test-plan-tree :data="treeData" :loading="treeLoading" @node-select-event="handleCaseNodeSelect" />
    </template>
    <template #2>
      <n-spin :show="loading">
        <n-card>
          <template #header>
            <n-table-header
              :condition="condition"
              :create-permission="['SYSTEM_USER:READ+CREATE']"
              @search="handleList"
              @create="handleCreate"
            />
          </template>
          <n-data-table :row-key="(row: ITestPlanNode) => row.id" :columns="columns" :data="data" />
          <n-pagination :total="total" :page-size="pageSize" :page="page" @update:page-size="handlePrevPage" />
        </n-card>
      </n-spin>
    </template>
  </n-split>
  <add-node ref="addNode" type="case" @refresh="parallelRequest" />
</template>

<style scoped></style>
