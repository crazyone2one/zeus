import { createServerTokenAuthentication } from '@alova/scene-vue'
import { createAlova } from 'alova'
import GlobalFetch from 'alova/GlobalFetch'
import VueHook from 'alova/vue'
import router from '../router'
import { useAuthStore } from '../store/modules/auth-store'
import { getCurrentProjectId, getCurrentWorkspaceId } from '../utils/token'
import { refreshUserToken } from '/@/apis/modules/auth-api'

const logOnDev = (message: string) => {
  if (import.meta.env.MODE === 'development') {
    console.debug(message)
  }
}
const { onAuthRequired, onResponseRefreshToken } = createServerTokenAuthentication({
  // ...
  assignToken: (method) => {
    if (!method.meta?.ignoreToken) {
      // 接口无需验证
      // https://alova.js.org/zh-CN/tutorial/getting-started/method-metadata#%E5%9C%A8%E8%AF%B7%E6%B1%82%E5%89%8D%E4%BD%BF%E7%94%A8%E8%BA%AB%E4%BB%BD%E6%A0%87%E8%AF%86
      const { accessToken } = useAuthStore()
      method.config.headers.Authorization = `Bearer ${accessToken}`
    }
    method.config.headers.WORKSPACE = getCurrentWorkspaceId()
    method.config.headers.PROJECT = getCurrentProjectId()
  },
  refreshTokenOnSuccess: {
    // 响应时触发，可获取到response和method，并返回boolean表示token是否过期
    // 当服务端返回401时，表示token过期
    isExpired: (response) => {
      return response.status === 401
    },
    // 当token过期时触发，在此函数中触发刷新token
    handler: async () => {
      const param = { refreshToken: useAuthStore().refreshToken }
      const { accessToken, refreshToken } = await refreshUserToken(param)
      useAuthStore().accessToken = accessToken
      useAuthStore().refreshToken = refreshToken
    },
  },
})
const alovaInstance = createAlova({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  // 请求超时时间，单位为毫秒，默认为0，表示永不超时
  timeout: 50000,
  statesHook: VueHook,
  requestAdapter: GlobalFetch(),
  beforeRequest: onAuthRequired(() => {}),
  responded: onResponseRefreshToken({
    onSuccess: async (response, method) => {
      //...响应成功拦截器
      // 当使用GlobalFetch请求适配器时，第一个参数接收Response对象
      // 第二个参数为当前请求的method实例，你可以用它同步请求前后的配置信息
      const json = await response.json()
      // refresh token 接口返回401表示token过期，需要重新登录
      if (response.status === 401 && method.url.concat('api/auth/refresh')) {
        window.$dialog.warning({
          title: '警告',
          content: '认证失败，重新登录',
          positiveText: '确定',
          negativeText: '不确定',
          onPositiveClick: () => {
            useAuthStore().resetAuth()
            const route = router.currentRoute
            router.push(
              `/login?redirect=${route.value.path}&params=${JSON.stringify(
                route.value.query ? route.value.query : route.value.params,
              )}`,
            )
          },
          onNegativeClick: () => {
            window.$message.error('不确定')
          },
        })
      }
      if (response.status === 403) {
        window.$message.warning(json.message)
      }
      if (response.status !== 200) throw new Error(json.message)
      if (!json.success) {
        // 抛出错误或返回reject状态的Promise实例时，此请求将抛出错误
        throw new Error(json.message)
      }
      // 解析的响应数据将传给method实例的transformData钩子函数，这些函数将在后续讲解
      // https://alova.js.org/zh-CN/tutorial/getting-started/method-metadata#%E5%9C%A8%E5%93%8D%E5%BA%94%E5%90%8E%E4%BD%BF%E7%94%A8%E6%A0%87%E8%AF%86%E8%BA%AB%E4%BB%BD
      return method.meta?.isDownload ? response.blob() : json.data
    },
    onError: (error, method) => {
      //...响应错误拦截器
      console.log(`output->error`, error)
      logOnDev(method.baseURL)
      alert(error.message)
    },
    // onComplete: (method) => {
    //   //...原响应完成拦截器
    // },
  }),
})
export default alovaInstance
