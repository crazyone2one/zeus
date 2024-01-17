<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { FormInst, FormRules } from 'naive-ui'
import { ref } from 'vue'
import { IUser, specialModifyPassword } from '/@/apis/modules/user-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { getCurrentUserId } from '/@/utils/token'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const editPasswordForm = ref<FormInst | null>(null)
const changePasswordUser = ref('')
const rules: FormRules = {
  confirmPassword: [{ required: true, message: i18n.t('member.password_format_is_incorrect'), trigger: 'blur' }],
  newPassword: [{ required: true, message: i18n.t('user.input_password'), trigger: 'blur' }],
}
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => specialModifyPassword(model), {
  // 初始化表单数据
  initialForm: {
    id: '',
    newPassword: '',
    confirmPassword: '',
  },
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})
const emit = defineEmits(['refresh'])
const open = (record: IUser) => {
  model.value.id = record.id
  changePasswordUser.value = record.id
  modalDialog.value?.show()
}
const handleSave = () => {
  editPasswordForm.value?.validate((error) => {
    if (!error) {
      submit().then(() => {
        window.$message.success(i18n.t('commons.modify_success'))
        if (changePasswordUser.value === getCurrentUserId()) {
          //
        } else {
          modalDialog.value?.toggleModal()
          emit('refresh')
        }
      })
    } else {
      return false
    }
  })
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="$t('member.edit_password')" dialog-width="30%" @confirm="handleSave">
      <template #content>
        <n-form
          ref="editPasswordForm"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('member.new_password')" path="newPassword">
            <n-input
              v-model:value="model.newPassword"
              type="password"
              show-password-on="mousedown"
              :placeholder="$t('user.input_password')"
            />
          </n-form-item>
          <n-form-item :label="$t('member.repeat_password')" path="confirmPassword">
            <n-input
              v-model:value="model.confirmPassword"
              type="password"
              show-password-on="mousedown"
              :placeholder="$t('user.input_password')"
            />
          </n-form-item>
          <n-form-item>
            <n-input v-model:value="model.id" disabled />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
