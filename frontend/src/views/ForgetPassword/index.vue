<template>
  <div class="apply-token-page">
    <div class="apply-token-card">
      <div class="apply-token-header">
        <h2 class="apply-token-title">申请重置密码Token</h2>
        <p class="apply-token-subtitle">输入注册邮箱，获取重置密码凭证</p>
      </div>

      <!-- 申请Token表单 -->
      <form class="apply-token-form" @submit.prevent="handleApplyToken">
        <!-- 邮箱输入框 -->
        <div class="form-item">
          <input
            type="email"
            v-model="tokenForm.email"
            placeholder="请输入注册邮箱"
            class="apply-token-input"
            :class="{ 'input-focus': emailFocus }"
            @focus="emailFocus = true"
            @blur="emailFocus = false"
            required
          />
          <p class="error-msg" v-if="emailError">{{ emailError }}</p>
        </div>

        <!-- 申请Token按钮 -->
        <button type="submit" class="apply-token-btn" :disabled="isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          申请重置Token
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
// 导入申请Token接口（在login.js中扩展）
import { applyToken } from "@/api/login";

const router = useRouter();

// 表单数据：仅邮箱
const tokenForm = ref({
  email: "",
});

// 输入框聚焦状态（动效控制）
const emailFocus = ref(false);

// 表单错误提示
const emailError = ref("");

// 按钮加载状态
const isLoading = ref(false);

// 表单校验（仅校验邮箱）
const validateForm = () => {
  let isValid = true;
  const { email } = tokenForm.value;

  // 邮箱格式校验
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!email.trim()) {
    emailError.value = "请输入注册邮箱";
    isValid = false;
  } else if (!emailReg.test(email)) {
    emailError.value = "请输入有效的邮箱格式（例：xxx@xxx.com）";
    isValid = false;
  } else {
    emailError.value = "";
  }

  return isValid;
};

// 核心：申请Token逻辑（仅修改跳转参数：email→token）
const handleApplyToken = async () => {
  try {
    // 1. 前端表单校验
    if (!validateForm()) return;

    // 2. 显示加载状态
    isLoading.value = true;

    // 3. 调用后端申请Token接口（传邮箱）
    const res = await applyToken({
      username: tokenForm.value.email, // 后端接收字段名（和注册一致）
    });

    // 4. 接口成功：提取token并跳转（核心修改：传token而非email）
    if (res.success) {
      // 提取后端返回的token
      const token = res.data?.token;
      if (!token) {
        alert("Token申请失败：后端未返回重置凭证！");
        isLoading.value = false;
        return;
      }
      alert("Token申请成功！即将跳转到重置密码页～");
      // 跳转并携带token参数（替换原来的email参数）
      router.push({
        path: "/reset-password",
        query: { token: token }, // 关键修改：传token
      });
    } else {
      alert(res.message || "Token申请失败，请检查邮箱是否正确！");
    }
  } catch (error) {
    console.error("申请Token接口调用失败：", error);
    alert("网络异常，请检查后端是否启动！");
  } finally {
    // 关闭加载状态
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 样式完全保留，无任何修改 */
.apply-token-page {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  width: 100% !important;
  height: 100% !important;
  background: linear-gradient(120deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 背景装饰点（增加层次感） */
.apply-token-page::before {
  content: "";
  position: absolute;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  top: 10%;
  left: 20%;
  filter: blur(50px);
}
.apply-token-page::after {
  content: "";
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  bottom: 10%;
  right: 20%;
  filter: blur(60px);
}

/* 卡片容器（悬浮质感） */
.apply-token-card {
  width: 100%;
  max-width: 450px;
  padding: 45px 35px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 2;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 标题区域 */
.apply-token-header {
  text-align: center;
  margin-bottom: 35px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}
.apply-token-title {
  font-size: 26px;
  font-weight: 600;
  color: #333;
  margin: 0;
  letter-spacing: 0.5px;
}
.apply-token-subtitle {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
  line-height: 1.4;
}

/* 表单容器 */
.apply-token-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 表单项 */
.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 输入框样式（和登录页一致） */
.apply-token-input {
  height: 52px;
  padding: 0 20px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  font-size: 16px;
  color: #333;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
  background: rgba(255, 255, 255, 0.8);
}
/* 输入框聚焦动效 */
.input-focus {
  border: double 1px transparent;
  background-image: linear-gradient(white, white),
                    linear-gradient(120deg, #667eea, #764ba2);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  box-shadow: 0 0 12px rgba(102, 126, 234, 0.25);
}
/* 占位符样式 */
.apply-token-input::placeholder {
  color: #999;
  font-size: 15px;
}

/* 错误提示 */
.error-msg {
  font-size: 12px;
  color: #e53e3e;
  margin: 0;
  height: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}
.error-msg::before {
  content: "!";
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #e53e3e;
  color: white;
  font-size: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 申请按钮（和登录按钮样式一致） */
.apply-token-btn {
  width: 100%;
  height: 54px;
  border-radius: 10px;
  font-size: 17px;
  font-weight: 500;
  background: linear-gradient(120deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}
.apply-token-btn:hover:not(:disabled) {
  background: linear-gradient(120deg, #5a6fe0 0%, #6a4298 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}
.apply-token-btn:disabled {
  background: linear-gradient(120deg, #c5cce8 0%, #d1c0e0 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 加载动画 */
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-top: 2px solid #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 移动端适配 */
@media (max-width: 480px) {
  .apply-token-card {
    padding: 35px 25px;
    margin: 0 15px;
  }
  .apply-token-title {
    font-size: 24px;
  }
  .apply-token-input {
    height: 48px;
    padding: 0 16px;
  }
  .apply-token-btn {
    height: 50px;
    font-size: 16px;
  }
}
</style>