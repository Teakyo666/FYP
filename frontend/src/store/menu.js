// src/store/menu.js
import { defineStore } from "pinia"
import { menuConfig, getUserMenus } from "@/config/menu"

export const useMenuStore = defineStore("menu", {
  state: () => ({
    menus: [],
    collapsed: false
  }),

  getters: {
    // 获取当前用户可访问的菜单
    allowedMenus: (state) => state.menus,
    
    // 获取默认路径（第一个菜单项的路径）
    defaultPath: (state) => {
      const firstMenu = state.menus.find(menu => menu.roles.includes(localStorage.getItem("role")))
      return firstMenu ? firstMenu.path : '/dashboard'
    }
  },

  actions: {
    // 初始化菜单
    initMenus(role) {
      this.menus = getUserMenus(role)
    },
    
    // 切换菜单折叠状态
    toggleCollapse() {
      this.collapsed = !this.collapsed
    },
    
    // 根据路径获取菜单项
    getMenuByPath(path) {
      return this.menus.find(menu => menu.path === path)
    }
  }
})