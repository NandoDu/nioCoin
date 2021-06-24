import {
	loginAPI,
	registerAPI,
	getAllGraphAPI,
	renameGraphAPI,
	deleteGraphAPI,
	getStarredGraphAPI,
	getEmailCodeAPI,
	changeUsernameAPI,
	changePasswordAPI,
	updateUserImgAPI,
	getAllGraphByOrderAPI,
	verifyEmailAPI,
	forgetPasswordAPI
} from "../api/user";
import actionCon from "./actionCon";

const user = {
	state: {
		loginSuccess: false,
		registerSuccess: false,
		userInfo: null,
		userGuideVisible: false,
		userInfoVisible: false,
		url: ""
	},
	mutations: {
		setUserGuideVisible: function (state, data) {
			state.userGuideVisible = data;
			//console.log(state.userGuideVisible);
		},
		setUsername: function (state, data) {
			state.userInfo.username = data;
		},
		setLoginSuccess: function (state, data) {
			if (data) {
				state.loginSuccess = true;
				state.userInfo = data;
			} else {
				state.loginSuccess = false;
				state.userInfo = null;
			}
		},
		setRegisterSuccess: function (state, data) {
			if (data) {
				state.registerSuccess = true;
			}
		},
		setUrl: function (state, data) {
			if (data !== "") {
				state.url = data;
			}
		},
		setUserImg: function (state, data) {
			if (data !== "")
				state.userInfo.imgUrl = data
		}

	},
	actions: {
		login: actionCon(loginAPI, "setLoginSuccess"),
		register: actionCon(registerAPI, "setRegisterSuccess"),
		getAllGraph: actionCon(getAllGraphAPI),
		getAllGraphByOrder: actionCon(getAllGraphByOrderAPI),
		getStarredGraph: actionCon(getStarredGraphAPI),
		renameGraph: actionCon(renameGraphAPI),
		deleteGraph: actionCon(deleteGraphAPI),
		getEmailCode: actionCon(getEmailCodeAPI),
		changeUsername: actionCon(changeUsernameAPI),
		changePassword: actionCon(changePasswordAPI),
		updateUserImg: actionCon(updateUserImgAPI, "setUrl"),
		verifyEmail: actionCon(verifyEmailAPI),
		forgetPassword: actionCon(forgetPasswordAPI)
	},
	getters: {
		logged: state => state.loginSuccess,
		registered: state => state.registerSuccess,
		userInfo: state => state.userInfo,
		userGuideVisible: state => state.userGuideVisible,
		loginSuccess: state => state.loginSuccess,
		url: state => state.url
	}
};
export default user;
