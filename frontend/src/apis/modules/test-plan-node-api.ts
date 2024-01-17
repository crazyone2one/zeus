import { TreeOption } from 'naive-ui'
import { IPageResponse, IQueryParam } from '../interface'
import http from '/@/plugins/alova'

export interface ITestPlanNode extends TreeOption {
  id: string
  name: string
  level: number
  parentId: string
  pos: number
  children: ITestPlanNode[]
  // 扩展字段
  expand?: boolean
  label?: string
  caseNum?: number
  parent?: ITestPlanNode
}

export const getTestPlanNodes = (projectId: string, params?: IQueryParam) =>
  http.Post<Array<ITestPlanNode>>(`/plan/node/list/${projectId}`, params)
/**
 * 列表数据
 * @param page
 * @param pageSize
 * @param params
 * @returns
 */
export const getTestPlanNodePage = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return http.Post<IPageResponse<ITestPlanNode>>('/plan/node/page', params)
}
/**
 * 保存方法
 * @param param
 * @returns
 */
export const savePlanNode = (param: ITestPlanNode) => http.Post('/plan/node/save', param)
export const editPlanNode = (param: ITestPlanNode) => http.Post('/plan/node/update', param)
