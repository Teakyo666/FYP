<!-- src/views/Dashboard.vue -->
<template>
  <el-container class="full-layout">
    <!-- Left card-style menu -->
    <el-aside width="240px" class="sidebar">
      <SidebarMenu />
    </el-aside>

    <!-- Right main content -->
    <el-container>
      <el-header height="70px" class="top-header">
        <div class="header-left">
          <div class="breadcrumb">
            <el-icon class="breadcrumb-icon"><Location /></el-icon>
            <span class="breadcrumb-text">{{ getCurrentPageName() }}</span>
          </div>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="notification-badge">
            <el-icon class="header-icon"><Bell /></el-icon>
          </el-badge>
          <el-dropdown trigger="click">
            <div class="user-info">
              <el-avatar 
                :src="userStore.avatar || defaultAvatar" 
                size="large"
                class="user-avatar"
              />
              <div class="user-details">
                <span class="username">{{ userStore.name || userStore.role || 'Guest' }}</span>
                <span class="user-role">{{ getRoleText() }}</span>
              </div>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown">
                <el-dropdown-item @click="router.push('/dashboard/add-apply')">
                  <el-icon><Plus /></el-icon>
                  Apply for permissions 
                </el-dropdown-item>
                <el-dropdown-item @click="router.push('/dashboard/profile')">
                  <el-icon><User /></el-icon>
                  Profile
                </el-dropdown-item>
                <el-dropdown-item @click="handleLogout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  Logout
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/index'
import { ElMessageBox, ElMessage } from 'element-plus'
import SidebarMenu from '@/components/SidebarMenu.vue'
import {
  Location,
  Bell,
  SwitchButton,
  User,
  Plus
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = 'https://cube.elemecdn.com/0/88/83b7c0bd1.png'

// Login check
onMounted(() => {
  if (!userStore.token && !userStore.role) {
    router.replace('/login')
  }
})

// Get current page name
const getCurrentPageName = () => {
  const pathMap = {
    '/dashboard': 'Dashboard',
    '/dashboard/admin': 'Admin Center',
    '/dashboard/volunteer': 'Volunteer Center',
    '/dashboard/recycler': 'Recycler Center',
    '/dashboard/customer': 'Personal Center',
    '/dashboard/profile': 'Profile',
  }
  return pathMap[route.path] || 'Dashboard'
}

// Get role text
const getRoleText = () => {
  const roleMap = {
    'admin': 'System Administrator',
    'volunteer': 'Volunteer',
    'recycler': 'Recycler',
    'customer': 'Regular User',
  }
  return roleMap[userStore.role] || 'Guest'
}

const handleLogout = () => {
  ElMessageBox.confirm('Are you sure you want to logout?', 'Logout Confirmation', { 
    type: 'warning',
    confirmButtonText: 'Confirm Logout',
    cancelButtonText: 'Cancel',
  })
    .then(() => {
      userStore.logout?.()
      ElMessage.success('Logged out successfully')
      router.replace('/login')
    })
}
</script>

<style scoped>
/* 全局重置 */
* { 
  margin: 0; 
  padding: 0; 
  box-sizing: border-box; 
}

.full-layout, .el-container { 
  height: 100vh !important; 
  overflow: hidden; 
}

/* 侧边栏 - 增强设计 */
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 240px;
  height: 100vh;
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  z-index: 1000;
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
  font-weight: 600;
  transform: translateX(0);
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.4);
}

.menu-card.active::before {
  transform: scaleY(1);
  background: #fff;
}

.menu-icon-wrapper {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  transition: all 0.3s ease;
}

.menu-card.active .menu-icon-wrapper {
  background: rgba(255, 255, 255, 0.2);
}

.menu-icon-wrapper .el-icon {
  font-size: 20px;
}

.menu-text {
  flex: 1;
  font-size: 15px;
  letter-spacing: 0.3px;
}

.menu-indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: transparent;
  transition: all 0.3s ease;
}

.menu-card.active .menu-indicator {
  background: #fff;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.6);
}

/* 侧边栏底部装饰 */
.sidebar-footer {
  height: 60px;
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.footer-decoration {
  height: 4px;
  background: linear-gradient(90deg, #409eff, #67c23a, #409eff);
  border-radius: 2px;
  background-size: 200% 100%;
  animation: shimmer 3s linear infinite;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Header 优化 */
.top-header {
  background: #fff;
  height: 70px !important;
  margin-left: 240px;
  padding: 0 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  border-radius: 8px;
}

.breadcrumb-icon {
  color: #409eff;
  font-size: 18px;
}

.breadcrumb-text {
  color: #303133;
  font-weight: 600;
  font-size: 15px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.notification-badge {
  cursor: pointer;
}

.header-icon {
  font-size: 22px;
  color: #606266;
  transition: all 0.3s ease;
  cursor: pointer;
}

.header-icon:hover {
  color: #409eff;
  transform: scale(1.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: linear-gradient(135deg, #e8eef5 0%, #dce4f0 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.user-avatar {
  border: 2px solid #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  color: #303133;
  font-weight: 600;
  font-size: 15px;
}

.user-role {
  color: #909399;
  font-size: 12px;
}

/* 主内容区 */
.main-content {
  margin-left: 240px;
  height: calc(100vh - 70px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  padding: 24px;
  overflow-y: auto;
}

.main-content::-webkit-scrollbar {
  width: 8px;
}

.main-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.main-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 自定义下拉菜单 */
.custom-dropdown {
  padding: 8px;
}

.custom-dropdown .el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.custom-dropdown .el-dropdown-menu__item:hover {
  background: #f5f7fa;
  color: #409eff;
}
</style>