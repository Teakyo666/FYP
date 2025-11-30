<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h2 class="login-title">系统登录</h2>
        <p class="login-subtitle">欢迎回来，请输入账号信息</p>
      </div>

      <!-- 登录表单 -->
      <form class="login-form" @submit.prevent="handleLogin">
        <!-- 用户名输入框 -->
        <div class="form-item">
          <input
            type="text"
            v-model="loginForm.username"
            placeholder="请输入用户名/手机号"
            class="login-input"
            :class="{ 'input-focus': usernameFocus }"
            @focus="usernameFocus = true"
            @blur="usernameFocus = false"
            required
          />
          <p class="error-msg" v-if="usernameError">{{ usernameError }}</p>
        </div>

        <!-- 密码输入框 -->
        <div class="form-item">
          <input
            type="password"
            v-model="loginForm.password"
            placeholder="请输入密码"
            class="login-input"
            :class="{ 'input-focus': passwordFocus }"
            @focus="passwordFocus = true"
            @blur="passwordFocus = false"
            required
          />
          <p class="error-msg" v-if="passwordError">{{ passwordError }}</p>
        </div>

        <!-- 前端验证码 -->
        <div class="form-item">
          <div class="code-wrapper">
            <input
              type="text"
              v-model="loginForm.code"
              placeholder="请输入验证码"
              class="login-input code-input"
              :class="{ 'input-focus': codeFocus }"
              @focus="codeFocus = true"
              @blur="codeFocus = false"
              required
              maxlength="4"
            />
            <div class="code-img" @click="refreshCode">
              {{ verifyCode }}
            </div>
          </div>
          <p class="error-msg" v-if="codeError">{{ codeError }}</p>
        </div>

        <!-- 新增注册按钮 + 保留忘记密码（左右布局） -->
        <div class="login-options">
          <span class="register-link">没有账号？<span @click="toRegister" class="link-text">立即注册</span></span>
          <span class="forget-password" @click="toForgetPassword">忘记密码？</span>
        </div>

        <!-- 登录按钮 -->
        <button type="submit" class="login-btn" :disabled="isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          登 录
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
// 1. 补充导入Pinia（核心修复）
import { useUserStore } from "@/store/index";
// 导入登录接口（仅传 username 和 password）
import { login } from "@/api/login";

const router = useRouter();
// 2. 创建userStore实例（核心修复）
const userStore = useUserStore();

// 表单数据：仅保留 username、password、code（无 remember）
const loginForm = ref({
  username: "",
  password: "",
  code: "",
});

// 输入框聚焦状态（动效控制）
const usernameFocus = ref(false);
const passwordFocus = ref(false);
const codeFocus = ref(false);

// 表单错误提示
const usernameError = ref("");
const passwordError = ref("");
const codeError = ref("");

// 登录按钮加载状态
const isLoading = ref(false);

// 前端验证码逻辑（随机4位数字）
const verifyCode = ref("");
const refreshCode = () => {
  verifyCode.value = Math.floor(1000 + Math.random() * 9000).toString();
};

// 表单校验（无 remember 相关逻辑）
const validateForm = () => {
  let isValid = true;

  // 校验用户名
  if (!loginForm.value.username.trim()) {
    usernameError.value = "请输入用户名";
    isValid = false;
  } else if (loginForm.value.username.length < 3 || loginForm.value.username.length > 20) {
    usernameError.value = "用户名长度为3-20位";
    isValid = false;
  } else {
    usernameError.value = "";
  }

  // 校验密码
  if (!loginForm.value.password.trim()) {
    passwordError.value = "请输入密码";
    isValid = false;
  } else if (loginForm.value.password.length < 6) {
    passwordError.value = "密码长度不少于6位";
    isValid = false;
  } else {
    passwordError.value = "";
  }

  // 校验验证码（前端本地校验）
  if (!loginForm.value.code.trim()) {
    codeError.value = "请输入验证码";
    isValid = false;
  } else if (loginForm.value.code.length !== 4) {
    codeError.value = "验证码为4位数字";
    isValid = false;
  } else if (loginForm.value.code !== verifyCode.value) {
    codeError.value = "验证码输入错误";
    isValid = false;
  } else {
    codeError.value = "";
  }

  return isValid;
};

