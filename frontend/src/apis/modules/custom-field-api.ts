import { SelectOption } from 'naive-ui'
import { IPageResponse, IQueryParam } from '../interface'
import http from '/@/plugins/alova'
export interface ICustomFieldOption extends SelectOption {}
export interface ICustomField {
  id: string
  name: string
  scene: string
  type: string
  system: boolean
  remark: string
  projectId: string
  options?: Array<SelectOption>
}

export const getCustomFieldPages = (page: number, pageSize: number, params: IQueryParam) => {
  params.pageNumber = page
  params.pageSize = pageSize
  return http.Post<IPageResponse<ICustomField>>('/custom/field/page', params)
}
