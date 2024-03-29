import { defineStore } from 'pinia'
export const useSettingStore = defineStore('settings', {
  state: () => {
    return {
      isCollapsed: true,
      language: 'zh-cn',
    }
  },
  actions: {
    setIsCollapsed(isCollapsed: boolean) {
      this.isCollapsed = isCollapsed
    },
    setLanguage(language: string) {
      this.language = language
    },
  },
  persist: true,
})
