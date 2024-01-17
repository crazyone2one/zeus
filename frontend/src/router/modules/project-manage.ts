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
        name: 'projectHome',
        component: () => import(`/@/views/project-manage/home/index.vue`),
      },
      {
        path: 'member',
        name: 'projectMember',
        component: () => import(`/@/views/project-manage/member/index.vue`),
      },
      {
        path: 'usergroup',
        name: 'projectUserGroup',
        component: () => import(`/@/views/project-manage/group/index.vue`),
      },
      {
        path: 'template',
        name: 'projectTemplate',
        component: () => import(`/@/views/project-manage/template/index.vue`),
      },
      { path: 'node', name: 'projectNode', component: () => import('/@/views/project-manage/node/index.vue') },
      {
        path: 'testCaseNode',
        name: 'testCaseNode',
        component: () => import(`/@/views/test-track/modules/TestCaseNodePage.vue`),
      },
    ],
  },
]
export default Project
