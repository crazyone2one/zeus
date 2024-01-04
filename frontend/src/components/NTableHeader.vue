<script setup lang="ts">
import { computed } from 'vue'
import { i18n } from '../i18n'
import NSearch from './NSearch.vue'
import NTableButton from './NTableButton.vue'
import { IQueryParam } from '/@/apis/interface'
interface IProp {
  showCreate?: boolean
  showImport?: boolean
  showRun?: boolean
  createTip?: string
  importTip?: string
  runTip?: string
  createPermission?: Array<string>
  uploadPermission?: Array<string>
  tip?: string
  haveSearch?: boolean
  condition: IQueryParam
}
const prop = withDefaults(defineProps<IProp>(), {
  showCreate: true,
  showImport: false,
  showRun: false,
  createTip: () => i18n.t('commons.create'),
  importTip: () => i18n.t('commons.import'),
  runTip: '',
  createPermission: () => [],
  uploadPermission: () => [],
  haveSearch: true,
  tip: () => i18n.t('commons.search_by_name'),
})
const emits = defineEmits(['update:condition', 'search', 'create', 'import', 'runTest'])
const _condition = computed({
  // getter
  get() {
    return prop.condition
  },
  // setter
  set(newValue) {
    console.log(`output->`, newValue)
    emits('update:condition', newValue)
  },
})
const handleSearch = () => {
  emits('search')
}
</script>
<template>
  <div>
    <n-space justify="space-between">
      <span class="operate-button">
        <n-table-button
          v-if="showCreate"
          v-permission="createPermission"
          :content="createTip"
          icon="i-carbon:add"
          @click="emits('create')"
        />
        <n-table-button v-if="showImport" :content="importTip" icon="i-carbon:cloud-upload" @click="emits('import')" />
        <n-table-button
          v-if="showRun"
          type="primary"
          :content="runTip"
          icon="i-carbon:play-outline"
          @click="emits('runTest')"
        />
        <slot name="button"></slot>
      </span>
      <span>
        <slot name="searchBarBefore"></slot>
        <n-search
          :show-base-search="haveSearch"
          :base-search-tip="tip"
          :condition="_condition"
          @search="handleSearch"
        />
      </span>
    </n-space>
  </div>
</template>

<style scoped></style>
