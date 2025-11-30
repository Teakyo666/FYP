<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <h2 class="register-title">账号注册</h2>
        <p class="register-subtitle">填写以下信息，快速创建账号</p>
      </div>

      <!-- 注册表单 -->
      <form class="register-form" @submit.prevent="handleRegister">
        <!-- 邮箱输入框 -->
        <div class="form-item">
          <input
            type="email"
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            class="register-input"
            :class="{ 'input-focus': emailFocus }"
            @focus="emailFocus = true"
            @blur="emailFocus = false"
            required
          />
          <p class="error-msg" v-if="emailError">{{ emailError }}</p>
        </div>

        <!-- 密码输入框 -->
        <div class="form-item">
          <input
            type="password"
            v-model="registerForm.password"
            placeholder="请输入密码（不少于6位）"
            class="register-input"
            :class="{ 'input-focus': passwordFocus }"
            @focus="passwordFocus = true"
            @blur="passwordFocus = false"
            required
          />
          <p class="error-msg" v-if="passwordError">{{ passwordError }}</p>
        </div>

        <!-- 二次密码输入框 -->
        <div class="form-item">
          <input
            type="password"
            v-model="registerForm.confirmPassword"
            placeholder="请再次输入密码"
            class="register-input"
            :class="{ 'input-focus': confirmPasswordFocus }"
            @focus="confirmPasswordFocus = true"
            @blur="confirmPasswordFocus = false"
            required
          />
          <p class="error-msg" v-if="confirmPasswordError">{{ confirmPasswordError }}</p>
        </div>

        <!-- 注册按钮 + 跳转登录 -->
        <div class="register-actions">
          <button type="submit" class="register-btn" :disabled="isLoading">
            <span v-if="isLoading" class="loading-spinner"></span>
            立即注册
          </button>
          <div class="login-link">
            已有账号？<span @click="toLogin" class="link-text">立即登录</span>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
// 导入注册接口（后续创建）
import { register } from "@/api/login";

const router = useRouter();

// 表单数据：邮箱、密码、二次密码
const registerForm = ref({
  email: "",
  password: "",
  confirmPassword: "",
});

// 输入框聚焦状态（动效控制）
const emailFocus = ref(false);
const passwordFocus = ref(false);
const confirmPasswordFocus = ref(false);

// 表单错误提示
const emailError = ref("");
const passwordError = ref("");
const confirmPasswordError = ref("");

// 注册按钮加载状态
const isLoading = ref(false);

// 表单校验逻辑
const validateForm = () => {
  let isValid = true;
  const { email, password, confirmPassword } = registerForm.value;

  // 校验邮箱
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!email.trim()) {
    emailError.value = "请输入邮箱";
    isValid = false;
  } else if (!emailReg.test(email)) {
    emailError.value = "请输入有效的邮箱格式（例：xxx@xxx.com）";
    isValid = false;
  } else {
    emailError.value = "";
  }

  // 校验密码
  if (!password.trim()) {
    passwordError.value = "请输入密码";
    isValid = false;
  } else if (password.length < 6) {
    passwordError.value = "密码长度不少于6位";
    isValid = false;
  } else {
    passwordError.value = "";
  }

  // 校验二次密码
  if (!confirmPassword.trim()) {
    confirmPasswordError.value = "请再次输入密码";
    isValid = false;
  } else if (confirmPassword !== password) {
    confirmPasswordError.value = "两次输入的密码不一致";
    isValid = false;
  } else {
    confirmPasswordError.value = "";
  }

  return isValid;
};

// 核心注册逻辑
const handleRegister = async () => {
  try {
    // 1. 前端表单校验
    if (!validateForm()) return;

    // 2. 显示加载状态
    isLoading.value = true;

    // 3. 调用注册接口（仅传邮箱和密码，二次密码仅前端校验）
    const res = await register({
      username: registerForm.value.email,
      password: registerForm.value.password,
    });

    // 4. 接口成功处理
    if (res.success) {
      alert("注册成功！即将跳转到登录页～");
      router.push("/login"); // 注册成功跳登录页
    } else {
      // 接口返回失败（如邮箱已被注册）
      alert(res.message || "注册失败，请稍后重试！");
    }
  } catch (error) {
    // 网络异常/接口调用失败
    console.error("注册接口调用失败：", error);
    alert("网络异常，请检查后端是否启动！");
  } finally {
    // 关闭加载状态
    isLoading.value = false;
  }
};

// 跳转到登录页
const toLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
/* 注册页全屏容器（和登录页一致的渐变背景） */
.register-page {
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

/* 背景装饰点（和登录页一致） */
.register-page::before {
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
.register-page::after {
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

/* 注册卡片（和登录页一致的悬浮质感） */
.register-card {
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

/* 注册标题区域 */
.register-header {
  text-align: center;
  margin-bottom: 35px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}
.register-title {
  font-size: 26px;
  font-weight: 600;
  color: #333;
  margin: 0;
  letter-spacing: 0.5px;
}
.register-subtitle {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
  line-height: 1.4;
}

/* 表单容器 */
.register-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 单个表单项 */
.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 输入框样式（和登录页一致） */
.register-input {
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
/* 输入框聚焦动效（和登录页一致） */
.input-focus {
  border: double 1px transparent;
  background-image: linear-gradient(white, white),
                    linear-gradient(120deg, #667eea, #764ba2);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  box-shadow: 0 0 12px rgba(102, 126, 234, 0.25);
}
/* 输入框占位符 */
.register-input::placeholder {
  color: #999;
  font-size: 15px;
}

/* 错误提示文字（和登录页一致） */
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

/* 注册按钮 + 跳转登录区域 */
.register-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 10px;
}

/* 注册按钮（和登录页按钮样式一致） */
.register-btn {
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
.register-btn:hover:not(:disabled) {
  background: linear-gradient(120deg, #5a6fe0 0%, #6a4298 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}
.register-btn:disabled {
  background: linear-gradient(120deg, #c5cce8 0%, #d1c0e0 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
.register-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
}

/* 加载动画（和登录页一致） */
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

/* 跳转登录链接 */
.login-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}
.link-text {
  color: #667eea;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.3s ease;
}
.link-text:hover {
  color: #764ba2;
  text-decoration: underline;
}

</style>