<script setup lang="ts">
import { ITestCaseTemplate } from '/@/apis/modules/test-case-template-api'
interface IProps {
  template?: ITestCaseTemplate
  prop?: string
  showEdit?: boolean
}
withDefaults(defineProps<IProps>(), {
  template: () => ({}) as ITestCaseTemplate,
  prop: 'name',
  showEdit: true,
})
const emits = defineEmits(['cancel', 'save'])

const handleSave = (): void => emits('save')
const handleCancel = (): void => {
  emits('cancel')
}
</script>
<template>
  <n-space justify="space-between">
    <span class="title">{{ template[prop] }}</span>
    <div class="text-right ml-0">
      <n-space>
        <n-button size="tiny" @click="handleCancel">{{ $t('test_track.return') }}</n-button>
        <n-button size="tiny" type="primary" @click="handleSave">{{ $t('test_track.save') }}</n-button>
      </n-space>
    </div>
  </n-space>
</template>

<style scoped></style>
