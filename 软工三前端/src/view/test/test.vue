<template>
  <div
      style="display: flex;flex-direction: column;justify-content: center;align-items: center;height: 100vh;width: 100vw">
    <kg-card :kg="kg"></kg-card>
  </div>
</template>

<script>
import kgCard from "../../components/KgCard/kgCard.vue";

export default {
  data() {
    return {
      kg: {
        "graphId": "2",
        "graphName": "test",
        "time": "2021-04-13T07:40:27.000+00:00"
      }
    };
  },
  components: {
    kgCard
  }
};
</script>

<style scoped>

</style>
