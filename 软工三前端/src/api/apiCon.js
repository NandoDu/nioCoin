import axios from "./request";

const Method = {Get: 0, Post: 1, Put: 2};
const ContentType = {Object: "application/json", String: "text/plain",Multipart: undefined};

const apiCon = (method, url, config = {}, contentType = ContentType.Object) => {
	config.headers = {
		"Content-Type": contentType,
		...config.headers
	};
	switch (method) {
		case Method.Get:
			return params => axios.get(url, {...config, params});
		case Method.Post:
			return (data, params) => axios.post(url, data, {...config, params});
		case Method.Put:
			return data => axios.put(url, data, config);
	}
};

export default apiCon;
export {Method, ContentType};
