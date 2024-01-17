<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { FormInst, NInputNumber, NSpin, NTreeSelect, TreeOption, TreeSelectOption } from 'naive-ui'
import { computed, ref } from 'vue'
import { getTestCaseNodesByCaseFilter, testCaseNodeAdd, testCaseNodeEdit } from '/@/apis/modules/test-case-api'
import { ITestPlanNode, editPlanNode, getTestPlanNodes, savePlanNode } from '/@/apis/modules/test-plan-node-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { getCurrentProjectId } from '/@/utils/token'
import { buildTestPlanNodeTree } from '/@/utils/tree'
interface IProp {
  type?: string
}
const props = withDefaults(defineProps<IProp>(), {
  type: 'case',
})
const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const formRef = ref<FormInst | null>(null)
const parentOptions = ref<Array<TreeSelectOption>>([])
const { send: sendTreeData } = useRequest(
  (pId) => (props.type === 'case' ? getTestCaseNodesByCaseFilter(pId) : getTestPlanNodes(pId)),
  { immediate: false },
)
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm(
  (model) =>
    model.id
      ? props.type === 'case'
        ? testCaseNodeEdit(model)
        : editPlanNode(model)
      : props.type === 'case'
        ? testCaseNodeAdd(model)
        : savePlanNode(model),
  {
    // 初始化表单数据
    initialForm: {
      id: '',
      name: '',
      level: 1,
      parentId: '',
    } as ITestPlanNode,
    // 设置这个参数为true即可在提交完成后自动重置表单数据
    resetAfterSubmiting: true,
    immediate: false,
  },
)
const emit = defineEmits(['refresh'])
const projectId = computed(() => {
  return getCurrentProjectId()
})
const open = (data?: ITestPlanNode) => {
  parentOptions.value = []
  sendTreeData(projectId.value).then((res) => buildTestPlanNodeTree(res, parentOptions.value))
  modalDialog.value?.show()
  if (data) {
    const level = buildNodeLevel(data)
    model.value.parentId = data.id
    model.value.level = data.level + 1
    model.value.name = ''
  }
}
const buildNodeLevel = (node: TreeOption) => {
  if (node.children) {
    let level: number = node.level as number
    const level_arr: Array<number> = node.children.map((item) => item.level as number)
    const tmp = Math.max(...level_arr)
    level = tmp + 1
    node.children.forEach((child) => buildNodeLevel(child))
    return level
  }
  return (node.level as number) + 1
}
const handleSave = () => {
  model.value.projectId = projectId.value
  submit().then(() => {
    window.$message.success(i18n.t('test_track.module.success_create'))
    emit('refresh')
    modalDialog.value?.toggleModal()
  })
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" title="编辑节点" @confirm="handleSave">
      <template #content>
        <n-form
          ref="formRef"
          :model="model"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item label="上级节点" path="parentId">
            <n-tree-select v-model:value="model.parentId" filterable :options="parentOptions" clearable />
          </n-form-item>
          <n-form-item label="节点名称" path="name">
            <n-input v-model:value="model.name" />
          </n-form-item>
          <n-form-item label="显示级别" path="level">
            <n-input-number v-model:value="model.level" />
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>
<style scoped></style>
