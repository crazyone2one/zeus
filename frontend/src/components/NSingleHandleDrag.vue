<script setup lang="ts">
import { SelectOption } from 'naive-ui'
import { computed, ref } from 'vue'
import draggable from 'vuedraggable'

interface IProp {
  disable?: boolean
  isKv: boolean
  data?: Array<SelectOption>
}
const props = withDefaults(defineProps<IProp>(), {
  disable: false,
  data: () => [{ name: 'John 1', id: 0, system: false }],
})
const emits = defineEmits(['update:data'])
const list = computed({
  get: () => props.data,
  set: (val) => {
    emits('update:data', val)
  },
})

const editIndex = ref(-1)
const dragging = ref(false)
const operators = [
  {
    id: 1,
    icon: 'i-carbon:edit',
    isEdit: true,
    click: (element: SelectOption, index: number) => {
      if (props.disable) {
        return
      }
      if (!element.system) {
        editIndex.value = index
      }
    },
  },
  {
    id: 2,
    icon: 'i-carbon:close',
    isEdit: false,
    click: (index: number) => {
      if (props.disable) {
        return
      }
      list.value.splice(index, 1)
    },
  },
]
const getUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = (Math.random() * 16) | 0,
      v = c == 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}
const add = () => {
  let item = {
    label: '',
    value: '',
  }
  if (!props.isKv) {
    item.value = getUUID().substring(0, 14)
  }
  while (typeof item.value === 'number') {
    item.value = getUUID().substring(0, 14)
  }
  list.value.push(item)
  editIndex.value = list.value.length - 1
}
const log = (evt: object) => {
  console.log(`output->evt`, evt)
}
const handleTextEdit = () => {
  if (!props.isKv) {
    editIndex.value = -1
  }
}
const handleValueEdit = (element: SelectOption) => {
  if (element.value && element.text) {
    editIndex.value = -1
  }
}
</script>
<template>
  <div class="row">
    <draggable
      :list="list"
      class="list-group"
      item-key="name"
      @start="dragging = true"
      @end="dragging = false"
      @change="log"
    >
      <template #header>
        <div class="btn-group list-group-item" role="group">
          <n-button text class="add-text" :disabled="disable" @click="add">
            <template #icon>
              <n-icon>
                <span class="i-carbon:add" />
              </n-icon>
            </template>
            {{ $t('custom_field.add_option') }}
          </n-button>
        </div>
      </template>

      <template #item="{ element, index }">
        <div class="list-group-item item">
          <n-input
            v-if="editIndex === index"
            v-model:value="element.label"
            type="text"
            class="text-item"
            :placeholder="$t('custom_field.field_text')"
            maxlength="50"
            show-count
            @blur="handleTextEdit()"
          />
          <span v-else>
            <span v-if="element.system">
              {{ $t(element.label) }}
            </span>
            <span v-else>
              {{ element.label }}
            </span>
          </span>
          <n-input
            v-if="editIndex === index && isKv"
            v-model:value="element.label"
            type="text"
            class="text-item"
            :placeholder="$t('custom_field.field_value')"
            maxlength="50"
            show-count
            @blur="handleValueEdit(element)"
          />
          <span v-else-if="isKv" class="text-item">
            <span>
              {{ (element.value && isKv ? '(' : '') + element.value + (element.value && isKv ? ')' : '') }}
            </span>
          </span>
          <n-button
            v-for="item in operators"
            :key="item.id"
            text
            size="tiny"
            :disabled="element.system && item.isEdit"
            @click="item.click(element, index)"
          >
            <template #icon>
              <n-icon>
                <span :class="item.icon" />
              </n-icon>
            </template>
          </n-button>
        </div>
      </template>
    </draggable>
  </div>
</template>

<style scoped>
.add-text {
  font-size: 14px;
}

.add-text:hover {
  font-size: 14px;
  font-weight: bold;
}
.text-item {
  margin: 5px;
  width: 150px;
}
</style>
