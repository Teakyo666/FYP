<template>
  <div class="dashboard">
    <div class="header">
      <h1>管理员仪表盘</h1>
      <el-button type="primary" @click="handleLogout">退出登录</el-button>
    </div>
    <div class="content">
      <h2>Hello 管理员！</h2>
      <!-- 动态显示ID（从Pinia获取） -->
      <p>ID：{{ userStore.id }}</p>
      <p>Role：{{ userStore.role }}</p>
      <el-button type="danger" size="large" @click="handleClick">管理员专属按钮</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/index";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore(); // 直接获取存储的role和id

const handleLogout = () => {
  userStore.logout();
  router.push("/login");
  ElMessage.success("退出成功");
};

const handleClick = () => {
  ElMessage.info(`管理员${userStore.id}按钮被点击！`);
};
</script>

<style scoped>
.dashboard {
  width: 100%;
  height: 100vh;
  padding: 30px;
  background: #f5f7fa;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 50px;
}
.header h1 {
  color: #e74c3c;
  font-size: 28px;
}
.content {
  text-align: center;
  margin-top: 100px;
}
.content h2 {
  font-size: 32px;
  margin-bottom: 20px;
}
.content p {
  font-size: 20px;
  color: #333;
  margin: 10px 0;
}
.content .el-button {
  margin-top: 30px;
  width: 200px;
  height: 60px;
  font-size: 18px;
}
</style>