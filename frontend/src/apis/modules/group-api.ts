import { SelectOption } from 'naive-ui'
import { IProject } from './project-api'
import { IWorkspace } from './workspace-api'
import alovaInstance from '/@/plugins/alova'

export interface IUserGroup {
  id: string
  userId: string
  groupId: string
  sourceId: string
  userGroupPermissions?: Array<IUserGroupPermission>
  group?: IGroup
}
export interface IGroup {
  id: string
  name: string
  description: string
  system: boolean
  type: string
  scopeId: string
  global: boolean
  selects?: Array<string> // 选择项
  ids?: Array<string> // ids
  workspaceOptions?: Array<SelectOption> // 工作空间选项
  workspaces?: Array<IWorkspace> // 工作空间选项
  projectOptions?: Array<SelectOption>
  projects?: Array<IProject>
  showSearchGetMore?: boolean
}
export interface IGroupResource {
  id: string
  name: string
}
export interface IGroupPermission {
  id: string
  name: string
  resourceId: string
  checked: boolean
}
export interface IUserGroupPermission {
  id: string
  groupId: string
  permissionId: string
  moduleId: string
}
export interface IGroupResourceDto {
  group: IGroup
  userGroupPermissions: Array<IUserGroupPermission>
  type: string
  permissions: Array<IGroupPermission>
  resource: IGroupResource
}
interface IGroupPermissionDTO {
  permissions: Array<IGroupResourceDto>
}

export const getUserGroupList = (param: { type: string; resourceId: string; projectId: string }) =>
  alovaInstance.Post<Array<IGroup>>(`/user/group/list`, param)

export const getUserAllGroups = (userId: string) => alovaInstance.Get<Array<IGroup>>(`/user/group/all/${userId}`)
export const getAllUserGroupByType = (param: { type: string }) =>
  alovaInstance.Post<Array<IGroup>>(`/user/group/get`, param)
