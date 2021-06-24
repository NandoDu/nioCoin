<template>

  <ElForm :model="query">
    <ElFormItem>
      节点名
      <ElInput v-model="query.nodeName" />
    </ElFormItem>
    <ElFormItem v-show="false">
      关系名
      <ElInput v-model="query.edgeName" />
    </ElFormItem>
    <ElFormItem>
      模糊搜索
      <ElSwitch v-model="query.fuzzy" />
    </ElFormItem>
    <div v-if="inHighlight">
      <ElButton @click="reset">复原</ElButton>
    </div>
    <div v-else>
      <ElButton @click="search">搜索</ElButton>
      <ElButton @click="filter">过滤</ElButton>
    </div>
  </ElForm>

</template>

<script>
import {reactive, ref, unref} from "vue";
import {useStore} from "vuex";

export default {
  name: "SearchOption",
  emits: ["search", "filter", "reset"],
  setup(props, {emit}) {
    const store = useStore();
    const inHighlight = ref(false);
    const query = reactive({
      nodeName: "",
      edgeName: "",
      fuzzy: true
    });
    const check = () => {
      if (!query.nodeName && !query.edgeName) {
        console.log("[error] need query arguments");
        return false;
      }
      return true;
    };
    const search = () => {
      if (!check()) return;
      inHighlight.value = true;
      emit("search", query);
    };
    const filter = () => {
      if (!check()) return;
      inHighlight.value = true;
      emit("filter", query);
    };
    const reset = () => {
      inHighlight.value = false;
      emit("reset");
    };
    return {
      query,
      search,
      filter,
      reset,
      inHighlight
    };
  }
};
</script>

<style scoped>

</style>
