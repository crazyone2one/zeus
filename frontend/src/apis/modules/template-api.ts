import { SelectOption } from 'naive-ui'
import { IQueryParam } from '../interface'
import { ICustomField } from './custom-field-api'
import http from '/@/plugins/alova'

interface ICustomFieldTemplate {
  id: string
  fieldId: string
  templateId: string
  scene: string
  key: string
  defaultValue: string
  required: boolean
  order: number
  customData: string
}
export interface ICustomFieldTemplateDao extends ICustomFieldTemplate {
  name: string
  scene: string
  type: string
  remark: string
  options: Array<SelectOption>
  system: boolean
  disabled?: boolean
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [key: string]: any
}

export const saveFieldTemplate = (params: ICustomField) => http.Post(`custom/field/save`, params)
export const modifyFieldTemplate = (params: ICustomField) => http.Put(`custom/field/update`, params)

export const getCustomFieldTemplates = (params: ICustomField) =>
  http.Post<Array<ICustomFieldTemplateDao>>(`/custom/field/template/list`, params)

export const getCustomFieldDefaultTemplates = (params: IQueryParam) =>
  http.Post<Array<ICustomField>>(`/custom/field/default`, params)
