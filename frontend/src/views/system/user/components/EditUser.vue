<script setup lang="ts">
import { useForm } from '@alova/scene-vue'
import { useRequest } from 'alova'
import { FormInst, FormRules, SelectOption } from 'naive-ui'
import { ref } from 'vue'
import { IWorkspaceResource } from '/@/apis/interface'
import { IGroup, getAllUserGroupByType, getUserAllGroups } from '/@/apis/modules/group-api'
import { IProject } from '/@/apis/modules/project-api'
import { IUser, specialCreateUser, specialModifyUser } from '/@/apis/modules/user-api'
import { IWorkspace, getGroupResource } from '/@/apis/modules/workspace-api'
import NModalDialog from '/@/components/NModalDialog.vue'
import { i18n } from '/@/i18n'
import { GROUP_TYPE } from '/@/utils/constants'

const modalDialog = ref<InstanceType<typeof NModalDialog> | null>(null)
const createUserForm = ref<FormInst | null>(null)
const type = ref('Add')
const title = ref('创建用户')
const limitOptionCount = ref(400)
const userGroup = ref<Array<SelectOption>>([])
const btnAddGroup = ref(false)
const currentWSGroupIndex = ref(-1)
const currentGroupWSIds = ref<Set<string>>(new Set())
const {
  loading: submiting,
  send: submit,
  // 响应式的表单数据，内容由initialForm决定
  form: model,
} = useForm((model) => (type.value === 'Add' ? specialCreateUser(model) : specialModifyUser(model)), {
  // 初始化表单数据
  initialForm: {
    id: '',
    name: '',
  } as IUser,
  // 设置这个参数为true即可在提交完成后自动重置表单数据
  resetAfterSubmiting: true,
  immediate: false,
})
const rules: FormRules = {
  name: [{ required: true, message: i18n.t('user.input_name'), trigger: 'blur' }],
  password: [{ required: true, message: i18n.t('user.input_password'), trigger: 'blur' }],
  email: {
    required: true,
    message: i18n.t('user.input_email'),
    trigger: 'blur',
  },
}
const emit = defineEmits(['refresh'])
const handleSave = () => {
  createUserForm.value?.validate((err) => {
    if (!err) {
      // console.log(`output->model.value`, model.value)
      submit()
        .then(() => {
          modalDialog.value?.hideModal()
          emit('refresh')
          window.$message.success(i18n.t('commons.save_success'))
        })
        .catch((e) => window.$message.error(e.message))
    } else {
      return
    }
  })
}
const { send: loadUserGroup } = useRequest((userId) => getUserAllGroups(userId), { immediate: false })
const { send: loadAllUserGroup } = useRequest((param) => getAllUserGroupByType(param), { immediate: false })
const { send: loadGroupResource } = useRequest((id, type) => getGroupResource(id, type), { immediate: false })
const open = (_type?: string, _title?: string, row?: IUser): void => {
  title.value = _title ?? title.value
  type.value = _type ?? type.value
  model.value = Object.assign({}, row)
  if (type.value === 'Edit') {
    loadUserGroup(row?.id).then((res) => {
      model.value.groups = res
      model.value.groups.forEach((group: IGroup) => {
        handleWorkspaceOption(group, group.workspaces)
        handleProjectOption(group, group.projects)
      })
    })
  }
  getAllUserGroup()
  modalDialog.value?.show()
}
const getAllUserGroup = (): void => {
  loadAllUserGroup({ type: GROUP_TYPE.SYSTEM }).then((res) => {
    userGroup.value = res.map((g) => ({
      label: g.name,
      value: g.id + '+' + g.type,
      selects: g.selects,
      type: g.type,
    }))
  })
}
const handleProjectOption = (group: IGroup, projects: Array<IProject> | undefined) => {
  if (!projects) {
    return
  }
  group.showSearchGetMore = projects.length > limitOptionCount.value
  const options = projects.slice(0, limitOptionCount.value)
  group.workspaceOptions = options.map((project) => ({
    label: project.name,
    value: project.id,
  }))
  if (!group.ids || group.ids.length === 0) {
    return
  }
  for (let id of group.ids) {
    let index = options.findIndex((o) => o.id === id)
    if (index <= -1) {
      let obj = group.workspaceOptions.find((d) => d.id === id)
      if (obj) {
        group.workspaceOptions.unshift(obj)
      }
    }
  }
}
const handleWorkspaceOption = (group: IGroup, workspaces: Array<IWorkspace> | undefined) => {
  if (!workspaces) {
    return
  }
  group.showSearchGetMore = workspaces.length > limitOptionCount.value
  const options = workspaces.slice(0, limitOptionCount.value)
  group.workspaceOptions = options.map((workspace) => ({
    label: workspace.name,
    value: workspace.id,
  }))
  if (!group.ids || group.ids.length === 0) {
    return
  }
  for (let id of group.ids) {
    let index = options.findIndex((o) => o.id === id)
    if (index <= -1) {
      let obj = workspaces.find((d) => d.id === id)
      if (obj) {
        group.workspaceOptions.unshift({ value: obj.id, label: obj.name })
      }
    }
  }
}
const groupType = (idType: string) => {
  if (!idType) {
    return
  }
  return idType.split('+')[1]
}
const getLabel = (index: number) => {
  let a = index + 1
  return i18n.t('commons.group') + a
}
const activeGroup = (group: IGroup) => {
  const tmp = userGroup.value.filter((ug) => {
    if (!group.selects) {
      return true
    }
    let sign = true
    for (let groupSelect of group.selects) {
      if (ug.value + '+' + ug.type === groupSelect) {
        sign = false
        break
      }
    }
    return sign
  })
  return tmp
}
const addGroup = () => {
  createUserForm.value?.validate((valid) => {
    if (!valid) {
      let roleInfo: { selects: string[] } = { selects: [] }
      roleInfo.selects = []
      let ids = model.value.groups.map((r: IGroup) => r.type)
      ids.forEach((id: string) => {
        roleInfo.selects.push(id)
      })
      const groups = model.value.groups
      groups.push(roleInfo)
      if (model.value.groups.length > userGroup.value.length - 1) {
        btnAddGroup.value = true
      }
    } else {
      return false
    }
  })
}
const removeGroup = (item: IGroup) => {
  if (model.value.groups.length === 1) {
    window.$message.info(i18n.t('system_user.remove_group_tip'))
    return
  }
  let index = 0
  model.value.groups.map((g: IGroup) => {
    console.log(`output->index`, index)
    console.log(`output->group`, item)
    console.log(`output->g`, g)
    if (g.type !== item.type) {
      index++
    } else {
      return true
    }
  })
  console.log(`output->model.value.groups`, model.value.groups)

  console.log(`output->model.value.groups-index`, model.value.groups[index])

  if (item.type) {
    let _type = item.type.split('+')[1]
    if (_type === GROUP_TYPE.WORKSPACE) {
      currentWSGroupIndex.value = -1
    } else {
      if (currentWSGroupIndex.value > index) {
        currentWSGroupIndex.value = currentWSGroupIndex.value - 1
      }
    }
  }
}

