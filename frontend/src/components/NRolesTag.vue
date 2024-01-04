<script setup lang="ts">
import { NTag } from 'naive-ui'
import { IGroup } from '../apis/modules/group-api'

interface IProps {
  roles: Array<IGroup>
  type?: 'default' | 'primary' | 'info' | 'success' | 'warning' | 'error'
  size?: 'small' | 'medium' | 'large'
  bordered?: boolean
}
withDefaults(defineProps<IProps>(), {
  type: 'primary',
  size: 'small',
  bordered: false,
})
</script>
<template>
  <div>
    <n-space>
      <span v-for="(role, index) in roles" :key="index">
        <n-tag :bordered="bordered" :size="size" :type="type">
          {{ role.name }}
        </n-tag>
      </span>
    </n-space>
  </div>
</template>

<style scoped></style>
