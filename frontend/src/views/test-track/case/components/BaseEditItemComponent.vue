<script setup lang="ts">
import { FormRules, SelectOption } from 'naive-ui'
import { computed, onMounted, ref } from 'vue'
import { i18n } from '/@/i18n'
interface IContentObject {
  content: {
    name: string
    type: string
    demandId: string
    options: SelectOption[]
    value: string[]
    defaultValue: string[]
  }
  contentType: string
}
interface IProps {
  rules: FormRules
  prop: string
  model: Record<string, unknown>
  editable?: boolean
  autoSave?: boolean
  contentObject?: IContentObject
  readonlyHoverEvent?: boolean
  contentClickEvent?: boolean
  projectId: string
}
const porps = withDefaults(defineProps<IProps>(), {
  editable: true,
  autoSave: true,
  contentObject: () => {
    return {
      prefix: '',
      content: { type: '', demandId: '', options: [], value: [], defaultValue: [], name: '' },
      suffix: '',
      contentType: 'TEXT',
    }
  },
  readonlyHoverEvent: false,
  contentClickEvent: false,
})
const emit = defineEmits(['update:model'])
const formValue = computed({
  get: () => porps.model,
  set: (val) => {
    emit('update:model', val)
  },
})
const selfEditable = ref(false)
const hoverEditable = ref(false)
const memberOptions = ref<Array<SelectOption>>([])
const isCustomNone = ref(false)
const optionPlatform = ref('')
const optionPlatformValue = ref('')

