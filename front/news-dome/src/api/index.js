import request from "./axios";

export const apiPost = (url, data) => request({
  url,
  method: "POST",
  data
});

export const apiGet = (url, params) => request({
  url,
  method: "GET",
  params
});

export const apiLogin = data => request({
  url: 'renren-fast/app/login',
  method: 'POST',
  data
});
