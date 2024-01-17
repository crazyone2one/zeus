<script setup lang="ts">
import { MenuOption, NMenu, NSplit } from 'naive-ui'
import { h, onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { i18n } from '/@/i18n'
import { getCurrentProjectId } from '/@/utils/token'
import ProjectSwitch from '/@/views/project-manage/components/ProjectSwitch.vue'

const route = useRoute()
const activeKey = ref('')
const menuOptions: MenuOption[] = [
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/track/home',
          },
        },
        { default: () => i18n.t('i18n.home') },
      ),
    key: 'trackHome',
  },
  {
    // label: i18n.t('project.member'),
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/track/case/all?projectId=' + getCurrentProjectId(),
          },
        },
        { default: () => i18n.t('test_track.case.test_case') },
      ),
    key: 'testCase',
  },
  {
    // label: i18n.t('project.group_permission'),
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/track/review/all',
          },
        },
        { default: () => i18n.t('test_track.review.test_review') },
      ),
    key: 'testCaseReview',
  },
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/track/plan/all',
          },
        },
        { default: () => i18n.t('test_track.plan.test_plan') },
      ),
    key: 'testPlan',
  },
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/track/issue',
          },
        },
        { default: () => i18n.t('test_track.issue.issue_management') },
      ),
    key: 'issueManagement',
  },
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            path: '/track/testPlan/reportList',
          },
        },
        { default: () => i18n.t('commons.report') },
      ),
    key: 'testPlanReportList',
  },
]
onMounted(() => {
  activeKey.value = route.name as string
})
</script>
<template>
  <div>
    <n-split direction="horizontal" style="height: 42px" :default-size="0.15">
      <template #1>
        <project-switch />
      </template>
      <template #2>
        <n-menu v-model:value="activeKey" mode="horizontal" :options="menuOptions" responsive />
      </template>
    </n-split>
  </div>
</template>

<style scoped></style>
