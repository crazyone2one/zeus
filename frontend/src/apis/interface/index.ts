// mybatis-flex分页查询返回类型
export interface IPageResponse<T> {
  empty?: boolean
  pageNumber: number
  pageSize: number
  totalPage: number
  totalRow: number
  records: Array<T>
}
/**
 * 查询参数类型
 */
export interface IQueryParam {
  name: string
  pageNumber: number
  pageSize: number
  ids?: Array<string>
  projectId?: string
  workspaceId?: string
  filters?: {
    scene?: string[]
    type?: string[]
    status?: string[]
    state?: string[]
  }
  [key: string]: string | number | Array<number> | unknown | []
}
export interface IQueryMemberRequest {
  name: string
  workspaceId: string
  projectId: string
}
