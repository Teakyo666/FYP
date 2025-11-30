// src/main.js（完整正确配置）
import { createApp } from "vue";
import { createPinia } from "pinia"; // 直接导入createPinia
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";

import App from "./App.vue";
import router from "./router";

// 1. 创建Pinia实例（必须先创建）
const pinia = createPinia();
const app = createApp(App);

// 2. 挂载顺序：先Pinia → 再路由 → 再ElementPlus
app.use(pinia);
app.use(router);
app.use(ElementPlus);

// 3. 挂载App
app.mount("#app");