<script setup lang="ts">
import { useRequest } from 'alova'
import { TreeOption } from 'naive-ui'
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { IQueryParam } from '/@/apis/interface'
import { ITestCaseNode, getTestCaseNodesByCaseFilter } from '/@/apis/modules/test-case-api'
import { ITestPlanNode } from '/@/apis/modules/test-plan-node-api'
import { i18n } from '/@/i18n'
import { useUserStore } from '/@/store/modules/user-store'
import { buildNodePath, buildTree } from '/@/utils/node-tree'
import { getCurrentProjectId } from '/@/utils/token'
import NNodeTree from '/@/views/project-manage/node/components/NNodeTree.vue'

const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const route = useRoute()
const currentNode = ref<ITestCaseNode>({} as ITestCaseNode)
const userStore = useUserStore()
const treeNodes = ref<TreeOption[]>([])
const { send: sendTreeData, loading: treeLoading } = useRequest(
  (pId, param) => getTestCaseNodesByCaseFilter(pId, param),
  { immediate: false },
)
const projectId = computed(() => {
  return getCurrentProjectId()
})
const routeModuleId = computed(() => {
  return route.query.moduleId
})
const parallelRequest = async () => {
  if (projectId.value && projectId.value !== '') {
    condition.projectId = projectId.value
    condition.casePublic = false
    const [treeResponse] = await Promise.all([sendTreeData(projectId.value, condition)])
    treeNodes.value = treeResponse
    treeNodes.value.forEach((node) => {
      node.name = node.name === '未规划用例' ? i18n.t('api_test.unplanned_case') : node.name
      buildTree(node as ITestCaseNode, { path: '' })
    })
    setModuleOptions()
  }
}
const setModuleOptions = () => {
  let moduleOptions: Array<TreeOption> = []
  treeNodes.value.forEach((node) => {
    buildNodePath(node as ITestCaseNode, { path: '' }, moduleOptions)
  })
  userStore.testCaseModuleOptions = moduleOptions
}
const handleNodeChange = (node: ITestPlanNode, nodeIds: string[], pNodes: Array<ITestPlanNode>) => {
  userStore.testCaseSelectNode = node
  userStore.testCaseSelectNodeIds = nodeIds
  condition.trashEnable = false
  condition.publicEnable = false
  currentNode.value = node
  // condition.nodeIds = nodeIds
  // loadTableData()
}
onMounted(() => {
  nextTick(() => {
    parallelRequest()
  })
  userStore.testCaseSelectNode = {} as ITestCaseNode
  userStore.testCaseSelectNodeIds = []
})
</script>
<template>
  <div>
    <slot name="header"></slot>
    <n-node-tree :data="treeNodes" :loading="treeLoading" @node-select-event="handleNodeChange" />
  </div>
</template>

<style scoped></style>
