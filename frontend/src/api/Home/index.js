// src/api/Home/index.js
import request from "@/utils/request";

export function sentence(data) {
  return request({
    url: "sentence/get",
    method: "get",
    params: data,
  });
}

export function GetProfile(data) {
  return request({
    url: "userInfo/get",
    method: "get",
    params: data,
  });
}
export function UpdateProfile(data) {
  return request({
    url: "userInfo/update",
    method: "post",
    data: data,
  });
}

