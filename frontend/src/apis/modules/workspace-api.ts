import { IPageResponse, IQueryParam } from '../interface'
import { IUserDto } from './user-api'
import alovaInstance from '/@/plugins/alova'

export interface IWorkspace {
  id: string
  name: string
  description: string
  memberSize?: number
}
export interface IMember {
  id: string
  name: string
  email: string
  phone: string
  groupIds: Array<string>
  workspaceId: string
}
/**
 * workspace列表查询
 * @param page 当前页码
 * @param pageSize 每页数据条数
 * @param params 查询条件
 * @returns
 */
export const queryWsPage = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IWorkspace>>('/workspace/page', params)
}
export const getUserWorkspaceList = () => {
  return alovaInstance.Get<Array<IWorkspace>>('/workspace/list/user/workspace')
}

export const createWs = (params: IWorkspace) => {
  return alovaInstance.Post<IWorkspace>('/workspace/special/save', params)
}
/**
 * update workspace
 * @param params workspace
 * @returns
 */
export const updateWorkspaceSpecial = (params: IWorkspace) => {
  return alovaInstance.Put(`/workspace/special/update`, params)
}
export const switchWorkspace = (wsId: string) => alovaInstance.Get<IUserDto>(`/user/switch/source/ws/${wsId}`)
export const getWorkspaces = () => alovaInstance.Get<Array<IWorkspace>>('/workspace/list')
/**
 * 更新workspace下用户信息
 * @param param user info param
 * @returns
 */
export const updateWorkspaceMember = (param: IMember) => alovaInstance.Post(`/workspace/member/update`, param)
