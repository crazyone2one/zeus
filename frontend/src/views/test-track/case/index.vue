<script setup lang="ts">
import { NSplit } from 'naive-ui'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { i18n } from '/@/i18n'
import { useUserStore } from '/@/store/modules/user-store'
import { getCurrentProjectId } from '/@/utils/token'
import TestCaseEdit from '/@/views/test-track/case/components/TestCaseEdit.vue'
import TestCaseList from '/@/views/test-track/case/components/TestCaseList.vue'
import TestCaseNodeTree from '/@/views/test-track/modules/TestCaseNodeTree.vue'

const router = useRouter()
const store = useUserStore()
const testCaseEdit = ref<InstanceType<typeof TestCaseEdit> | null>(null)
const options = [
  {
    label: i18n.t('test_track.case.import.import_by_excel'),
    key: 'excel',
  },
  {
    label: i18n.t('test_track.case.import.import_by_xmind'),
    key: 'xmind',
    disabled: true,
  },
]
const currentVersion = ref<string | null>(null)
const _projectId = computed(() => {
  return getCurrentProjectId()
})
const selectNode = computed(() => {
  return store.testCaseSelectNode
})
const handleSelect = (key: string) => {
  window.$message.info(key)
}
const handleCreateCase = () => {
  const query = { projectId: _projectId.value, createNodeId: selectNode.value ? selectNode.value.id : '' }
  let path = '/track/case/create'
  router.resolve({ path, query })
  testCaseEdit.value?.open()
  // window.open(TestCaseData.href, '_blank')
}
</script>
<template>
  <n-card>
    <div class="top-btn-group-layout" style="margin-bottom: 16px">
      <n-button v-permission="['PROJECT_TRACK_CASE:READ+CREATE']" type="primary" size="tiny" @click="handleCreateCase">
        <template #icon>
          <n-icon>
            <span class="i-carbon:add" />
          </n-icon>
        </template>
        {{ $t('test_track.case.create_case') }}
      </n-button>
      <n-dropdown trigger="click" :options="options" @select="handleSelect">
        <n-button v-permission="['PROJECT_TRACK_CASE:READ+IMPORT']" size="tiny" class="ml-2">
          <template #icon>
            <n-icon>
              <span class="i-carbon:cloud-upload" />
            </n-icon>
          </template>
          {{ $t('commons.import') }}</n-button
        >
      </n-dropdown>
    </div>
    <div style="display: flex" class="test-case-aside-layout">
      <n-split direction="horizontal" :default-size="0.15">
        <template #1>
          <test-case-node-tree />
        </template>
        <template #2>
          <test-case-list :default-version="currentVersion" />
        </template>
      </n-split>
    </div>
  </n-card>
  <test-case-edit ref="testCaseEdit" :public-case-id="''" :is-public-show="true" />
</template>

<style scoped>
/* .top-btn-group-layout :deep(.n-button--primary-type span),
.export-case-layout :deep(.el-button--small span) {
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 400;
  font-size: 14px;
  line-height: 22px;
  position: relative;
  top: -5px;
} */
</style>
