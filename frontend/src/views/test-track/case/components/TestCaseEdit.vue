<script setup lang="ts">
import { NDrawer, NDrawerContent } from 'naive-ui'
import { computed, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import CaseEditInfoComponent from './CaseEditInfoComponent.vue'
import TestCaseEditNameView from './TestCaseEditNameView.vue'
import { hasPermission } from '/@/utils/permission'
import { getCurrentUser } from '/@/utils/token'

interface IProps {
  isPublicShow?: boolean
  publicCaseId?: string
}
const props = withDefaults(defineProps<IProps>(), {
  isPublicShow: false,
  publicCaseId: '',
})
const active = ref(false)
const route = useRoute()
const form = reactive({
  name: 'xxxxxxxxxxx',
  num: '',
  nodePath: '/未规划用例',
  maintainer: getCurrentUser().id,
  priority: 'P0',
  type: '',
  method: '',
  prerequisite: '',
  testId: '',
  nodeId: '',
  steps: [
    {
      num: 1,
      desc: '',
      result: '',
    },
  ],
  stepDesc: '',
  stepResult: '',
  selected: [],
  remark: '',
  tags: [],
  demandId: '',
  demandName: '',
  status: 'Prepare',
  reviewStatus: 'Prepare',
  stepDescription: '',
  expectedResult: '',
  stepModel: 'STEP',
  customNum: '',
  followPeople: '',
  versionId: '',
})
const editableState = ref(false)
const isNameEdit = ref(true)
const caseId = computed(() => {
  return !props.isPublicShow ? (route.params.caseId as string) : props.publicCaseId
})
const editType = computed(() => {
  return route.query.type
})
const routeProjectId = computed(() => {
  return route.query.projectId
})
const isPublicCopy = computed(() => {
  return editType.value === 'publicCopy'
})
const isCopy = computed(() => {
  return editType.value === 'copy' || isPublicCopy.value
})
const isAdd = computed(() => {
  return !caseId.value || isCopy.value
})
const editable = computed(() => {
  return isAdd.value || editableState.value
})
const hasReadonlyPermission = computed(() => {
  return hasPermission('PROJECT_TRACK_CASE:READ') && !hasPermission('PROJECT_TRACK_CASE:READ+EDIT')
})
const readOnly = computed(() => {
  if (props.isPublicShow || hasReadonlyPermission.value) {
    return true
  }
  return !hasPermission('PROJECT_TRACK_CASE:READ+CREATE') && !hasPermission('PROJECT_TRACK_CASE:READ+EDIT')
})
const open = () => {
  active.value = true
  isNameEdit.value = true
}
defineExpose({ open })
</script>
<template>
  <n-drawer v-model:show="active" width="100%" placement="right">
    <n-drawer-content :native-scrollbar="false" closable>
      <!-- 创建 or 编辑用例 -->
      <div class="edit-content-container" :class="{ 'editable-edit-content-container': editable }">
        <div class="header-content-row">
          <!-- 用例名称展示与编辑 -->
          <test-case-edit-name-view
            :is-add="false"
            :is-name-edit="false"
            :editable-state="editableState"
            :name="form.name"
          />
        </div>
        <case-edit-info-component :form="form" :editable="false" :case-id="caseId" :read-only="readOnly" />
      </div>

      <template #footer>
        <n-button>Footer</n-button>
      </template>
    </n-drawer-content>
  </n-drawer>
</template>

<style scoped></style>
