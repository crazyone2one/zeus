<script setup lang="ts">
import { useRequest } from 'alova'
import { NFlex } from 'naive-ui'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { IProject, getProjectDetail, getProjectMemberSize } from '/@/apis/modules/project-api'
import { hasPermission } from '/@/utils/permission'
import { getCurrentProjectId } from '/@/utils/token'

const router = useRouter()
const project = ref<IProject>({} as IProject)
const memberSize = ref(0)
const show = ref(false)
const projectId = computed(() => {
  return getCurrentProjectId()
})
const { send } = useRequest((id) => getProjectDetail(id), { immediate: false })
const { send: loadMemberSize } = useRequest((id) => getProjectMemberSize(id), { immediate: false })
// 利用send函数返回的promise对象
const parallelRequest = async () => {
  if (projectId.value) {
    show.value = true
    const [projectResponse, memberResponse] = await Promise.all([
      send(projectId.value),
      loadMemberSize(projectId.value),
    ])
    // 并行请求完成，继续处理业务...
    project.value = projectResponse
    memberSize.value = memberResponse
    show.value = false
  }
}
const handleClick = (str: string, permissions: Array<string>) => {
  for (let permission of permissions) {
    if (hasPermission(permission)) {
      router.push(str)
      return
    }
  }
  window.$message.warning('无操作权限!!')
}
const handleEdit = () => {}
onMounted(() => {
  parallelRequest()
})
</script>
<template>
  <n-spin :show="show">
    <n-card>
      <n-flex justify="space-between">
        <n-card class="project-info-card">
          <div class="project-info-card-div shepherd-project-name">
            <span class="project-name">{{ project.name }}</span>
            <i v-permission="['PROJECT_MANAGER:READ+EDIT']" class="i-carbon:edit project-edit" @click="handleEdit"></i>
            <n-flex class="project-item">
              <span class="project-item-title">{{ $t('project.desc') }}：</span>
              <n-tooltip trigger="hover">
                <template #trigger>
                  <span class="project-item-desc project-desc">{{ project.description }}</span>
                </template>
                {{ project.description }}
              </n-tooltip>
            </n-flex>
            <n-flex class="project-item">
              <span class="project-item-title">{{ $t('project.manage_people') }}：</span>
              <span class="project-item-desc">{{ project.createUser }}</span>
            </n-flex>
            <n-flex class="project-item">
              <span class="project-item-title">{{ $t('project.create_time') }}：</span>
              <span class="project-item-desc">{{ project.createTime }}</span>
            </n-flex>
          </div>
        </n-card>
        <n-card class="project-menu-card">
          <div class="project-menu-card-div">
            <div class="div-item">
              <div style="float: left">
                <i
                  class="i-carbon:user-filled icon-color"
                  @click="handleClick('/project/member', ['PROJECT_USER:READ'])"
                >
                </i>
              </div>
              <div style="float: left">
                <span class="title" @click="handleClick('/project/member', ['PROJECT_USER:READ'])">
                  {{ $t('project.member') }}
                </span>
                <br />
                <span class="desc">{{ $t('project.member_desc') }}</span>
              </div>
            </div>
            <div class="div-item">
              <div style="float: left">
                <i class="i-carbon:user icon-color" @click="handleClick('/project/usergroup', ['PROJECT_GROUP:READ'])">
                </i>
              </div>
              <div style="float: left">
                <span class="title" @click="handleClick('/project/usergroup', ['PROJECT_GROUP:READ'])">
                  {{ $t('group.group_permission') }} </span
                ><br />
                <span class="desc">{{ $t('project.group_desc') }}</span>
              </div>
            </div>
            <div class="div-item">
              <div style="float: left">
                <i
                  class="i-carbon:application-web icon-color"
                  @click="handleClick('/project/env', ['PROJECT_ENVIRONMENT:READ'])"
                >
                </i>
              </div>
              <div style="float: left">
                <span class="title" @click="handleClick('/project/env', ['PROJECT_ENVIRONMENT:READ'])">
                  {{ $t('project.env') }} </span
                ><br />
                <span class="desc">{{ $t('project.env_desc') }}</span>
              </div>
            </div>
            <div class="div-item">
              <div style="float: left">
                <i
                  class="i-carbon:baggage-claim icon-color"
                  @click="handleClick('/project/file/manage', ['PROJECT_FILE:READ', 'PROJECT_FILE:READ+JAR'])"
                >
                </i>
              </div>
              <div style="float: left">
                <span
                  class="title"
                  @click="handleClick('/project/file/manage', ['PROJECT_FILE:READ', 'PROJECT_FILE:READ+JAR'])"
                >
                  {{ $t('project.file_manage') }} </span
                ><br />
                <span class="desc">{{ $t('project.file_desc') }}</span>
              </div>
            </div>
            <div class="div-item">
              <div style="float: left">
                <i
                  class="i-carbon:document icon-color"
                  @click="handleClick('/project/code/segment', ['PROJECT_CUSTOM_CODE:READ'])"
                >
                </i>
              </div>
              <div style="float: left">
                <span class="title" @click="handleClick('/project/code/segment', ['PROJECT_CUSTOM_CODE:READ'])">
                  {{ $t('project.code_segment.code_segment') }} </span
                ><br />
                <span class="desc">{{ $t('project.code_segment_desc') }}</span>
              </div>
            </div>
          </div>
        </n-card>
      </n-flex>
    </n-card></n-spin
  >
</template>

<style scoped>
.project-info-card,
.project-menu-card {
  height: 200px;
  position: relative;
}

.project-info-card-div,
.project-menu-card-div {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.project-menu-card {
  height: 400px;
}

.project-name {
  font-weight: bold;
  text-align: center;
  color: var(--primary_color);
  font-size: 18px;
  user-select: none;
}

.project-item {
  margin-top: 25px;
  font-size: 16px;
  min-width: 220px;
}

.icon-color {
  font-size: 35px;
  color: var(--primary_color);
  margin-right: 6px;
}

.div-item {
  width: 100%;
  padding: 10px;
  height: 50px;
  min-width: 230px;
}

.icon-color:hover,
.title:hover {
  cursor: pointer;
}

.title {
  font-size: 10px;
}

.card {
  /* height: calc(100vh - 100px); */
  position: relative;
}

.number {
  font-size: 15px;
  color: var(--primary_color);
}

.number:hover {
  cursor: pointer;
}

.project-item-title {
  font-size: 15px;
  user-select: none;
}

.project-item-desc {
  font-size: 14px;
  user-select: none;
}

.card-col {
  margin-top: 80px;
}

.project-edit {
  color: var(--primary_color);
  font-size: 14px;
  margin-left: 8px;
}

.project-edit:hover {
  cursor: pointer;
}

.desc {
  color: #989292;
}

.project-desc {
  display: inline-block;
  width: 180px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 14px;
}
</style>
