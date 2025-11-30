import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/index"; // 导入Pinia获取登录状态

// 公共路由（保留你所有原有路由）
export const constantRoutes = [
  {
    path: "/",
    redirect: "/login" 
  },
  {
    path: "/login",
    component: () => import("@/views/Login/index.vue"),
    hidden: true,
  },
  {
    path: "/register",
    component: () => import("@/views/Register/index.vue"),
    hidden: true,
  },
  {
    path: "/forget-password",
    component: () => import("@/views/ForgetPassword/index.vue"),
    hidden: true,
  },
  {
    path: "/reset-password",
    component: () => import("@/views/ResetPassword/index.vue"),
    hidden: true,
  },
  // 新增：4个角色专属仪表盘路由（核心）
  {
    path: "/admindashboard",
    component: () => import("@/views/admindashboard/index.vue"),
    hidden: true, // 和原有路由保持一致的hidden属性
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: "/volunteerdashboard",
    component: () => import("@/views/volunteerdashboard/index.vue"),
    hidden: true,
    meta: { requiresAuth: true }
  },
  {
    path: "/recyclerdashboard",
    component: () => import("@/views/recyclerdashboard/index.vue"),
    hidden: true,
    meta: { requiresAuth: true }
  },
  {
    path: "/customerdashboard",
    component: () => import("@/views/userdashboard/index.vue"),
    hidden: true,
    meta: { requiresAuth: true }
  }
];

// 创建路由实例（保留原有配置）
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 }),
});

// 新增：路由守卫（校验登录状态，不影响原有路由）
router.beforeEach((to, from, next) => {
  // 跳过Pinia未初始化的情况（首次加载）
  if (!window.$pinia) {
    next();
    return;
  }

  const userStore = useUserStore();
  const isAuthenticated = !!userStore.role; // 有role即已登录

  // 仅校验需要登录的仪表盘路由
  if (to.meta.requiresAuth) {
    if (isAuthenticated) {
      next(); // 已登录：正常访问
    } else {
      next("/login"); // 未登录：跳登录页
    }
  } else {
    next(); // 公共路由（登录/注册等）：直接放行
  }
});

// 修复Pinia在路由守卫中提前初始化的问题（关键）
router.beforeEach((to, from, next) => {
  if (!window.$pinia) {
    // 延迟执行，确保Pinia已挂载
    setTimeout(() => {
      next();
    }, 0);
  } else {
    next();
  }
});

export default router;