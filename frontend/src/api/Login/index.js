import request from "@/utils/request";

export function login(data) {
  return request({
    url: "user/login",
    method: "post",
    data: data,
  });
}

export function register(data) {
  return request({
    url: "user/create",
    method: "post",
    data: data,
  });
}

export function applyToken(data) {
  return request({
    url: "user/applyToken",
    method: "post",
    data: data,
  });
}
export function resetPassword(data) {
  return request({
    url: "user/resetPassword",
    method: "post",
    data: data,
  });
}