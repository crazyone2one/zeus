<script setup lang="ts">
import { NTree, TreeOption, TreeOverrideNodeClickBehavior } from 'naive-ui'
import { ref } from 'vue'
import { ITestPlanNode } from '/@/apis/modules/test-plan-node-api'
interface IProp {
  loading?: boolean
  data?: Array<TreeOption>
}
withDefaults(defineProps<IProp>(), {
  loading: false,
  data: () => [],
})
const pattern = ref('')
const showIrrelevantNodes = ref(false)
// const currentNode = ref<ITestPlanNode>({} as ITestPlanNode)
const emits = defineEmits(['nodeSelectEvent'])
const override: TreeOverrideNodeClickBehavior = ({ option }) => {
  if (option.children) {
    return 'toggleExpand'
  }
  return 'default'
}
const getChildNodeId = (rootNode: ITestPlanNode, nodeIds: Array<string>) => {
  nodeIds.push(rootNode.id)
  if (rootNode.children) {
    rootNode.children.forEach((childNode) => {
      getChildNodeId(childNode, nodeIds)
    })
  }
}
const getParentNodes = (rootNode: ITestPlanNode, pNodes: Array<ITestPlanNode>) => {
  if (rootNode.parent && rootNode.parent.id !== '0') {
    getParentNodes(rootNode.parent, pNodes)
  }
  if (rootNode.name && rootNode.name !== '') {
    pNodes.push(rootNode)
  }
}
const nodeProps = ({ option }: { option: ITestPlanNode | TreeOption }) => {
  return {
    onClick() {
      let nodeIds: Array<string> = []
      let pNodes: Array<ITestPlanNode> = []
      getChildNodeId(option as ITestPlanNode, nodeIds)
      getParentNodes(option as ITestPlanNode, pNodes)
      // emits('nodeSelectEvent', option, option.id !== 'root' ? [] : nodeIds, pNodes)
      emits('nodeSelectEvent', option, nodeIds, pNodes)
      // console.log(`output->nodeIds`, nodeIds)
      // console.log(`output->node`, option)
      // console.log(`output->pNodes`, pNodes)
    },
  }
}
</script>
<template>
  <div>
    <slot name="header"></slot>
    <n-spin :show="loading">
      <n-space vertical :size="12">
        <n-input v-model:value="pattern" placeholder="搜索" clearable />
        <n-switch v-model:value="showIrrelevantNodes">
          <template #checked> 展示搜索无关的节点 </template>
          <template #unchecked> 隐藏搜索无关的节点 </template>
        </n-switch>
        <n-tree
          block-line
          :data="data"
          checkable
          :override-default-node-click-behavior="override"
          :node-props="nodeProps"
          :pattern="pattern"
          :show-irrelevant-nodes="showIrrelevantNodes"
        />
      </n-space>
    </n-spin>
    <slot name="bottom" />
  </div>
</template>
<style scoped></style>
