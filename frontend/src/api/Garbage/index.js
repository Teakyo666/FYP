// src/api/Garbage/index.js
import request from "@/utils/request";

export function Garbage(data) {
  return request({
    url: "home/get",
    method: "get",
    params: data,
  });
}

export function CreateGarbage(data) {
  return request({
    url: "home/create",
    method: "post",
    data: data,
  });
}

export function UpdateGarbage(data) {
  return request({
    url: "home/update",
    method: "post",
    data: data,
  });
}

export function DeleteGarbage(data) {
  return request({
    url: "home/delete",
    method: "post",
    params: data,
  });
}

export function ListGarbage(params) {
  return request({
    url: "home/list",
    method: "get",
    params: params,
  });
}