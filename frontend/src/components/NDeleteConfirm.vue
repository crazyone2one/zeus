<script setup lang="ts">
import { ref } from 'vue'
import { i18n } from '../i18n'
import NModalDialog from '/@/components/NModalDialog.vue'
interface IProp {
  title?: string
  withTip?: boolean
}
withDefaults(defineProps<IProp>(), {
  title: () => i18n.t('commons.title'),
  withTip: false,
})
const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const value = ref('')
const record = ref<Record<string, unknown>>({})
const emit = defineEmits(['delete'])
const handleSave = () => {
  if (value.value.trim() !== ('DELETE-' + record.value.name).trim()) {
    window.$message.warning(i18n.t('commons.incorrect_input'))
    return
  }
  emit('delete', record.value)
  modalDialog.value?.toggleModal()
}
const open = (row: Record<string, unknown>) => {
  record.value = row
  value.value = ''
  modalDialog.value?.show()
}
defineExpose({ open })
</script>
<template>
  <n-modal-dialog ref="modalDialog" :title="title" class="delete-confirm" @confirm="handleSave">
    <template #content>
      <n-space vertical>
        <div>
          <span>{{ $t('commons.delete_confirm') }}</span>
          <span class="delete-tip"> DELETE-{{ record.name }}</span>
        </div>
        <div v-if="withTip" class="tip">
          <span>
            <slot class="tip"></slot>
          </span>
        </div>
        <div>
          <n-input v-model:value="value" :placeholder="$t('commons.input_content')" />
        </div>
      </n-space>
    </template>
  </n-modal-dialog>
</template>

<style scoped>
.delete-confirm :deep(.n-dialog) {
  width: 500px;
}

.delete-confirm .n-dialog:first-child {
  margin-bottom: 10px;
}

.delete-confirm .n-space:first-child {
  margin-bottom: 20px;
}

.delete-tip {
  font-style: italic;
  font-weight: bold;
  white-space: pre-wrap;
}

.tip {
  margin-bottom: 20px;
  color: red;
}
</style>
