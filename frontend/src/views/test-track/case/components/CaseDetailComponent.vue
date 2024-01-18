<script setup lang="ts">
import { computed } from 'vue'
import BaseEditItemComponent from './BaseEditItemComponent.vue'
import { i18n } from '/@/i18n'
interface IProps {
  editable: boolean
  form: Record<string, unknown>
  readOnly: boolean
}
const props = withDefaults(defineProps<IProps>(), {})
const emit = defineEmits(['update:model'])
const formValue = computed({
  get: () => props.form,
  set: (val) => {
    emit('update:model', val)
  },
})
const rules = {
  name: [
    {
      required: true,
      message: i18n.t('test_track.case.input_name'),
      trigger: 'blur',
    },
    {
      max: 255,
      message: i18n.t('test_track.length_less_than') + '255',
      trigger: 'blur',
    },
  ],
}
</script>
<template>
  <div>
    <n-form ref="formRef" :model="formValue" :rules="rules">
      <!-- case name -->
      <div v-if="editable" class="case-name-row" @keydown.enter.prevent>
        <div class="case-name case-title-wrap">
          <div class="name title-wrap">{{ $t('case.case_name') }}</div>
          <div class="required required-item"></div>
        </div>
      </div>
      <div class="content-wrap">
        <div class="opt-row">
          <base-edit-item-component
            :editable="editable"
            :auto-save="!readOnly"
            :model="form"
            :rules="rules"
            :content-object="{ content: form.name, contentType: 'TEXT' }"
          >
        <template #content="{ onClick }">
            <div @click="onClick">
            </div>
        </template>
        </base-edit-item-component>
        </div>
      </div>
    </n-form>
  </div>
</template>

<style scoped></style>
