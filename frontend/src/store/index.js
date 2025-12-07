import { defineStore } from "pinia";

export const useUserStore = defineStore("user", {
  state: () => ({
    role: localStorage.getItem("role") || "",
    id: localStorage.getItem("id") || "",
    name: localStorage.getItem('name') || ''
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
    },
    
    // 退出登录：清空role和id
    logout() {
      this.role = "";
      this.id = "";
      this.name = "";
      localStorage.clear();
    },

    //登录显示名称
    setName(name) {
      this.name = name
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      user.name = name
      localStorage.setItem('user', JSON.stringify(user))
}
  }
});