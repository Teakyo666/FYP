// src/api/Apply/index.js
import request from "@/utils/request";

export const ListApply = (params) => {
  return request({
    url: "apply/list",
    method: "get",
    params:params,
  });
};

export const DeleteApply = (data) => {
  return request({
    url: "apply/delete",
    method: "post",
    params:data,
  });
};

export const UpdateApply = (data) => {
  return request({
    url: "apply/update",
    method: "post",
    data:data,
  });
};

export const CreateApply = (data) => {
  return request({
    url: "apply/create",
    method: "post",
    data:data,
  });
};