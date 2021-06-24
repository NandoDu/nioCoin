<template>
  <el-drawer
      destroy-on-close
      direction="rtl"
      size=300px>
    <div>
      <el-menu
          class="el-menu-vertical-demo"
          default-active="2">
        <el-submenu index="1">
          <template #title>
            <i class="el-icon-location"></i>
            <span>Project Member</span>
          </template>
          <el-menu-item-group>
            <el-menu-item index="1-1">黄雨晨</el-menu-item>
            <el-menu-item index="1-2">李镔达</el-menu-item>
            <el-menu-item index="1-3">杜铭哲</el-menu-item>
            <el-menu-item index="1-4">戴俊浩</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
        <el-menu-item index="2" @click="$router.push('/guide')">
          <i class="el-icon-menu"></i>
          <template #title>User Guide</template>
        </el-menu-item>
        <el-menu-item index="3" @click="$router.push('/story')">
          <i class="el-icon-document"></i>
          <template #title>Product Story</template>
        </el-menu-item>
      </el-menu>
      <div v-if="!isLogged" class="login-button-block">
        <span class="drawerLink" style="font-size: 14px!important;" @click="$router.push('/login')">Sign In</span>
        <el-button size="mini"
                   style="border-radius: 6px!important;margin-left: 15px;font-size: 14px;right: 0!important;"
                   @click="$router.push('/register')">
          Sign Up
        </el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {computed} from "vue";

export default {
  name: "NewDrawer",
  props: ["show"],
  setup() {
    let store = useStore();
    return {
      isLogged: computed(() => store.getters.logged)
    };
  }
};
</script>

<style scoped src="./NewDrawer.less" />
