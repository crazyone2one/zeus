<script setup lang="ts">
import { computed, ref } from 'vue'
import CaseDetailComponent from './CaseDetailComponent.vue'
import { i18n } from '/@/i18n'

interface IProps {
  editable: boolean
  form: Record<string, unknown>
  caseId: string
  readOnly: boolean
}
const props = withDefaults(defineProps<IProps>(), {})
const caseActiveName = ref('detail')
const commentState = ref('READY')
const isCommentEdit = computed(() => {
  return commentState.value === 'EDIT'
})
</script>
<template>
  <div class="content-body-wrap">
    <div v-if="!editable" class="tab-pane-wrap">
      <n-tabs v-model:value="caseActiveName" type="line" animated>
        <!-- 用例详情 -->
        <n-tab-pane name="detail" :tab="i18n.t('case.use_case_detail')">
          <div class="tab-container" :class="{ 'comment-edit-tab-container': isCommentEdit }">
            <case-detail-component
              :form="form"
              :editable="editable"
              :case-id="caseId"
              :read-only="readOnly || !editable"
            />
          </div>
        </n-tab-pane>
        <!-- 关联用例 -->
        <n-tab-pane name="associateTestCases" :tab="i18n.t('case.associate_test_cases')" disabled>
          Hey Jude
        </n-tab-pane>
        <!-- 关联缺陷 -->
        <n-tab-pane name="associatedDefects" :tab="i18n.t('test_track.case.relate_issue')" disabled>
          Hey Jude
        </n-tab-pane>
        <!-- 依赖关系 -->
        <n-tab-pane name="dependencies" :tab="i18n.t('case.dependencies')" disabled> Hey Jude </n-tab-pane>
        <!-- 评论 -->
        <n-tab-pane name="comment" :tab="i18n.t('case.comment')" disabled> Hey Jude </n-tab-pane>
        <!-- 变更记录 -->
        <n-tab-pane name="changeRecord" :tab="i18n.t('case.change_record')" disabled> Hey Jude </n-tab-pane>
      </n-tabs>
    </div>
  </div>
</template>

<style scoped></style>
