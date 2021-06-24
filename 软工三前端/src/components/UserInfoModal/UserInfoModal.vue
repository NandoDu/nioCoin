<template>
	<el-dialog
		title="用户信息"
		width="40%"
		@close="handleClose"
		center>
		<div style="display: flex;flex-direction: column;align-items: center">
			<el-form ref="refForm" :model="form" label-width="80px">
				<el-form-item label="用户名:">
					<div style="display: flex;flex-direction: row" v-if="mode !== 1">
						<span>{{ username }}</span>
						<i class="el-icon-edit-outline" style="zoom: 1.5;margin-top: 6px;padding-left: 5px" @click="setMode(1)"></i>
					</div>
					<el-input size="medium" v-model="form.name" style="width: 50%" v-if="mode === 1"></el-input>
				</el-form-item>
				<el-form-item label="密码:">
					<div v-if="mode !== 3">
						<el-button type="primary" size="small" @click="setMode(3)">修改密码</el-button>
					</div>
					<div style="display: flex;flex-direction: column;" v-if="mode === 3">
						<el-input size="medium" v-model="form.oldPassword" placeholder="请输入原密码" show-password></el-input>
						<el-input size="medium" v-model="form.newPassword" placeholder="请输入新密码" show-password
						          style="margin-top: 20px"></el-input>
					</div>
				</el-form-item>
				<el-form-item label="用户头像:">
					<div v-if="mode !== 2">
						<img alt="avatar" :src="imgUrl" class="avatar" title="点击修改头像" @click="setMode(2)"/>
					</div>
					<div v-if="mode === 2">
						<el-upload
							list-type="picture-card"
							action="#"
							accept=".png,.jpg,.jpeg"
							:multiple=false
							:on-preview="handlePictureCardPreview"
							:on-remove="handleRemove"
							:http-request="handlePicChange"
							:on-change="checkFileNumber"
							:before-upload="checkFileLen"
						>
							<i class="el-icon-plus"></i>
						</el-upload>
						<el-dialog v-model="dialogVisible" width="60%">
							<img style="width: 100%" :src="dialogImageUrl" ref="avatarPreview" alt="">
						</el-dialog>
					</div>
				</el-form-item>
			</el-form>
		</div>
		<template #footer>
    <span class="dialog-footer" v-if="mode!==0">
	    <el-button @click="setMode(0)">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确 定</el-button>
    </span>
		</template>
	</el-dialog>
</template>

<script>

import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {useStore} from "vuex";
import axios from "axios";

export default {
	name: "UserInfoModal",
	props: ["show"],
	setup(props) {
		let store = useStore();
		let mode = ref(0);
		let username = ref('')
		let imgUrl = ref('')
		let avatarPreview = ref()
		let form = reactive({name: '', region: '', oldPassword: '', newPassword: ''})
		let refForm = ref();
		let fileLen = ref(0);
		let dialogImageUrl = ref('')
		let fileName = ref('')
		let dialogVisible = ref(false);
		const getUsername = () => {
			if (store.getters.loginSuccess) {
				username.value = store.getters.userInfo.username;
				form.name = store.getters.userInfo.username;
				imgUrl.value = store.getters.userInfo.imgUrl;
			}
		}
		getUsername();
		const setMode = (modeIndex) => {
			mode.value = modeIndex;
		}
		const checkFileNumber = (file, fileList) => {
			fileLen.value += 1;
			console.log(fileLen.value)
			// console.log(fileList)
			// if (fileList.length > 1) {
			// 	ElMessage.error("最多上传一张图片！")
			// 	return false;
			// }
		}
		
		const checkFileLen = (file) => {
			if (fileLen.value > 1) {
				ElMessage.error("最多上传一张图片！")
				return false;
			} else if (file.size > 1 * 1024 * 1024) {
				ElMessage.error("图片最大大小为1M")
				return false;
			}
		}
		const handlePicChange = (item) => {
			// const picLimit = item.file.size / 1024 / 1024 > 1;
			// if (picLimit) {
			// 	ElMessage.error("图片过大，请重新选择")
			// 	return
			// }
			// const reader = new FileReader();
			// reader.readAsDataURL(file.raw);
			let stringList = item.file.name.split('.')
			fileName.value = String(store.getters.userInfo.id) + '.' + stringList[1]
			let userIdTemp = store.getters.userInfo.id;
			let json = JSON.stringify({
				userId: userIdTemp,
				name: fileName.value
			})
			let formData = new FormData();
			let data = new Blob([json], {type: "application/json"});
			formData.append("image", item.file);
			formData.append("data", data);
			// store.dispatch("updateUserImg", formData)
			// 	.then(() => {
			// 		ElMessage.success("头像修改成功");
			// 	})
			axios.post('http://localhost:9000/api/user/uploadImg', formData).then((res) => {
				store.commit("setUrl", res.data.data)
				ElMessage.success("头像上传成功！")
			})
		}
		const handleClose = () => {
			console.log("关闭")
		}
		const handlePictureCardPreview = (file) => {
			dialogImageUrl.value = store.getters.url + '?v=' + Math.random()
			console.log(dialogImageUrl.value)
			dialogVisible.value = true;
		}
		const handleRemove = () => {
			fileLen.value -= 1
			console.log("remove")
		}
		const handleSubmit = () => {
			switch (mode.value) {
				case 0 :
					ElMessage.error("Unknown Action Error");
					break;
				case 1:
					store.dispatch("changeUsername", {oldUsername: store.getters.userInfo.username, newUsername: form.name})
						.then(() => {
							ElMessage.success("用户名修改成功")
							store.commit("setUsername", form.name)
							username.value = store.getters.userInfo.username
						})
					mode.value = 0;
					break;
				case 2:
					if (fileLen.value === 0) {
						ElMessage.error("请至少选择一张图片")
						return
					}
					ElMessage.success("头像修改成功");
					let temp = store.getters.url + '?v=' + Math.random();
					store.commit("setUserImg", temp)
					imgUrl.value = temp;
					fileLen.value = 0;
					mode.value = 0;
					break;
				case 3:
					store.dispatch("changePassword", {
						oldPass: form.oldPassword,
						newPass: form.newPassword,
						userName: store.getters.userInfo.username
					})
						.then(() => {
							ElMessage.success("密码修改成功")
						})
					ElMessage.success("3");
					mode.value = 0;
					break;
				default:
					mode.value = 0;
					ElMessage.error("Unknown Action Error");
					break;
			}
		}
		return {
			mode,
			form,
			username,
			dialogImageUrl,
			dialogVisible,
			imgUrl,
			handlePicChange,
			handlePictureCardPreview,
			handleClose,
			setMode,
			handleSubmit,
			handleRemove,
			checkFileNumber,
			checkFileLen,
			refForm
		}
	}
}
</script>

<style scoped src="./UserInfoModal.less"/>