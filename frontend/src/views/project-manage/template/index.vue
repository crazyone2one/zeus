<script setup lang="ts">
import { NTabPane, NTabs } from 'naive-ui'
import CustomFieldList from './components/CustomFieldList.vue'
import TestCaseTemplateList from './components/TestCaseTemplateList.vue'
</script>
<template>
  <n-card content-style="padding: 0;">
    <n-tabs type="line" size="large" :tabs-padding="20" pane-style="padding: 20px;">
      <n-tab-pane :name="$t('custom_field.name')">
        <custom-field-list />
      </n-tab-pane>
      <n-tab-pane :name="$t('workspace.case_template_manage')">
        <test-case-template-list />
      </n-tab-pane>
      <n-tab-pane :name="$t('workspace.issue_template_manage')" disabled> ROCKLIFE </n-tab-pane>
      <n-tab-pane :name="$t('custom_template.api_template')" disabled> ROCKLIFE </n-tab-pane>
    </n-tabs>
  </n-card>
</template>
<style scoped></style>
