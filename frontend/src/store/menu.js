// src/store/menu.js
import { defineStore } from "pinia"
import { menuConfig, getUserMenus } from "@/config/menu"

export const useMenuStore = defineStore("menu", {
  state: () => ({
    menus: [],
    collapsed: false
  }),

  getters: {
    // Get menus accessible to current user
    allowedMenus: (state) => state.menus,
    
    // Get default path (path of first menu item)
    defaultPath: (state) => {
      const firstMenu = state.menus.find(menu => menu.roles.includes(localStorage.getItem("role")))
      return firstMenu ? firstMenu.path : '/dashboard'
    }
  },

  actions: {
    // Initialize menus
    initMenus(role) {
      this.menus = getUserMenus(role)
    },
    
    // Toggle menu collapse state
    toggleCollapse() {
      this.collapsed = !this.collapsed
    },
    
    // Get menu item by path
    getMenuByPath(path) {
      return this.menus.find(menu => menu.path === path)
    }
  }
})