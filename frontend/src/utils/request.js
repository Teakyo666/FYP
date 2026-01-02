// src/utils/request.js
import axios from "axios";

// Create axios instance
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000, // Request timeout
  headers: {
    "Content-Type": "application/json;charset=utf-8",
  },
});

service.interceptors.request.use(
  (config) => {
    return config; // Return configuration directly, no additional processing
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor: Unified processing of results (success: true/false)
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    // Backend success indicator: success: true
    if (res.success) {
      return res; // Return backend data directly, use res.data in page
    } else {
      // Automatically prompt error on failure
      // No longer use alert to display error, switch to silent processing
      return Promise.reject(res);
    }
  },
  (error) => {
    // No longer display network exception prompt, switch to silent processing
    return Promise.reject(error);
  }
);

export default service;