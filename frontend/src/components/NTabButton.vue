<script setup lang="ts">
import { NButtonGroup } from 'naive-ui'
import { computed, ref } from 'vue'
interface Props {
  isShowChangeButton?: boolean
  leftButtonEnable?: boolean
  leftTip?: string
  leftIconActiveClass?: string
  activeDom: string
}
const props = withDefaults(defineProps<Props>(), {
  isShowChangeButton: true,
  leftButtonEnable: true,
  leftTip: 'left',
  leftIconActiveClass: 'i-carbon:chevron-left',
})

const isShow = ref(true)
const leftActive = computed(() => {
  return props.activeDom === 'left'
})
const rightActive = computed(() => {
  return props.activeDom === 'right'
})
</script>
<template>
  <n-card v-if="isShow">
    <n-button-group v-if="isShowChangeButton" class="btn-group">
      <n-tooltip v-if="leftButtonEnable" class="item">
        <template #trigger>
          <n-button secondary :class="{ active: leftActive }" style="margin: 3px 8px 3px 3px">
            <template #icon>
              <n-icon>
                <span :class="leftIconActiveClass" />
              </n-icon>
            </template>
          </n-button>
        </template>
        {{ leftTip }}
      </n-tooltip>
    </n-button-group>
  </n-card>
</template>

<style scoped>
.item {
  height: 32px;
  padding: 5px 8px;
  border: solid 1px var(--primary_color);
}
.btn-group {
  float: right;
  box-sizing: border-box;
  width: 64px;
  height: 32px;
  background: #ffffff;
  border: 1px solid #bbbfc4;
  border-radius: 4px;
  flex: none;
  order: 7;
  flex-grow: 0;
  margin-left: 12px;
}
</style>
