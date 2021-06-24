<template>
	<div>
		<div class="leftPic"></div>
		<div class="rightForm">
			<header class="title">
				<span class="forgetReturn" @click="$router.push('/')">忘记nioCoin密码</span>
			</header>
			<div class="loginForm">
				<el-form ref="refForm" :model="form" :rules="rules">
					<el-form-item prop="username">
						<el-input v-model="form.username" placeholder="请输入用户名"></el-input>
					</el-form-item>
					<el-form-item prop="email">
						<el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
					</el-form-item>
					<el-form-item prop="verCode">
						<el-input v-model="form.verCode" placeholder="请输入邮箱验证码">
							<template #append>
								<el-button @click="onSendEmail">发送验证码</el-button>
							</template>
						</el-input>
					</el-form-item>
					<el-form-item v-if="mode === 0">
						<el-button style="margin-top: 20px;width: 430px" type="primary" @click="verifyEmail">验证邮箱</el-button>
					</el-form-item>
					<el-form-item prop="password" v-if="mode === 1">
						<el-input v-model="form.password" placeholder="请输入密码" show-password></el-input>
					</el-form-item>
					<el-form-item prop="checkPassword" v-if="mode === 1">
						<el-input v-model="form.checkPassword" placeholder="请再次确认密码" show-password></el-input>
					</el-form-item>
					<el-form-item v-if="mode === 1">
						<el-button style="margin-top: 20px;width: 430px" type="primary" @click="onSubmit">立即创建</el-button>
					</el-form-item>
				</el-form>
				<div class="toReg" style="float: right">
					<span>已有账号？</span>
					<span class="registerPageLink" @click="$router.push('/login')">登录</span>
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

export default {
	setup() {
		let router = useRouter();
		let store = useStore();
		let mode = ref(0);
		let form = reactive({
			username: "",
			password: "",
			checkPassword: "",
			email: "",
			verCode: ""
		});
		let refForm = ref();
		
		const convert = form => {
			return {
				username: form.username,
				password: form.password,
				email: form.email,
				verCode: form.verCode
			};
		};
		
		const validatePass = (rule, value, callback) => {
			if (value === "") {
				callback(new Error("请输入密码"));
			} else {
				if (this.form.checkPassword !== "") {
					this.$refs.form.validateField("checkPass");
				}
				callback();
			}
		};
		const validatePass2 = (rule, value, callback) => {
			if (value === "") {
				callback(new Error("请再次输入密码"));
			} else if (value !== this.form.password) {
				console.log(value);
				callback(new Error("两次输入密码不一致!"));
			} else {
				callback();
			}
		};
		const validateEmail = (rule, value, callback) => {
			const mailReg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/;
			if (!value) {
				return callback(new Error("请输入邮箱"));
			}
			setTimeout(() => {
				if (mailReg.test(value)) {
					callback();
				} else {
					callback(new Error("请输入正确的邮箱格式"));
				}
			}, 100);
		};
		const rules = {
			username: [
				{required: true, message: "请输入用户名", trigger: "blur"},
				{min: 3, max: 20, message: "长度在6到20个字符", trigger: "blur"}
			],
			password: [
				{validator: validatePass, trigger: "blur"}
			],
			checkPassword: [
				{validator: validatePass2, trigger: "blur"}
			],
			email: [
				{validator: validateEmail, trigger: "blur"}
			],
			verCode: [
				{required: true, message: "请输入邮箱验证码", trigger: "blur"}
			]
		};
		const onSendEmail = async () => {
			try {
				console.log(form.email);
				await store.dispatch("getEmailCode", {
					emailAddr: form.email
				}).then(res => {
					console.log(res);
				});
				ElMessage.success("邮箱验证码已发送，请耐心等待");
			} catch (e) {
				console.log("[Error] Fail to sendVer!");
				console.log(e);
			}
		};
		const onSubmit = async () => {
			store.dispatch("forgetPassword",{username: form.username,password: form.password})
			.then(()=>{
				ElMessage.success("密码修改成功");
				router.push("/login");
			})
		};
		const verifyEmail = () => {
			store.dispatch("verifyEmail", {username: form.username, email: form.email, verCode: form.verCode})
				.then(() => {
					mode.value = 1
				})
		}
		return {
			onSubmit,
			onSendEmail,
			verifyEmail,
			form,
			mode,
			refForm,
			rules
		};
	}
};
</script>
<style scoped src="./style.less"/>