const edit = computed(() => {
  return porps.editable
})
const getCustomComponentClass = computed(() => {
  let type = 'select'
  let curType = porps.contentObject.content.type || ''
  switch (curType) {
    case 'select':
    case 'multipleSelect':
    case 'cascadingSelect':
    case 'member':
    case 'multipleMember':
    case 'input':
      type = 'select'
      break
    case 'text':
      type = 'text'
      break
    case 'richText':
      type = 'text'
      break
    default:
      type = 'select'
      break
  }
  if (isCustomNone.value) {
    type = type + ' custom-empty'
  }
  return type
})
const getMemberOptions = () => {}
const mouseLeaveEvent = () => {
  if (porps.readonlyHoverEvent) {
    hoverEditable.value = false
  }
}
const clickContent = () => {
  if (porps.contentClickEvent) {
    selfEditable.value = true
    hoverEditable.value = false
  }
}
const handleReadTextClick = () => {
  selfEditable.value = !porps.autoSave ? false : true
}
const mouseEnterEvent = () => {
  if (porps.readonlyHoverEvent) {
    hoverEditable.value = true
  }
}
const getStoryPlatform = (demandOptions?: Array<unknown>) => {}
const getStoryLabel = (demandOptions?: Array<unknown>) => {}
const getCustomText = () => {
  let options = porps.contentObject.content.options || []
  if (
    porps.contentObject.content.type &&
    (porps.contentObject.content.type === 'member' || porps.contentObject.content.type === 'multipleMember')
  ) {
    options = memberOptions.value
    if (options && options.length > 0) {
      let tempValue =
        porps.contentObject.content.value && porps.contentObject.content.value.length > 0
          ? porps.contentObject.content.value
          : porps.contentObject.content.defaultValue
      if (!tempValue || (Array.isArray(tempValue) && tempValue.length <= 0)) {
        let customVal
        if (formValue.value) {
          customVal = formValue.value[porps.contentObject.content.name]
        }
        if (customVal) {
          isCustomNone.value = false
          tempValue = customVal
        } else {
          isCustomNone.value = true
          return i18n.t('case.none')
        }
      } else {
        isCustomNone.value = false
      }
      if (Array.isArray(tempValue) && tempValue.length > 0) {
        let arr = []
        tempValue.forEach((item) => {
          let temp = options.find((option) => option.value === item)
          if (temp) {
            if (Array.isArray(temp)) {
              arr.push(...temp)
            } else {
              arr.push(temp)
            }
          }
        })
      } else {
        let temp = options.find((option) => option.value === tempValue)
      }
    }
    if (['input', 'richText', 'textarea'].indexOf(porps.contentObject.content.type) > -1) {
      if (!porps.contentObject.content.defaultValue) {
        isCustomNone.value = true
        return i18n.t('case.none')
      } else {
        isCustomNone.value = false
        return porps.contentObject.content.defaultValue
      }
    } else if (['multipleInput'].indexOf(porps.contentObject.content.type) > -1) {
      if (porps.contentObject.content.defaultValue && porps.contentObject.content.defaultValue instanceof Array) {
        return porps.contentObject.content.defaultValue.join(' ')
      }
    }

    if (!porps.contentObject.content.defaultValue) {
      isCustomNone.value = true
      return i18n.t('case.none')
    } else {
      isCustomNone.value = false
      return porps.contentObject.content.defaultValue
    }
  }
}
onMounted(() => {
  if (
    porps.contentObject.content &&
    porps.contentObject.content.type &&
    (porps.contentObject.content.type === 'member' || porps.contentObject.content.type === 'multipleMember')
  ) {
    getMemberOptions()
  }
})
</script>
<template>
  <div>
    <div class="content" @mouseleave="mouseLeaveEvent">
      <div v-show="edit" class="edit">
        <n-form v-if="!editable" ref="formRef" :model="formValue" :rules="rules">
          <slot name="content" :on-click="clickContent" :hover-editable="hoverEditable"></slot>
        </n-form>
        <slot v-if="editable" name="content" :on-click="clickContent" :hover-editable="hoverEditable"></slot>
      </div>
      <div v-show="!edit" class="readonly">
        <div
          v-if="contentObject.content && contentObject.contentType === 'TEXT'"
          class="text"
          @click="handleReadTextClick"
        >
          <n-tooltip trigger="hover">
            <template #trigger>
              <span>{{ contentObject.content }}</span>
            </template>
            {{ contentObject.content }}
          </n-tooltip>
        </div>
        <div
          v-else-if="contentObject.content && contentObject.contentType === 'INPUT'"
          class="select"
          @click="handleReadTextClick"
          @mouseenter="mouseEnterEvent"
        >
          <n-tooltip trigger="hover">
            <template #trigger>
              <span>{{ contentObject.content }}</span>
            </template>
            {{ contentObject.content }}
          </n-tooltip>
        </div>
        <div
          v-else-if="
            contentObject.content &&
            contentObject.contentType === 'TAG' &&
            Array.isArray(contentObject.content) &&
            contentObject.content.length > 0
          "
          class="tag-wrap"
          @click="handleReadTextClick"
        >
          <div class="tag-box">
            <div v-for="(item, index) in contentObject.content" :key="index" class="tag-row">
              <n-tooltip trigger="hover">
                <template #trigger>
                  <span>{{ item }}</span>
                </template>
                {{ item }}
              </n-tooltip>
            </div>
          </div>
        </div>
        <div
          v-else-if="contentObject.content && contentObject.content.demandId && contentObject.contentType === 'STORY'"
          class="story-wrap"
          @click="handleReadTextClick"
        >
          <div class="story-box">
            <div class="platform">{{ getStoryPlatform() }}</div>
            <n-tooltip trigger="hover">
              <template #trigger>
                <div class="story-label text-ellipsis">{{ getStoryLabel() }}</div>
              </template>
              {{ getStoryLabel() }}
            </n-tooltip>
          </div>
        </div>
        <div
          v-else-if="contentObject.content && contentObject.contentType === 'SELECT'"
          class="select"
          @mouseenter="mouseEnterEvent"
        >
          {{ contentObject.content }}
        </div>
        <div
          v-else-if="contentObject.content && contentObject.contentType === 'RICHTEXT'"
          class="select"
          @click="handleReadTextClick"
        ></div>
        <div
          v-else-if="contentObject.content && contentObject.contentType === 'CUSTOM'"
          id="custom-div"
          :class="getCustomComponentClass"
          @click="handleReadTextClick"
          @mouseenter="mouseEnterEvent"
        >
          <span v-if="contentObject.content.type !== 'richText'">
            {{ getCustomText() }}
          </span>
        </div>
        <div v-else class="empty" @click="handleReadTextClick">
          {{ $t('case.none') }}
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
