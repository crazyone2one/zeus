<script setup lang="ts">
import { useRequest } from 'alova'
import { NSplit, TreeOption } from 'naive-ui'
import { computed, onMounted, reactive, ref } from 'vue'
import TestPlanTree from '../../project-manage/node/components/TestPlanTree.vue'
import TestPlanList from './components/TestPlanList.vue'
import { IQueryParam } from '/@/apis/interface'
import { ITestPlanNode, getTestPlanNodes } from '/@/apis/modules/test-plan-node-api'
import { getCurrentProjectId } from '/@/utils/token'
import { buildTree } from '/@/utils/tree'

const condition = reactive<IQueryParam>({
  name: '',
  pageNumber: 1,
  pageSize: 5,
})
const treeData = ref<TreeOption[]>([])
const { send: sendTreeData, loading: treeLoading } = useRequest((pId) => getTestPlanNodes(pId), { immediate: false })
const projectId = computed(() => {
  return getCurrentProjectId()
})
const parallelRequest = async () => {
  condition.projectId = projectId.value
  const [treeResponse] = await Promise.all([sendTreeData(projectId.value)])
  // 并行请求完成，继续处理业务...
  // buildTestPlanNodeTree(treeResponse, treeData.value)
  treeData.value = buildTree(treeResponse)
}
const handleCaseNodeSelect = (node: ITestPlanNode, nodeIds: string[], pNodes: Array<ITestPlanNode>) => {
  console.log(`output->node`, node)
  console.log(`output->pNodes`, pNodes)
  condition.nodeIds = nodeIds
  // loadTableData()
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
      <test-plan-list />
    </template>
  </n-split>
</template>

<style scoped></style>
