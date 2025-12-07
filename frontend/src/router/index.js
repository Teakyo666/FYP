// src/router/index.js
import { createRouter, createWebHistory } from "vue-router"
import { useUserStore } from "@/store/index"
import { useMenuStore } from "@/store/menu"
import Dashboard from '@/views/Dashboard.vue'

// 1. 静态路由（永远存在）
const constantRoutes = [
  { path: "/", redirect: "/login" },

  { path: "/login",           component: () => import("@/views/Login/index.vue") },
  { path: "/register",        component: () => import("@/views/Register/index.vue") },
  { path: "/forget-password", component: () => import("@/views/ForgetPassword/index.vue") },
  { path: "/reset-password",  component: () => import("@/views/ResetPassword/index.vue") },

  // 唯一后台入口
  {
    path: "/dashboard",
    component: Dashboard,
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        component: () => import('@/views/dashboard/Home.vue')
      },
      {
        path: 'volunteer',
        component: () => import('@/views/volunteerdashboard/index.vue')
      },
      {
        path: 'recycler',
        component: () => import('@/views/recyclerdashboard/index.vue')
      },
      {
        path: 'customer',
        component: () => import('@/views/userdashboard/index.vue')
      },
      {
        path: 'profile',
        component: () => import('@/views/dashboard/Profile.vue')
      },
      {
        path: 'garbage',
        component: () => import('@/views/dashboard/Garbage/query.vue')
      },
      {
        path: 'admin/garbage',
        component: () => import('@/views/dashboard/admin/garbage.vue')
      },
      {
        path: 'admin/user',
        component: () => import('@/views/dashboard/admin/user.vue')
      },
    ]
  }
]

// 2. 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
})

// 3. 真正的数据驱动动态路由注册（只看 menu.js 就行！）
function registerDynamicRoutes() {
  const menuStore = useMenuStore()
  const userStore = useUserStore()

  // 只有登录了才注册
  if (!userStore.role) return

  // 初始化菜单
  menuStore.initMenus(userStore.role)

  // 遍历配置的所有菜单，根据当前角色能看到的自动注册路由
  menuStore.allowedMenus.forEach(menu => {
    // 防止重复注册（刷新页面也会走这里）
    if (router.hasRoute(menu.path)) return

    router.addRoute("dashboard", {
      path: menu.path,
      name: menu.name,
      component: () => import(`@/views/${menu.component}.vue`)
    })
  })

  // 注册一个终极兜底：任何没匹配到的子路径都跳转到当前角色默认页面
  router.addRoute("dashboard", {
    path: "/dashboard/:pathMatch(.*)*",
    redirect: () => menuStore.defaultPath
  })
}

// 4. 路由守卫
router.beforeEach((to, from, next) => {
  // Pinia 还没初始化（首次加载）
  if (!window.$pinia) {
    next()
    return
  }

  const userStore = useUserStore()
  const isLogin = !!userStore.role

  if (isLogin) {
    // 关键：登录后立刻把有权限的路由全部注册进去（只执行一次就够了）
    registerDynamicRoutes()

    // 已登录还去登录页？踢回后台
    if (to.path === "/login") {
      next("/dashboard")
    } else {
      next()
    }
  } else {
    // 未登录想访问需要登录的页面 → 去登录页
    if (to.meta.requiresAuth) {
      next("/login")
    } else {
      next()
    }
  }
})

export default router