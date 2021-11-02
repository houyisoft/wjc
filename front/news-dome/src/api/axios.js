import axios from "axios";
import cookie from "vue-cookies";
import { Message } from 'element-ui';

axios.defaults.baseURL = "http://39.102.55.84:8081/";

// HTTP request 拦截  d
axios.interceptors.request.use(
  async config => {
    // 请求头部添加 token 信息
    config.headers["token"] = cookie.get("newsToken") || sessionStorage.newsToken || null;;
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// HTTP response 拦截
axios.interceptors.response.use(
  async response => {
    // 接口出错时进行提示
    if (response.data.code == 500) Message.warning(response.data.msg);
    return response;
  },
  error => {
    return Promise.reject(error);
  }
);

export default axios;
