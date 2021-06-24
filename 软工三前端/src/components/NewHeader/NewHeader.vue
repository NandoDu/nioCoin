<template>
	<div v-bind="$attrs" class="header-container">
		<div :class="dynStyle" class="header-all header-flex header-center header-padding">
			<div class="div-hover" style="float: left;margin-top: 12px">
				<LogoOfDog/>
				<div v-if="isLogged" class="loginDrawerTrigger">
					<a class="a-override" @click="showDrawer=true">
						<LogoOfList/>
					</a>
				</div>
			</div>
			<div class="menu-block menu-block-visible">
				<div class="menu-item">
					<el-dropdown>
            <span class="el-dropdown-link" style="font-size: 18px;font-weight: 500">
              Project Members<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
						<template #dropdown>
							<el-dropdown-menu style="width: 180px;text-align: center">
								<el-dropdown-item class="elDropdownItemStyle">黄雨晨</el-dropdown-item>
								<el-dropdown-item class="elDropdownItemStyle">李镔达</el-dropdown-item>
								<el-dropdown-item class="elDropdownItemStyle">杜铭哲</el-dropdown-item>
								<el-dropdown-item class="elDropdownItemStyle">戴俊浩</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
				</div>
				<span class="menu-item" @click="$router.push('/guide')">User Guide</span>
				<span class="menu-item" @click="$router.push('/story')">Product Story</span>
			</div>
			<div v-if="isLogged" style="float: right">
				<el-dropdown trigger="click">
					<div style="display: flex;flex-direction: row">
						<img alt="avatar" class="avatarStyle" :src="imgUrl">
						<i class="el-icon-arrow-down" style="margin-top: 21px; margin-left: 5px"></i>
					</div>
					<template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item class="elDropdownItemStyle" @click="userInfoVisible = true">个人信息</el-dropdown-item>
							<el-dropdown-item class="elDropdownItemStyle" @click="$router.push('/mine/item')">我的文件</el-dropdown-item>
							<el-dropdown-item class="elDropdownItemStyle" @click="$router.push('/mine/history')">操作历史
							</el-dropdown-item>
							<el-dropdown-item class="elDropdownItemStyle" divided @click="logout"><span style="color: red">退出登录</span>
							</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
			</div>
			<div v-else>
				<div class="sign-block sign-block-visible">
					<span class="linkClass" @click="$router.push('/login')">Sign In</span>
					<el-button style="border-radius: 8px!important;margin-left: 15px" type="primary"
					           @click="$router.push('/register')">
						Sign Up
					</el-button>
				</div>
				<div class="burger-block burger-block-visible">
					<span class="burger-link" @click="$router.push('/login')">Sign In</span>
					<div style="margin-top: 8px;margin-left: 8px">
						<a class="a-override" @click="showDrawer=true">
							<LogoOfList/>
						</a>
					</div>
				</div>
				<div class="only-burger only-burger-visible">
					<div style="margin-top: 8px;margin-left: 8px">
						<a class="a-override" @click="showDrawer=true">
							<LogoOfList/>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<NewDrawer v-model="showDrawer"/>
	<UserInfoModal v-model="userInfoVisible" @close="getImgUrl"/>
</template>

<script>
import {LogoOfList, LogoOfDog} from "../util";
import {NewDrawer} from "../NewDrawer";
import {UserInfoModal} from "../UserInfoModal"
import {computed, ref} from "vue";
import {useStore} from "vuex";
import {useRouter} from "vue-router";

export default {
	name: "NewHeader",
	components: {
		LogoOfDog,
		LogoOfList,
		NewDrawer,
		UserInfoModal
	},
	props: {
		widthLimited: {type: Boolean}
	},
	setup(props) {
		let store = useStore();
		let imgUrl = ref("");
		let router = useRouter();
		const getImgUrl = () => {
			if (store.getters.loginSuccess) {
				imgUrl.value = store.getters.userInfo.imgUrl + '?v=' + Math.random();
			}
		}
		getImgUrl();
		return {
			showDrawer: ref(false),
			userInfoVisible: ref(false),
			isLogged: computed(() => store.getters.logged),
			logout: () => {
				store.commit("setLoginSuccess");
				localStorage.clear();
				router.push("/");
			},
			dynStyle: {
				limited: props.widthLimited
			},
			imgUrl,
			getImgUrl
		};
	}
	
};
</script>

<style scoped src="./NewHeader.less"/>
