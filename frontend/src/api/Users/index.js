// src/api/Users/index.js
import request from "@/utils/request";

export function User(data) {
  return request({
    url: "users/get",
    method: "get",
    params: data,
  });
}

export function UserUpdate(data) {
  return request({
    url: "users/update",
    method: "post",
    data: data,
  });
}

export function UserDelete(data) {
  return request({
    url: "users/delete",
    method: "post",
    data: data,
  });
}

export function UserList(data) {
  return request({
    url: "users/list",
    method: "get",
    params: data,
  });
}

export function UserRegister(data) {
  return request({
    url: "users/register",
    method: "post",
    data: data,
  });
}