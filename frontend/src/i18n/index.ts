import { createI18n } from 'vue-i18n'
import pinia from '../store'
import en_US from './locales/en_US'
import zh_CN from './locales/zh_CN'
import track_en from '/@/i18n/modules/track/en_US.ts'
import track_cn from '/@/i18n/modules/track/zh_CN.ts'
import { useSettingStore } from '/@/store/modules/setting-store'

const getLocale = (): string => {
  const store = useSettingStore(pinia)
  return store.language || 'zh-cn'
}
const instance = createI18n({
  // something vue-i18n options here ...
  legacy: false,
  globalInjection: true,
  messages: {
    ['zh-cn']: {
      ...zh_CN,
      ...track_cn
    },
    ['en']: {
      ...en_US,
      ...track_en
    },
  },
  locale: getLocale(), // set locale
  fallbackLocale: 'en', // set fallback locale
})

export const i18n = instance.global
export default instance
