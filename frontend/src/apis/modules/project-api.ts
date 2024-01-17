import { IPageResponse, IQueryParam } from '../interface'
import { IUser, IUserDto } from './user-api'
import http from '/@/plugins/alova'

export interface IProject extends Record<string, unknown> {
  id: string
  name: string
  description: string
  workspaceId: string
  apiTemplateId?: string
  caseTemplateId?: string
  issueTemplateId?: string
  memberSize?: number
  createUser?: string
  createTime?: string
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
  return http.Post<IPageResponse<IProject>>('/project/page', params)
}
export const saveProject = (params: IProject) => {
  return http.Post<IProject>('/project/save', params)
}
export const modifyProject = (params: IProject) => {
  return http.Put('/project/update', params)
}
export const delProjectById = (params: string) => {
  return http.Delete(`/project/remove/${params}`)
}
export const getProjectList = () => http.Get<Array<IProject>>(`/project/list`)

export const getUserProjectList = (param: { userId: string; workspaceId: string }) =>
  http.Post<Array<IProject>>(`/project/list/related`, param)

export const switchProject = (param: { id: string; lastProjectId: string }) =>
  http.Post<IUserDto>(`/user/update/current`, param)

export const getProjectDetail = (id: string) => http.Get<IProject>(`/project/getInfo/${id}`)
export const getProjectMemberSize = (id: string) => http.Get<number>(`/project/member/size/${id}`)

// 项目管理模块相关接口
export const getProjectMembers = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return http.Post<IPageResponse<IProject>>('/project/member/list/', params)
}
/**
 * 修改项目下成员信息
 * @param param
 * @returns
 */
export const modifyProjectMember = (param: IUser) => http.Post(`/project/member/update`, param)
export const delProjectMember = (projectId: string, userId: string) =>
  http.Get(`/project/member/delete/${projectId}/${userId}`)
interface IAddMemberRequest {
  workspaceId?: string
  projectId?: string
  userIds: Array<string>
  groupIds: Array<string>
}
export const addProjectMember = (param: IAddMemberRequest) => http.Post(`/project/member/add`, param)
