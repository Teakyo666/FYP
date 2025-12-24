import request from "@/utils/request";

export function GetPlace(data) {
  return request({
    url: "place/get",
    method: "get",
    params: data,
  });
}
export function GetAllPlace(data) {
  return request({
    url: "place/getAll",
    method: "get",
    params: data,
  });
}
export function AddPlace(data) {
  return request({
    url: "place/create",
    method: "post",
    data: data,
  });
}
export function UpdatePlace(data) {
  return request({
    url: "place/update",
    method: "post",
    data: data,
  });
}
export function DeletePlace(data) {
  return request({
    url: "place/delete",
    method: "post",
    data: data,
  });
}

export function SearchPlace(data) {
  return request({
    url: "place/getByType",
    method: "get",
    params: data,
  });
}

export function ListPlace(data) {
  return request({
    url: "place/list",
    method: "get",
    params: data,
  });
}