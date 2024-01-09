import { RouteRecordRaw } from 'vue-router'
const Setting: Array<RouteRecordRaw> = [
  {
    path: '/setting/user',
    name: 'user',
    component: () => import(`/@/views/system/user/index.vue`),
    meta: {
      title: 'commons.user',
      permissions: ['SYSTEM_USER:READ'],
      system: true,
    },
  },
  {
    path: '/setting/workspace',
    name: 'workspace',
    component: () => import(`/@/views/system/workspace/index.vue`),
    meta: {
      title: 'commons.workspace',
      permissions: ['SYSTEM_WORKSPACE:READ'],
      system: true,
    },
  },
  //   {
  //     path: "/organization",
  //     name: "organization",
  //     component: () => import(`/@/views/setting/organization/index.vue`),
  //     meta: { title: "organization", requiresAuth: true },
  //   },
  {
    path: '/setting/usergroup',
    name: 'usergroup',
    component: () => import(`/@/views/system/group/index.vue`),
    meta: {
      title: 'group.group_permission',
      permissions: ['SYSTEM_GROUP:READ'],
      system: true,
    },
  },

  {
    path: '/project/:type',
    name: 'project',
    component: () => import(`/@/views/system/project/index.vue`),
    meta: {
      title: 'project.manager',
      permissions: ['WORKSPACE_PROJECT_MANAGER:READ'],
      workspace: true,
    },
  },
]
export default Setting