// 核心登录逻辑（修复后）
const handleLogin = async () => {
  try {
    // 1. 前端表单校验（含验证码）
    if (!validateForm()) return;

    // 2. 显示加载状态
    isLoading.value = true;

    // 3. 调用登录接口：仅传递后端需要的 username 和 password
    const res = await login({
      username: loginForm.value.username,
      password: loginForm.value.password,
    });
    
    if (res.success) {
      // 存储id和role到Pinia
      userStore.login({
        id: res.data.id,
        role: res.data.role
      });
      console.log("存储的role：", userStore.role); // 调试用：看是否存储成功
      console.log("存储的id：", userStore.id);

        const targetRole = res.data.role;
        const targetPath = `/${targetRole}dashboard`;
        console.log("最终跳转路径：", targetPath); 
        // 跳转
        await router.push(targetPath).catch(err => {
          console.error("跳转失败：", err);
          alert(`跳转失败！路径${targetPath}不存在，请检查路由配置`);
        });

      // 跳转（替换alert为控制台提示，避免阻塞）
      await router.push(targetPath).catch(err => {
        console.error("跳转失败：", err);
        alert(`跳转失败！路径${targetPath}不存在，请检查路由配置`);
      });
      // 延迟提示，确保跳转完成
      setTimeout(() => {
        alert(`登录成功！已跳转到${targetRole}仪表盘`);
      }, 500);
    } else {
      alert(res.message || "登录失败，请检查账号密码！");
      refreshCode(); // 刷新验证码
      loginForm.value.code = "";
    }
  } catch (error) {
    // 精准区分错误类型，不再误报“网络错误”
    console.error("登录错误详情：", error);
    if (error.message.includes("Network Error")) {
      alert("真·网络异常！请检查后端是否启动/接口地址是否正确");
    } else if (error.message.includes("userStore is not defined")) {
      alert("代码错误：缺少Pinia导入！");
    } else {
      alert(`登录失败：${error.message || "未知错误"}`);
    }
    refreshCode();
    loginForm.value.code = "";
  } finally {
    // 关闭加载状态
    isLoading.value = false;
  }
};

// 新增：跳转到注册页
const toRegister = () => {
  router.push("/register");
};

// 忘记密码跳转（无需可删除，需配合路由）
const toForgetPassword = () => {
  router.push("/forget-password");
};

// 页面挂载时生成初始验证码
onMounted(() => {
  refreshCode();
});
</script>

<style scoped>
/* 所有样式完全保留，无修改 */
.login-page {
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
.login-page::before {
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
.login-page::after {
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
.login-card {
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
.login-header {
  text-align: center;
  margin-bottom: 35px;
  padding-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}
.login-title {
  font-size: 26px;
  font-weight: 600;
  color: #333;
  margin: 0;
  letter-spacing: 0.5px;
}
.login-subtitle {
  font-size: 14px;
  color: #666;
  margin-top: 8px;
  line-height: 1.4;
}
.login-form {
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
.login-input {
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
.login-input::placeholder {
  color: #999;
  font-size: 15px;
}
.code-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
}
.code-input {
  flex: 1;
}
.code-img {
  width: 110px;
  height: 52px;
  line-height: 52px;
  text-align: center;
  background: linear-gradient(120deg, #f5f7fa 0%, #e4eaf5 100%);
  border-radius: 10px;
  color: #667eea;
  font-size: 18px;
  letter-spacing: 3px;
  cursor: pointer;
  user-select: none;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.code-img:hover {
  background: linear-gradient(120deg, #e4eaf5 0%, #f5f7fa 100%);
  transform: translateY(-2px);
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
.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 5px 0 10px;
}
.register-link {
  font-size: 14px;
  color: #666;
}
.link-text, .forget-password {
  color: #667eea;
  cursor: pointer;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}
.link-text:hover, .forget-password:hover {
  color: #764ba2;
  text-decoration: underline;
}
.login-btn {
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
.login-btn:hover:not(:disabled) {
  background: linear-gradient(120deg, #5a6fe0 0%, #6a4298 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}
.login-btn:disabled {
  background: linear-gradient(120deg, #c5cce8 0%, #d1c0e0 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}
.login-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.3);
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
</style>