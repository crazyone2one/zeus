import { IPageResponse, IQueryParam } from '../interface'
import alovaInstance from '/@/plugins/alova'

export interface IProject {
  id: string
  name: string
  description: string
  workspaceId: string
  apiTemplateId?: string
  caseTemplateId?: string
  issueTemplateId?: string
  memberSize?: number
}
/**
 * 列表数据查询
 * @param page
 * @param pageSize
 * @param params
 * @returns
 */
export const getProjectPages = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IProject>>('/project/page', params)
}
export const saveProject = (params: IProject) => {
  return alovaInstance.Post<IProject>('/project/save', params)
}
export const modifyProject = (params: IProject) => {
  return alovaInstance.Put('/project/update', params)
}

export const getProjectList = () => alovaInstance.Get<Array<IProject>>(`/project/list`)

export const getUserProjectList = (param: { userId: string; workspaceId: string }) =>
  alovaInstance.Post<Array<IProject>>(`/project/list/related`, param)
