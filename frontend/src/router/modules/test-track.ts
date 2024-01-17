import { RouteRecordRaw } from 'vue-router'
const Track: Array<RouteRecordRaw> = [
  {
    path: '/track',
    name: 'track',
    redirect: '/track/home',
    component: () => import(`/@/views/test-track/index.vue`),
    children: [
      {
        path: '/track/home',
        name: 'trackHome',
        component: () => import(`/@/views/test-track/home/index.vue`),
      },
      // {
      //   path: 'case/create',
      //   name: 'testCaseCreate',
      //   component: () => import('@/business/case/components/TestCaseEdit'),
      // },
      {
        path: 'case/:projectId',
        name: 'testCase',
        component: () => import(`/@/views/test-track/case/index.vue`),
      },
      {
        path: 'case/all/:redirectID?/:dataType?/:dataSelectRange?/:projectId?/:workspaceId?',
        name: 'testCaseRedirect',
        component: () => import(`/@/views/test-track/case/index.vue`),
      },
      // {
      //   path: 'case/edit/:caseId',
      //   name: 'testCaseEdit',
      //   component: () => import('@/business/case/components/TestCaseEdit'),
      // },
      {
        path: 'testPlan/reportList',
        name: 'testPlanReportList',
        component: () => import(`/@/views/test-track/report/index.vue`),
      },
      {
        path: 'issue/:id?/:projectId?/:dataSelectRange?',
        name: 'issueManagement',
        component: () => import(`/@/views/test-track/issue/index.vue`),
      },
      {
        path: 'plan/:type',
        name: 'testPlan',
        component: () => import(`/@/views/test-track/plan/index.vue`),
      },
      // {
      //   path: "plan/view/:planId",
      //   name: "planView",
      //   component: () => import('@/business/plan/view/TestPlanView')
      // },
      { path: 'review/:type', name: 'testCaseReview', component: () => import(`/@/views/test-track/review/index.vue`) },
    ],
  },
]
export default Track
