import { TreeSelectOption } from 'naive-ui'
import { h } from 'vue'
import { ITestPlanNode } from '../apis/modules/test-plan-node-api'

/**
 * 递归构建树形结构
 * @param nodes
 * @param options
 */
export const buildTestPlanNodeTree = (nodes: Array<ITestPlanNode>, options: Array<TreeSelectOption>) => {
  nodes.forEach((item) => {
    const node: TreeSelectOption = {}
    node.label = item.label
    node.key = item.id
    if (node.children) {
      console.log(`output->node.children`, node.children)
      buildTestPlanNodeTree(item.children, node.children)
    }
    options.push(node)
  })
}

export const buildTree = (data: ITestPlanNode[]) => {
  if (data) {
    data.forEach((item) => {
      item.key = item.id
      item.label = item.name
      item.suffix = () => h('span', {}, { default: () => item.caseNum })
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
