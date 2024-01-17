<script setup lang="ts">
import { useRequest } from 'alova'
import { TreeOption } from 'naive-ui'
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { IQueryParam } from '/@/apis/interface'
import { getTestCaseNodesByCaseFilter } from '/@/apis/modules/test-case-api'
import { ITestPlanNode } from '/@/apis/modules/test-plan-node-api'
import { getCurrentProjectId } from '/@/utils/token'
import { buildTree } from '/@/utils/tree'
import NNodeTree from '/@/views/project-manage/node/components/NNodeTree.vue'

const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const treeData = ref<TreeOption[]>([])
const { send: sendTreeData, loading: treeLoading } = useRequest(
  (pId, param) => getTestCaseNodesByCaseFilter(pId, param),
  { immediate: false },
)
const projectId = computed(() => {
  return getCurrentProjectId()
})
const parallelRequest = async () => {
  if (projectId.value && projectId.value !== '') {
    condition.projectId = projectId.value
    condition.casePublic = false
    const [treeResponse] = await Promise.all([sendTreeData(projectId.value, condition)])
    treeData.value = buildTree(treeResponse)
  }
}
const handleCaseNodeSelect = (node: ITestPlanNode, nodeIds: string[], pNodes: Array<ITestPlanNode>) => {
  console.log(`output->node`, node)
  console.log(`output->pNodes`, pNodes)
  condition.nodeIds = nodeIds
  // loadTableData()
}
onMounted(() => {
  nextTick(() => {
    parallelRequest()
  })
})
</script>
<template>
  <div>
    <slot name="header"></slot>
    <n-node-tree :data="treeData" :loading="treeLoading" @node-select-event="handleCaseNodeSelect" />
  </div>
</template>

<style scoped></style>
