// src/utils/request.js
import axios from "axios";

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000, // 超时时间
  headers: {
    "Content-Type": "application/json;charset=utf-8",
  },
});

service.interceptors.request.use(
  (config) => {
    return config; // 直接返回配置，不做额外处理
  },
  (error) => Promise.reject(error)
);

// 响应拦截器：统一处理结果（适配你后端的 success: true/false）
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    // 后端成功标识：success: true
    if (res.success) {
      return res; // 直接返回后端数据，页面里直接用 res.data
    } else {
      // 失败时自动提示错误
      alert(res.message || "操作失败");
      return Promise.reject(res);
    }
  },
  (error) => {
    console.error("接口请求失败：", error);
    alert("网络异常，请稍后再试");
    return Promise.reject(error);
  }
);

export default service;