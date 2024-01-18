import { TreeOption } from 'naive-ui'
import { ITestCaseNode } from '../apis/modules/test-case-api'

export const buildTree = (node: ITestCaseNode, option: TreeOption) => {
  option.key = node.id
  option.label = node.name
  option.path = option.path + '/' + node.name
  node.path = option.path
  if (node.children) {
    for (let i = 0; i < node.children.length; i++) {
      buildTree(node.children[i], { path: option.path })
    }
  }
}
export function buildNodePath(node: ITestCaseNode, option: TreeOption, moduleOptions: Array<TreeOption>) {
  option.id = node.id
  option.path = option.path + '/' + node.name
  moduleOptions.push(option)
  if (node.children) {
    for (let i = 0; i < node.children.length; i++) {
      buildNodePath(node.children[i], { path: option.path }, moduleOptions)
    }
  }
}
