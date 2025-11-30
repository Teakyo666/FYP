<template>
  <div class="reset-password-page">
    <div class="reset-password-card">
      <div class="reset-password-header">
        <h2 class="reset-password-title">重置密码</h2>
        <p class="reset-password-subtitle">输入Token和新密码，完成密码重置</p>
      </div>

      <form class="reset-password-form" @submit.prevent="handleResetPassword">
        <!-- Token输入框：绑定resetToken -->
        <div class="form-item">
          <input
            type="text"
            v-model="resetForm.resetToken"
            placeholder="请输入重置Token"
            class="reset-password-input"
            :class="{ 'input-focus': tokenFocus }"
            @focus="tokenFocus = true"
            @blur="tokenFocus = false"
            required
          />
          <p class="error-msg" v-if="tokenError">{{ tokenError }}</p>
        </div>

        <!-- 新密码输入框 -->
        <div class="form-item">
          <input
            type="password"
            v-model="resetForm.newPassword"
            placeholder="请输入新密码（不少于6位）"
            class="reset-password-input"
            :class="{ 'input-focus': passwordFocus }"
            @focus="passwordFocus = true"
            @blur="passwordFocus = false"
            required
          />
          <p class="error-msg" v-if="passwordError">{{ passwordError }}</p>
        </div>

        <button type="submit" class="reset-password-btn" :disabled="isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          确认重置密码
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { resetPassword } from "@/api/login";

const router = useRouter();
const route = useRoute();

// 表单字段：resetToken（匹配后端） + newPassword
const resetForm = ref({
  resetToken: "",
  newPassword: "",
});

// 输入框聚焦状态
const tokenFocus = ref(false);
const passwordFocus = ref(false);

// 错误提示
const tokenError = ref("");
const passwordError = ref("");

// 按钮加载状态
const isLoading = ref(false);

// 页面挂载：从路由参数token → 赋值给表单resetToken
onMounted(() => {
  // 路由参数是token，赋值给表单的resetToken
  const routeToken = route.query.token;
  if (routeToken) {
    resetForm.value.resetToken = routeToken; // 关键：把?token=xxx赋值给resetToken
    console.log("从路由获取的Token：", routeToken); // 调试用，可删除
  } else {
    alert("请先申请重置Token！");
    router.push("/apply-token");
  }
});

// 表单校验：校验resetToken和newPassword
const validateForm = () => {
  let isValid = true;
  const { resetToken, newPassword } = resetForm.value;

  // 校验resetToken
  if (!resetToken.trim()) {
    tokenError.value = "请输入重置Token";
    isValid = false;
  } else {
    tokenError.value = "";
  }

  // 校验新密码
  if (!newPassword.trim()) {
    passwordError.value = "请输入新密码";
    isValid = false;
  } else if (newPassword.length < 6) {
    passwordError.value = "新密码长度不少于6位";
    isValid = false;
  } else {
    passwordError.value = "";
  }

  return isValid;
};

// 重置密码核心逻辑：传resetToken（匹配后端）
const handleResetPassword = async () => {
  try {
    if (!validateForm()) return;
    isLoading.value = true;

    // 调用后端接口：传resetToken + newPassword（完全匹配后端要求）
    const res = await resetPassword({
      resetToken: resetForm.value.resetToken,
      newPassword: resetForm.value.newPassword,
    });

    if (res.success) {
      alert("密码重置成功！即将跳转到登录页～");
      router.push("/login");
    } else {
      alert(res.message || "Token无效或已过期，请重新申请！");
    }
  } catch (error) {
    console.error("重置密码错误详情：", error);
    // 精准区分错误类型
    if (error.message.includes("Network Error") || error.message.includes("timeout")) {
      alert("网络异常，请检查后端是否启动！");
    } else {
      alert(`重置密码失败：${error.message}`);
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 样式部分保持不变，无需修改 */
.reset-password-page {
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

.reset-password-page::before {
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
.reset-password-page::after {
  content: "";
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  bottom: 10%;
  right: 20%;
  filter: blur(50px);
}

.reset-password-card {
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

.reset-password-header {
  text-align: center;
  margin-bottom: 35px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}
.reset-password-title {
  font-size: 26px;
  font-weight: 600;
  color: #333;
  margin: 0;
  letter-spacing: 0.5px;
}
.reset-password-subtitle {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
  line-height: 1.4;
}

.reset-password-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.reset-password-input {
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
.input-focus {
  border: double 1px transparent;
  background-image: linear-gradient(white, white),
                    linear-gradient(120deg, #667eea, #764ba2);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  box-shadow: 0 0 12px rgba(102, 126, 234, 0.25);
}
.reset-password-input::placeholder {
  color: #999;
  font-size: 15px;
}

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

.reset-password-btn {
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
.reset-password-btn:hover:not(:disabled) {
  background: linear-gradient(120deg, #5a6fe0 0%, #6a4298 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}
.reset-password-btn:disabled {
  background: linear-gradient(120deg, #c5cce8 0%, #d1c0e0 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

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

@media (max-width: 480px) {
  .reset-password-card {
    padding: 35px 25px;
    margin: 0 15px;
  }
  .reset-password-title {
    font-size: 24px;
  }
  .reset-password-input {
    height: 48px;
    padding: 0 16px;
  }
  .reset-password-btn {
    height: 50px;
    font-size: 16px;
  }
}
</style>