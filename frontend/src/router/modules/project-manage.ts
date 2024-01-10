import { RouteRecordRaw } from 'vue-router'
const Project: Array<RouteRecordRaw> = [
  {
    path: '/project',
    name: 'Project',
    redirect: '/project/home',
    component: () => import(`/@/views/project-manage/index.vue`),
    children: [
      {
        path: 'home',
        component: () => import(`/@/views/project-manage/home/index.vue`),
      },
    ],
  },
]
export default Project
