import { TreeOption } from 'naive-ui'
import { IPageResponse, IQueryParam } from '../interface'
import http from '/@/plugins/alova'
/**
 * 节点类型
 */
export interface ITestCaseNode extends TreeOption {
  id: string
  name: string
  level: number
  parentId: string
  pos: number
  children: ITestCaseNode[]
  // 扩展字段
  expand?: boolean
  label?: string
  caseNum?: number
  parent?: ITestCaseNode
}
export interface ITestCase {
  id: string
  name: string
}
/**
 * 查询用例模块节点列表数据
 * @param projectId
 * @param params
 * @returns
 */
export const getTestCaseNodesByCaseFilter = (projectId: string, params?: IQueryParam) =>
  http.Post<Array<ITestCaseNode>>(`/case/node/list/${projectId}`, params)
/**
 * 列表数据
 * @param page
 * @param pageSize
 * @param params
 * @returns
 */
export const getTestCaseNodePage = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return http.Post<IPageResponse<ITestCaseNode>>('/case/node/page', params)
}

export const testCaseNodeAdd = (param: ITestCaseNode) => http.Post('/case/node/save', param)
export const testCaseNodeEdit = (param: ITestCaseNode) => http.Post('/case/node/update', param)

/**
 * 查询测试用例列表数据
 * @param param
 * @returns
 */
export const testCaseList = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return http.Post<IPageResponse<ITestCase>>('/test/case/page', params)
}
