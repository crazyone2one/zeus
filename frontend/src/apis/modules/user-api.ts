import { IGroup, IGroupResourceDto, IUserGroup } from "./group-api";

export interface IUser {
  id: string;
  name: string;
  email: string;
  phone: string;
  lastProjectId: string;
  lastWorkspaceId: string;
  status: boolean;
  password?: string;
  groups: Array<IGroup>;
}
export interface IUserDto extends IUser {
  userGroups?: Array<IUserGroup>;
  groups: Array<IGroup>;
  groupPermissions?: Array<IGroupResourceDto>;
}
