<template>
  <div class="dashboard">
    <div class="header">
      <h1>回收人员仪表盘</h1>
      <el-button type="primary" @click="handleLogout">退出登录</el-button>
    </div>
    <div class="content">
      <h2>Hello 回收人员！</h2>
      <p>ID：{{ userStore.id }}</p>
      <p>Role：{{ userStore.role }}</p>
      <el-button type="warning" size="large" @click="handleClick">回收人员专属按钮</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/index";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();

const handleLogout = () => {
  userStore.logout();
  router.push("/login");
  ElMessage.success("退出成功");
};

const handleClick = () => {
  ElMessage.info(`回收人员${userStore.id}按钮被点击！`);
};
</script>