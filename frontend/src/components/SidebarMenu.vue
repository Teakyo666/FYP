<!-- src/components/SidebarMenu.vue -->
<template>
  <div class="sidebar-menu">
    <div class="logo">
      <div class="logo-icon">♻️</div>
      <div class="logo-text">
        <div class="logo-title">EcoMind</div>
        <div class="logo-subtitle">Green System</div>
      </div>
    </div>

    <div class="card-menu">
      <!-- 渲染所有菜单项 -->
      <div
        v-for="menu in allowedMenus"
        :key="menu.path"
        class="menu-card"
        :class="{ active: isActiveMenu(menu.path) }"
        @click="navigateTo(menu.path)"
      >
        <div class="menu-icon-wrapper">
          <el-icon>
            <component :is="getIconComponent(menu.icon)" />
          </el-icon>
        </div>
        <span class="menu-text">{{ menu.title }}</span>
        <div class="menu-indicator"></div>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="sidebar-footer">
      <div class="footer-decoration"></div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/index'
import { useMenuStore } from '@/store/menu'
import {
  House,
  HelpFilled,
  User,
  Search,
  Delete,
  Message,
  Location,
  Check,
  Edit,
} from '@element-plus/icons-vue'

// 导入图标组件
const icons = {
  House,
  HelpFilled,
  User,
  Search,
  Delete,
  Message,
  Location,
  Check,
  Edit,
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const menuStore = useMenuStore()

// 获取当前用户可访问的菜单
const allowedMenus = computed(() => {
  // 初始化菜单
  menuStore.initMenus(userStore.role)
  return menuStore.allowedMenus.filter(menu => 
    menu.roles.includes(userStore.role)
  )
})

// 判断菜单是否激活
const isActiveMenu = (menuPath) => {
  // 特殊处理根路径
  if (menuPath === '/dashboard' && (route.path === '/dashboard' || route.path === '/dashboard/')) {
    return true
  }
  return route.path === menuPath
}

// 导航到指定路径
const navigateTo = (path) => {
  router.push(path)
}

// 获取图标组件
const getIconComponent = (iconName) => {
  return icons[iconName] || Search // 默认返回Search图标，避免错误
}
</script>

<style scoped>
/* 侧边栏菜单样式 */
.sidebar-menu {
  width: 240px;
  height: 100vh;
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
}

/* Logo 优化 */
.logo {
  height: 80px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.03);
}

.logo-icon {
  font-size: 32px;
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.logo-title {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.logo-subtitle {
  color: rgba(255, 255, 255, 0.5);
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 卡片菜单容器 */
.card-menu {
  flex: 1;
  padding: 24px 0;
  overflow-y: auto;
}

.card-menu::-webkit-scrollbar {
  width: 4px;
}

.card-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
}

/* 菜单卡片 - 重新设计 */
.menu-card {
  position: relative;
  height: 56px;
  margin: 6px 16px;
  padding: 0 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.04);
  color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.menu-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  background: #409eff;
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.menu-card:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  transform: translateX(4px);
}

.menu-card:hover::before {
  transform: scaleY(1);
}

.menu-card.active {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: #fff;
}

.menu-card.active::before {
  transform: scaleY(1);
}

.menu-icon-wrapper {
  font-size: 18px;
  transition: transform 0.3s ease;
}

.menu-card:hover .menu-icon-wrapper {
  transform: scale(1.1);
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
  flex: 1;
  transition: all 0.3s ease;
}

.menu-indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.menu-card:hover .menu-indicator {
  opacity: 0.7;
}

.menu-card.active .menu-indicator {
  opacity: 1;
  background: #fff;
}

/* 底部装饰 */
.sidebar-footer {
  padding: 20px;
}

.footer-decoration {
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  border-radius: 1px;
}
</style>