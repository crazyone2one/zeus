<script setup lang="ts">
import { MenuOption, NMenu, NSplit } from 'naive-ui'
import { h, onMounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import ProjectSwitch from '../components/ProjectSwitch.vue'
import { i18n } from '/@/i18n'
import { hasPermission } from '/@/utils/permission'

const route = useRoute()
const showMenu = ref(true)
const activeKey = ref('')
const menuOptions: MenuOption[] = [
  {
    label: () =>
      h(
        RouterLink,
        {
          to: {
            name: 'projectHome',
          },
        },
        { default: () => i18n.t('project.info') },
      ),
    key: 'projectHome',
  },
  {
    // label: i18n.t('project.member'),
    label: () =>
      h(
        RouterLink,
        {
          to: {
            name: 'projectMember',
          },
        },
        { default: () => i18n.t('project.member') },
      ),
    key: 'projectMember',
  },
  {
    // label: i18n.t('project.group_permission'),
    label: () =>
      h(
        RouterLink,
        {
          to: {
            name: 'projectUserGroup',
          },
        },
        { default: () => i18n.t('project.group_permission') },
      ),
    key: 'projectUserGroup',
  },
  {
    label: i18n.t('project.env'),
    key: 'pinball-1973',
    disabled: true,
  },
  {
    label: i18n.t('project.file_manage'),
    key: 'a-wild-sheep-chase',
    disabled: true,
  },
  {
    label: i18n.t('error_report_library.name'),
    key: 'dance-dance-dance',
    show: showMenu.value,
    disabled: true,
  },
  {
    // label: i18n.t('workspace.template_manage'),
    label: () =>
      h(
        RouterLink,
        {
          to: {
            name: 'projectTemplate',
          },
        },
        { default: () => i18n.t('workspace.template_manage') },
      ),
    key: 'projectTemplate',
    show: showMenu.value,
  },
  {
    label: i18n.t('organization.message_settings'),
    key: 'dance-dance-dance',
    show: showMenu.value,
    disabled: true,
  },
  {
    label: i18n.t('project.log'),
    key: 'dance-dance-dance',
    show: showMenu.value,
    disabled: true,
  },
]
onMounted(() => {
  let menuCount = 0
  let permissions = [
    'PROJECT_APP_MANAGER:READ+EDIT',
    'PROJECT_MESSAGE:READ',
    'PROJECT_OPERATING_LOG:READ',
    'PROJECT_CUSTOM_CODE:READ',
    'PROJECT_TEMPLATE:READ',
    'PROJECT_VERSION:READ',
    'PROJECT_ERROR_REPORT_LIBRARY:READ',
  ]
  for (let permission of permissions) {
    // 更多选项中菜单数量小于3个时，不显示更多选项
    if (menuCount >= 3) {
      showMenu.value = false
      break
    }
    if (hasPermission(permission)) {
      menuCount++
    }
  }
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
