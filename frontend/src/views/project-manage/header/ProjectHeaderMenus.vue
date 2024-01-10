<script setup lang="ts">
import { MenuOption, NMenu, NSplit } from 'naive-ui'
import { ref } from 'vue'
import ProjectSwitch from '../components/ProjectSwitch.vue'

const activeKey = ref('')
const menuOptions: MenuOption[] = [
  {
    label: '饮品',
    key: 'beverage',
  },
  {
    label: '食物',
    key: 'food',
  },
  {
    label: '过去增多，未来减少',
    key: 'the-past-increases-the-future-recedes',
  },
  {
    label: '1973年的弹珠玩具',
    key: 'pinball-1973',
    disabled: true,
  },
  {
    label: '寻羊冒险记',
    key: 'a-wild-sheep-chase',
    disabled: true,
  },
  {
    label: '舞，舞，舞',
    key: 'dance-dance-dance',
  },
]
</script>
<template>
  <div>
    <n-split direction="horizontal" style="height: 42px">
      <template #1>
        <project-switch />
      </template>
      <template #2>
        <n-menu v-model:value="activeKey" mode="horizontal" :options="menuOptions" responsive />
      </template>
    </n-split>
  </div>
</template>

<style scoped></style>
