import {ContentType, Method} from "./apiCon";
import apiCon from "./apiCon";

const loginAPI = apiCon(Method.Post, "/user/login");
const registerAPI = apiCon(Method.Post, "/user/register");
const getAllGraphAPI = apiCon(Method.Get, "/user/allGraph");
const getAllGraphByOrderAPI = apiCon(Method.Get, "/user/allGraphByOrder");
const getStarredGraphAPI = apiCon(Method.Get, "/user/starredGraph");
const renameGraphAPI = apiCon(Method.Post, "/user/graphRename");
const deleteGraphAPI = apiCon(Method.Get, "/user/deleteGraph");
const getEmailCodeAPI = apiCon(Method.Get, "/user/getEmailCode");
const changeUsernameAPI = apiCon(Method.Post, "user/changeUsername");
const changePasswordAPI = apiCon(Method.Post, "user/changePass");
const updateUserImgAPI = apiCon(Method.Post,"user/uploadImg",{},ContentType.Multipart);
const verifyEmailAPI = apiCon(Method.Post, "user/verifyEmail");
const forgetPasswordAPI = apiCon(Method.Post, "user/forgetPass");

export {
	loginAPI,
	registerAPI,
	getAllGraphAPI,
	getStarredGraphAPI,
	renameGraphAPI,
	deleteGraphAPI,
	getEmailCodeAPI,
	changeUsernameAPI,
	changePasswordAPI,
	updateUserImgAPI,
	getAllGraphByOrderAPI,
	verifyEmailAPI,
	forgetPasswordAPI
};
