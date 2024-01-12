import { ICustomField } from './custom-field-api'
import http from '/@/plugins/alova'

export const saveFieldTemplate = (params: ICustomField) => http.Post(`custom/field/save`, params)
export const modifyFieldTemplate = (params: ICustomField) => http.Put(`custom/field/update`, params)
