<script setup lang="ts">
interface IProps {
  isAdd: boolean
  isNameEdit: boolean
  editableState: boolean
}
defineProps<IProps>()
// eslint-disable-next-line no-undef
const name = defineModel<string>('name')
</script>
<template>
  <div class="case-name">
    <div v-if="isAdd || editableState">
      {{ editableState ? $t('test_track.case.edit_case') : $t('test_track.case.create_case') }}
    </div>
    <div v-else :class="isNameEdit ? 'name-input' : 'name-text'">
      <span v-if="isNameEdit">
        <n-input
          v-model:value="name"
          type="text"
          :placeholder="$t('case.please_enter_the_case_name')"
          maxlength="255"
          show-count
          clearable
        />
      </span>
      <span v-else>{{ name }}</span>
    </div>
  </div>
</template>

<style scoped>
.case-name {
  height: auto;
  font-size: 16px;
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 500;
  line-height: px2rem(24);
  color: #1f2329;
  margin-left: px2rem(8);
  margin-right: px2rem(8);
  cursor: pointer;
  /* 文本不会换行显示 */
  white-space: nowrap;
  /* 超出盒子部分隐藏 */
  overflow: hidden;
  /* 文本超出的部分打点显示 */
  text-overflow: ellipsis;
  max-width: 85%;
}
</style>
