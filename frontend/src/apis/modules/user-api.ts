import { IPageResponse, IQueryParam } from '../interface'
import { IGroup, IGroupResourceDto, IUserGroup } from './group-api'
import alovaInstance from '/@/plugins/alova'

export interface IUser {
  id: string
  name: string
  email: string
  phone: string
  lastProjectId: string
  projectId?: string
  lastWorkspaceId: string
  workspaceId?: string
  status: boolean
  password?: string
  groups: Array<IGroup>
  roleIds?: Array<string>
  groupIds?: Array<string>
}
export interface IUserDto extends IUser {
  userGroups?: Array<IUserGroup>
  groups: Array<IGroup>
  groupPermissions?: Array<IGroupResourceDto>
}
/**
 * 查询workspace关联的用户
 * @param page 当前页码
 * @param pageSize 每页数据条数
 * @param params 查询条件
 * @returns
 */
export const getWorkspaceMemberPageSpecial = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IUser>>('/user/special/ws/member/list', params)
}
export const getProjectMemberPages = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IUser>>(`/user/ws/project/member/list/${params.workspaceId}`, params)
}
export const getUserListByResourceUrl = (url: string) => alovaInstance.Get<Array<IUser>>(url)
/**
 * 工作空间下添加用户
 * @param param 参数
 * @returns
 */
export const addWorkspaceMemberSpecial = (param: { userIds: Array<string>; groupIds: Array<string> }) =>
  alovaInstance.Post(`/user/special/ws/member/add`, param)

export const specialListUsers = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return alovaInstance.Post<IPageResponse<IUser>>('/user/special/page', params)
}

export const specialCreateUser = (user: IUser) => alovaInstance.Post(`/user/special/save`, user)
export const specialModifyUser = (user: IUser) => alovaInstance.Post(`/user/special/update`, user)

export const specialModifyPassword = (param: { confirmPassword: string; newPassword: string; id: string }) =>
  alovaInstance.Post(`/user/special/password`, param)
