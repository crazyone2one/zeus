import mitt from 'mitt'
import 'virtual:uno.css'
import { createApp } from 'vue'
import App from './App.vue'
import permission from './directive/permission'
import i18n from './i18n'
import naive from './plugins/naive'
import router from './router'
import pinia from './store'
import './style.css'

const emitter = mitt()
const app = createApp(App)
app.use(pinia)
app.use(i18n)
app.use(router).use(naive)
app.directive('permission', permission)
app.config.globalProperties.emitter = emitter
app.mount('#app')
