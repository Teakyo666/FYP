import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  state: () => ({
    role: localStorage.getItem("role") || "", // 角色
    id: localStorage.getItem("id") || ""      // 新增：用户ID
  }),
  actions: {
    // 登录：存储role和id
    login(userInfo) {
      this.role = userInfo.role;
      this.id = userInfo.id;
      localStorage.setItem("role", userInfo.role);
      localStorage.setItem("id", userInfo.id);
    },
    // 退出登录：清空role和id
    logout() {
      this.role = "";
      this.id = "";
      localStorage.clear();
    }
  }
});