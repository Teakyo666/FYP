// src/router/index.js
import { createRouter, createWebHistory } from "vue-router"
import { useUserStore } from "@/store/index"
import { useMenuStore } from "@/store/menu"
import Dashboard from '@/views/Dashboard.vue'

// 1. Static routes (always exist)
const constantRoutes = [
  { path: "/", redirect: "/login" },

  { path: "/login",           component: () => import("@/views/Login/index.vue") },
  { path: "/register",        component: () => import("@/views/Register/index.vue") },
  { path: "/forget-password", component: () => import("@/views/ForgetPassword/index.vue") },
  { path: "/reset-password",  component: () => import("@/views/ResetPassword/index.vue") },

  // Single dashboard entry
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
        path: 'profile',
        component: () => import('@/views/dashboard/Profile.vue')
      },
      {
        path: 'admin/apply',
        component: () => import('@/views/dashboard/admin/apply.vue')
      },
      {
        path: 'add-apply',
        component: () => import('@/views/dashboard/AddApply.vue')
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
      {
        path: 'admin/AI',
        component: () => import('@/views/dashboard/admin/AI.vue')
      },
      {
        path: 'garbage/sentence',
        component: () => import('@/views/dashboard/Garbage/sentence.vue')
      },
      {
        path: 'garbage/place',
        component: () => import('@/views/dashboard/Garbage/place.vue')
      },
      {
        path: 'garbage/placecheck',
        component: () => import('@/views/dashboard/Garbage/placecheck.vue')
      },

    ]
  }
]

// 2. Create router instance
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 })
})

// 3. True data-driven dynamic route registration (just look at menu.js!)
function registerDynamicRoutes() {
  const menuStore = useMenuStore()
  const userStore = useUserStore()

  // Only register after login
  if (!userStore.role) return

  // Initialize menus
  menuStore.initMenus(userStore.role)

  // Iterate through all configured menus, automatically register routes based on what the current role can see
  menuStore.allowedMenus.forEach(menu => {
    // Prevent duplicate registration (also happens on page refresh)
    if (router.hasRoute(menu.path)) return

    router.addRoute("dashboard", {
      path: menu.path,
      name: menu.name,
      component: () => import(`@/views/${menu.component}.vue`)
    })
  })

  // Register a final fallback: redirect any unmatched sub-paths to the current role's default page
  router.addRoute("dashboard", {
    path: "/dashboard/:pathMatch(.*)*",
    redirect: () => menuStore.defaultPath
  })
}

// 4. Route guard
router.beforeEach((to, from, next) => {
  // Pinia not initialized yet (first load)
  if (!window.$pinia) {
    next()
    return
  }

  const userStore = useUserStore()
  const isLogin = !!userStore.role

  if (isLogin) {
    // Key: register all authorized routes immediately after login (only execute once is enough)
    registerDynamicRoutes()

    // Trying to go to login page when already logged in? Redirect back to dashboard
    if (to.path === "/login") {
      next("/dashboard")
    } else {
      next()
    }
  } else {
    // Trying to access pages requiring login when not logged in → go to login page
    if (to.meta.requiresAuth) {
      next("/login")
    } else {
      next()
    }
  }
})

export default router