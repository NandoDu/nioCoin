import axios from "axios";
import {ElMessage} from "element-plus";

const instance = axios.create({
    // baseURL: "http://118.195.135.53:9000/api",
    baseURL: "http://localhost:9000/api",
    timeout: 50000
});

//请求拦截
instance.interceptors.request.use(
    config => config,
    err => console.log(err)
);

//响应拦截
instance.interceptors.response.use(
    res => {
        let data = res.data;
        if (data.success) {
            return data.data;
        } else {
            console.log(data.msg);
            ElMessage({
                message: data.msg,
                type: "error",
                center: true
            });
            return Promise.reject(data.msg);
        }
    },
    err => {
        console.log(`[error] axios ${err}`);
        return Promise.reject(err);
    }
);

export default instance;
