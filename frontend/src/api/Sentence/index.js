// src/api/Sentence/index.js
import request from "@/utils/request";

export function CreateSentence(data) {
  return request({
    url: "sentence/create",
    method: "post",
    data: data,
  });
}

export function ListSentence(params) {
  return request({
    url: "sentence/list",
    method: "get",
    params: params,
  });
}

export function DeleteSentence(data) {
  return request({
    url: "sentence/delete",
    method: "post",
    params: data,
  });
}

export function UpdateSentence(data) {
  return request({
    url: "sentence/update",
    method: "post",
    data: data,
  });
}

export function GetSentence(params) {
  return request({
    url: "sentence/get",
    method: "get",
    params: params,
  });
}