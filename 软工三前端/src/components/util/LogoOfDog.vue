<template>
  <img alt="logo" class="logoStyle" src="/src/asset/logo.png" style="max-height: 30px;" @click="$router.push('/')">
</template>

<script>
export default {
  name: "LogoOfDog"
};
</script>

<style scoped>
.logoStyle {
  position: relative;
}

.logoStyle:hover {
  top: 1px;
}

.logoStyle:active {
  top: 2px
}
</style>
