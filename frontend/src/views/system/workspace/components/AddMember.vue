<script setup lang="ts">
import { useRequest } from 'alova'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { reactive, ref } from 'vue'
import { getUserGroupList } from '/@/apis/modules/group-api'
import { getUserListByResourceUrl } from '/@/apis/modules/user-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { GROUP_PROJECT } from '/@/utils/constants'
import { getCurrentProjectId } from '/@/utils/token'

interface IProp {
  groupType?: string
  groupScopeId?: string
  projectId?: string
  userResourceUrl?: string
}
const props = withDefaults(defineProps<IProp>(), {
  groupType: '',
  groupScopeId: '',
  projectId: '',
  userResourceUrl: '/user/list',
})

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const formRef = ref<FormInst | null>(null)
const model = reactive({
  userIds: [],
  groupIds: [],
})
const userOptions = ref<Array<SelectOption>>([])
const groupOptions = ref<Array<SelectOption>>([])
const rules: FormRules = {
  userIds: [{ type: 'array', required: true, message: i18n.t('member.please_choose_member'), trigger: 'blur' }],
  groupIds: { type: 'array', required: true, message: i18n.t('group.please_select_group'), trigger: 'blur' },
}
const emit = defineEmits(['submit'])
const handleSave = () => {
  formRef.value?.validate((err) => {
    if (!err) {
      let param = {
        userIds: model.userIds,
        groupIds: model.groupIds,
      }
      emit('submit', param)
    } else {
      return
    }
  })
}
const { send: sendUserList } = useRequest((url) => getUserListByResourceUrl(url), { immediate: false })
const { send: sendGroupList } = useRequest((param) => getUserGroupList(param), { immediate: false })
// 利用send函数返回的promise对象
const parallelRequest = async () => {
  let param = { type: props.groupType, resourceId: props.groupScopeId, projectId: '' }
  if (props.groupType === GROUP_PROJECT) {
    param.projectId = props.projectId || getCurrentProjectId()
  }
  const [userResponse, groupResponse] = await Promise.all([sendUserList(props.userResourceUrl), sendGroupList(param)])
  // 并行请求完成，继续处理业务...
  userResponse.map((u) => {
    userOptions.value.push({ label: u.name, value: u.id })
    if (model.userIds) {
      model.userIds.forEach((id) => {
        let index = userOptions.value.findIndex((o) => o.id === id)
        if (index <= -1) {
          let obj = userOptions.value.find((d) => d.id === id)
          if (obj) {
            userOptions.value.unshift(obj)
          }
        }
      })
    }
  })
  groupResponse.map((g) => {
    groupOptions.value.push({ label: g.name, value: g.id })
  })
}
const open = (): void => {
  modalDialog.value?.show()
  parallelRequest()
}
const close = (): void => {
  modalDialog.value?.toggleModal()
}
defineExpose({ open, close })
</script>
<template>
  <n-spin :show="false">
    <n-modal-dialog ref="modalDialog" :title="$t('member.create')" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('commons.member')" path="userIds">
            <n-select
              v-model:value="model.userIds"
              :options="userOptions"
              filterable
              multiple
              :placeholder="$t('member.please_choose_member')"
            />
          </n-form-item>
          <n-form-item :label="$t('commons.group')" path="groupIds">
            <n-select
              v-model:value="model.groupIds"
              :options="groupOptions"
              filterable
              multiple
              :placeholder="$t('group.please_select_group')"
            />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
