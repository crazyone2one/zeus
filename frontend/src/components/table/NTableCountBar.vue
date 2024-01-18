<script setup lang="ts">
defineProps<{ countContent: string }>()
</script>
<template>
  <div class="count-div">
    <span>{{ countContent }}</span>
  </div>
</template>

<style scoped>
.count-div {
  display: inline-block;
  font-family: 'PingFang SC';
  font-style: normal;
  font-weight: 500;
  font-size: 18px;
  line-height: 26px;
  color: #1f2329;
}
</style>
