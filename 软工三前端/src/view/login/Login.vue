<template>
  <div>
    <div class="leftPic"></div>
    <div class="rightForm">
      <header class="title">
        <span class="loginReturn" @click="$router.push('/')">登录nioCoin</span>
      </header>
      <div class="loginForm">
        <el-form ref="refForm" :model="form" :rules="rules">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名或邮箱"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" placeholder="请输入密码" show-password @keyup.enter="onSubmit"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button style="margin-top: 20px;width: 430px" type="primary" @click="onSubmit">登录</el-button>
          </el-form-item>
        </el-form>
	      <div class="toReg" style="float: left">
		      <span>忘记密码？</span>
		      <span class="loginPageLink" @click="$router.push('/forget')">找回密码</span>
	      </div>
        <div class="toReg" style="float: right">
          <span>没有用户？</span>
          <span class="loginPageLink" @click="$router.push('/register')">注册</span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {reactive, ref, unref} from "vue";
import {useRouter} from "vue-router";
import {useStore} from "vuex";
import {ElMessage} from "element-plus";

// const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
export default {
  setup() {
    let router = useRouter();
    let store = useStore();

    let form = reactive({
      username: "",
      password: ""
    });
    let refForm = ref();

    const convert = form => {
      return {
        username: form.username,
        password: form.password
      };
    };

    const rules = {
      username: [
        {required: true, message: "请输入用户名或邮箱", trigger: "blur"}
      ],
      password: [
        {required: true, message: "请输入密码", trigger: "blur"}
      ]
    };

    const onSubmit = async () => {
      const theForm = unref(refForm);
      await theForm.validate();
      await store.dispatch("login", convert(form));
      if (store.getters.logged) {
        console.log("success");
        ElMessage.success("登录成功");
        await router.push("/mine/item");
      }
    };

    return {
      onSubmit,
      form,
      refForm,
      rules
    };
  }
};
</script>
<style scoped src="./style.less" />
