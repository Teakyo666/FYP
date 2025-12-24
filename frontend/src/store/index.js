import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  state: () => ({
    role: localStorage.getItem("role") || "",
    id: localStorage.getItem("id") || "",
    name: localStorage.getItem('name') || '',
    avatar: localStorage.getItem('avatar') || ''
  }),

  actions: {
    // 登录：
    login(userInfo) {
      this.role = userInfo.role;
      this.id = userInfo.id;
      this.name = userInfo.name
      this.avatar = userInfo.avatar || ''
      localStorage.setItem("role", userInfo.role);
      localStorage.setItem("id", userInfo.id);
      localStorage.setItem('name', userInfo.name);
      localStorage.setItem('avatar', userInfo.avatar || '');
    },
    
    // 退出登录：清空role和id
    logout() {
      this.role = "";
      this.id = "";
      this.name = "";
      this.avatar = "";
      localStorage.clear();
    },

    // 更新用户信息（包括头像）
    updateUserInfo(userInfo) {
      if (userInfo.name) {
        this.name = userInfo.name;
        localStorage.setItem('name', userInfo.name);
      }
      if (userInfo.avatar !== undefined) {
        this.avatar = userInfo.avatar;
        localStorage.setItem('avatar', userInfo.avatar);
      }
    }
  }
});