const getResource = (idType: string, index: number) => {
  if (!idType) {
    return
  }
  let id = idType.split('+')[0]
  let type = idType.split('+')[1]
  if (index > 0 && model.value.groups[index].ids && model.value.groups[index].ids.length > 0) {
    return
  }
  let isHaveWorkspace = false
  if (type === GROUP_TYPE.PROJECT) {
    for (let i = 0; i < model.value.groups.length; i++) {
      let group = model.value.groups[i]
      let _type = group.type.split('+')[1]
      if (_type === GROUP_TYPE.WORKSPACE) {
        isHaveWorkspace = true
        break
      }
    }
  } else if (type === GROUP_TYPE.WORKSPACE) {
    isHaveWorkspace = true
  }
  loadGroupResource(id, type).then((res) => {
    const tmp = res
    if (tmp) {
      _setResource(tmp, index, type)
      if (id === 'super_group') {
        return
      }
      if (isHaveWorkspace === false) {
        addWorkspaceGroup(id, index)
      }
    }
  })
}
const addWorkspaceGroup = (id: string, index: number) => {
  let isHaveWorkSpace
  model.value.groups.forEach((item: IGroup) => {
    if (item.type === 'ws_member+WORKSPACE') {
      isHaveWorkSpace = true
    }
  })
  if (isHaveWorkSpace) {
    return
  }
  loadGroupResource(id, GROUP_TYPE.WORKSPACE).then((res) => {
    let data = res
    if (data) {
      let roleInfo: { selects: string[]; type: string; ids: string[] } = { selects: [], type: '', ids: [] }
      roleInfo.selects = []
      roleInfo.type = 'ws_member+WORKSPACE'
      let ids: Array<string> = model.value.groups.map((r: IGroup) => r.type)
      ids.forEach((id) => {
        roleInfo.selects.push(id)
      })
      if (currentGroupWSIds.value.size > 0) {
        roleInfo.ids = []
        currentGroupWSIds.value.forEach((item) => {
          roleInfo.ids.push(item)
        })
      } else {
        roleInfo.ids = []
      }
      let groups = model.value.groups
      groups.push(roleInfo)
      currentWSGroupIndex.value = index + 1
      _setResource(data, index + 1, GROUP_TYPE.WORKSPACE)
    }
  })
}
const _setResource = (data: IWorkspaceResource, index: number, type: string) => {
  switch (type) {
    case GROUP_TYPE.WORKSPACE:
      model.value.groups[index].workspaces = data.workspaces
      handleWorkspaceOption(model.value.groups[index], data.workspaces)
      break
    case GROUP_TYPE.PROJECT:
      model.value.groups[index].projects = data.projects
      handleProjectOption(model.value.groups[index], data.projects)
      break
    default:
      break
  }
}
defineExpose({ open })
</script>
<template>
  <n-spin :show="submiting">
    <n-modal-dialog ref="modalDialog" :title="title" dialog-width="40%" @confirm="handleSave">
      <template #content>
        <n-form
          ref="createUserForm"
          :model="model"
          :rules="rules"
          label-placement="left"
          require-mark-placement="right-hanging"
          label-width="auto"
        >
          <n-form-item :label="$t('commons.name')" path="name">
            <n-input v-model:value="model.name" :placeholder="$t('user.input_name')" />
          </n-form-item>
          <n-form-item :label="$t('commons.email')" path="email">
            <n-input v-model:value="model.email" :placeholder="$t('user.input_email')" />
          </n-form-item>
          <n-form-item :label="$t('commons.phone')" path="phone">
            <n-input v-model:value="model.phone" :placeholder="$t('user.input_phone')" />
          </n-form-item>
          <n-form-item v-if="type === 'Add'" :label="$t('commons.password')" path="password">
            <n-input
              v-model:value="model.password"
              type="password"
              show-password-on="mousedown"
              :placeholder="$t('user.input_password')"
            />
          </n-form-item>
          <div v-for="(group, index) in model.groups" :key="index">
            <n-form-item :label="getLabel(index)" :path="'groups.' + index + '.type'">
              <n-select
                v-model:value="group.type"
                :options="activeGroup(group)"
                :disabled="model.groups[index].type != null && model.groups[index].type !== ''"
                :placeholder="$t('user.select_group')"
                @update:value="getResource(group.type, index)"
              />
              <n-button size="tiny" style="margin-left: 20px" @click.prevent="removeGroup(group)">
                {{ $t('commons.delete') }}</n-button
              >
            </n-form-item>
            <div v-if="groupType(group.type) === GROUP_TYPE.WORKSPACE">
              <n-form-item :label="$t('commons.workspace')" :path="'groups.' + index + '.ids'">
                <n-select
                  v-model:value="group.ids"
                  :options="group.workspaceOptions"
                  :placeholder="$t('system_user.search_get_more_tip')"
                  multiple
                />
              </n-form-item>
            </div>
            <div v-if="groupType(group.type) === GROUP_TYPE.PROJECT">
              <n-form-item :label="$t('commons.project')" :path="'groups.' + index + '.ids'">
                <n-select
                  v-model:value="group.ids"
                  :options="group.projectOptions"
                  :placeholder="$t('system_user.search_get_more_tip')"
                  multiple
                />
              </n-form-item>
            </div>
          </div>
          <n-form-item>
            <n-button type="success" :disabled="btnAddGroup" @click="addGroup"> {{ $t('group.add') }}</n-button>
          </n-form-item>
        </n-form>
      </template>
    </n-modal-dialog>
  </n-spin>
</template>

<style scoped></style>
