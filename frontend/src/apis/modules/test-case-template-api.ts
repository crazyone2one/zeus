import { IPageResponse, IQueryParam } from '../interface'
import http from '/@/plugins/alova'

export interface ITestCaseStep {}
export interface ITestCaseTemplate {
  id: string
  name: string
  type: string
  description: string
  caseName: string
  system: boolean
  global: boolean
  prerequisite?: string
  stepDescription?: string
  expectedResult?: string
  actualResult?: string
  projectId?: string
  steps?: Array<ITestCaseStep>
  stepModel?: string
  customFieldIds?: Array<string>
  [key: string]: string | boolean | ITestCaseStep[] | undefined
}

/**
 * 列表数据查询
 * @param page
 * @param pageSize
 * @param params
 * @returns
 */
export const getCaseFieldTemplatePages = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return http.Post<IPageResponse<ITestCaseTemplate>>('/field/template/case/page', params)
}
