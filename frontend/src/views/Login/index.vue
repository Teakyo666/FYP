<template>
  <div class="login-page">
    <!-- Background decoration -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- Left brand section -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="brand-icon">
          <div class="recycle-icon">♻️</div>
        </div>
        <h1 class="brand-title">EcoMind</h1>
        <p class="brand-slogan">Waste Sorting · Recycling · Better Earth</p>
        
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">✓</div>
            <div class="feature-text">Smart Classification</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">✓</div>
            <div class="feature-text">Online Recycling Query</div>
          </div>
          <div class="feature-item">
            <div class="feature-icon">✓</div>
            <div class="feature-text">Query nearby recycling stations</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Right login card -->
    <div class="login-section">
      <div class="login-card">
        <div class="login-header">
          <h2 class="login-title">Welcome Back</h2>
          <p class="login-subtitle">Sign in to your account to start your eco journey</p>
        </div>

        <form class="login-form" @submit.prevent="handleLogin">
          <!-- Username -->
          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">👤</span>
              Username
            </label>
            <div class="input-wrapper">
              <input
                type="text"
                v-model="loginForm.username"
                placeholder="Enter email"
                class="login-input"
                :class="{ 'input-focus': usernameFocus, 'input-error': usernameError }"
                @focus="usernameFocus = true"
                @blur="usernameFocus = false"
                required
              />
              <span class="input-icon">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </span>
            </div>
            <transition name="error-fade">
              <p class="error-msg" v-if="usernameError">
                <span class="error-icon">⚠</span>
                {{ usernameError }}
              </p>
            </transition>
          </div>

          <!-- Password -->
          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">🔒</span>
              Password
            </label>
            <div class="input-wrapper">
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="loginForm.password"
                placeholder="Enter password"
                class="login-input"
                :class="{ 'input-focus': passwordFocus, 'input-error': passwordError }"
                @focus="passwordFocus = true"
                @blur="passwordFocus = false"
                required
              />
              <span class="input-icon clickable" @click="showPassword = !showPassword">
                <svg v-if="!showPassword" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="3" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 15.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <line x1="1" y1="1" x2="23" y2="23" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </span>
            </div>
            <transition name="error-fade">
              <p class="error-msg" v-if="passwordError">
                <span class="error-icon">⚠</span>
                {{ passwordError }}
              </p>
            </transition>
          </div>

          <!-- Verification Code -->
          <div class="form-item">
            <label class="form-label">
              <span class="label-icon">🔐</span>
              Verification Code
            </label>
            <div class="code-wrapper">
              <div class="input-wrapper code-input-wrapper">
                <input
                  type="text"
                  v-model="loginForm.code"
                  placeholder="Enter verification code"
                  class="login-input code-input"
                  :class="{ 'input-focus': codeFocus, 'input-error': codeError }"
                  @focus="codeFocus = true"
                  @blur="codeFocus = false"
                  required
                  maxlength="4"
                />
              </div>
              <div class="code-img" @click="refreshCode">
                <span class="code-text">{{ verifyCode }}</span>
                <span class="refresh-hint">Click to refresh</span>
              </div>
            </div>
            <transition name="error-fade">
              <p class="error-msg" v-if="codeError">
                <span class="error-icon">⚠</span>
                {{ codeError }}
              </p>
            </transition>
          </div>

          <div class="login-options">
            <label class="remember-checkbox">
              <input type="checkbox" v-model="rememberMe" />
              <span class="checkbox-text">Remember Me</span>
            </label>
            <span class="forget-password" @click="toForgetPassword">Forgot Password?</span>
          </div>

          <button type="submit" class="login-btn" :disabled="isLoading">
            <span v-if="isLoading" class="loading-spinner"></span>
            <span v-else>Login</span>
          </button>

          <div class="register-section">
            <span class="register-text">Don't have an account?</span>
            <span @click="toRegister" class="register-link">Register Now</span>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { useUserStore } from "@/store/index"
import { login } from "@/api/login"
import { ElMessage } from "element-plus"

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({ username: "", password: "", code: "" })

const usernameFocus = ref(false)
const passwordFocus = ref(false)
const codeFocus = ref(false)
const showPassword = ref(false)
const rememberMe = ref(false)

const usernameError = ref("")
const passwordError = ref("")
const codeError = ref("")
const isLoading = ref(false)

const verifyCode = ref("")
const refreshCode = () => {
  verifyCode.value = Math.floor(1000 + Math.random() * 9000).toString()
}

const validateForm = () => {
  let ok = true

  if (!loginForm.value.username.trim()) {
    usernameError.value = "Please enter username"
    ok = false
  } else if (loginForm.value.username.length < 3) {
    usernameError.value = "Username must be at least 3 characters"
    ok = false
  } else usernameError.value = ""

  if (!loginForm.value.password) {
    passwordError.value = "Please enter password"
    ok = false
  } else if (loginForm.value.password.length < 6) {
    passwordError.value = "Password must be at least 6 characters"
    ok = false
  } else passwordError.value = ""

  if (!loginForm.value.code || loginForm.value.code !== verifyCode.value) {
    codeError.value = "Verification code error"
    refreshCode()
    loginForm.value.code = ""
    ok = false
  } else codeError.value = ""

  return ok
}

const handleLogin = async () => {
  if (!validateForm()) return

  isLoading.value = true

  try {
    const res = await login({
      username: loginForm.value.username,
      password: loginForm.value.password,
    })

    if (res.success || res.code === 200 || res.data) {
      const data = res.data || res

      userStore.login({
        id: data.id,
        role: data.role,
        name: data.name || data.username,
        avatar: data.avatar
      })

      ElMessage.success("Login successful! Welcome back 🎉")
      await router.push("/dashboard")

    } else {
      ElMessage.error(res.message || "Login failed, please check your account information")
      refreshCode()
      loginForm.value.code = ""
    }
  } catch (err) {
    console.error(err)
    ElMessage.error("Please enter a valid username and password")
    refreshCode()
    loginForm.value.code = ""
  } finally {
    isLoading.value = false
  }
}

const toRegister = () => router.push("/register")
const toForgetPassword = () => router.push("/forget-password")

onMounted(refreshCode)
</script>

<style scoped>
.login-page {
  position: fixed;
  inset: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  overflow: hidden;
}

/* Background decoration */
.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(50px);
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  left: -100px;
  animation: float 20s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -80px;
  right: 10%;
  animation: float 15s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 40%;
  right: -50px;
  animation: float 18s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(30px, 30px) scale(1.05); }
}

/* Left brand area */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4rem;
  position: relative;
  z-index: 1;
}

.brand-content {
  max-width: 500px;
  color: white;
}

.brand-icon {
  margin-bottom: 2rem;
}

.recycle-icon {
  font-size: 5rem;
  animation: rotate 20s linear infinite;
  display: inline-block;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.brand-title {
  font-size: 3.5rem;
  font-weight: 900;
  margin: 0 0 1rem;
  line-height: 1.2;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.brand-slogan {
  font-size: 1.3rem;
  margin: 0 0 3rem;
  opacity: 0.95;
  font-weight: 500;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.features {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 1.5rem;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(10px);
}

.feature-icon {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  font-weight: bold;
}

.feature-text {
  font-size: 1.1rem;
  font-weight: 600;
}

/* Right login area */
.login-section {
  width: 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  position: relative;
  z-index: 1;
}

.login-card {
  width: 100%;
  padding: 3rem;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.login-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.login-title {
  font-size: 2rem;
  font-weight: 800;
  color: #1f2937;
  margin: 0 0 0.5rem;
  letter-spacing: -0.5px;
}

.login-subtitle {
  font-size: 0.95rem;
  color: #6b7280;
  margin: 0;
  font-weight: 500;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
}

.label-icon {
  font-size: 1rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.login-input {
  width: 100%;
  height: 52px;
  padding: 0 3rem 0 1rem;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  font-size: 1rem;
  color: #1f2937;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
  background: #f9fafb;
}

.login-input:focus,
.input-focus {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.login-input.input-error {
  border-color: #ef4444;
  background: #fef2f2;
}

.login-input::placeholder {
  color: #9ca3af;
  font-size: 0.95rem;
}

.input-icon {
  position: absolute;
  right: 1rem;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.3s ease;
}

.input-icon.clickable {
  cursor: pointer;
}

.input-icon.clickable:hover {
  color: #667eea;
}

.code-wrapper {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.code-input-wrapper {
  flex: 1;
}

.code-input {
  padding-right: 1rem;
}

.code-img {
  width: 120px;
  height: 52px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  user-select: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.code-img::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(255, 255, 255, 0);
  transition: background 0.3s ease;
}

.code-img:hover::before {
  background: rgba(255, 255, 255, 0.2);
}

.code-img:active {
  transform: scale(0.95);
}

.code-text {
  color: white;
  font-size: 1.5rem;
  letter-spacing: 4px;
  font-weight: 700;
  position: relative;
  z-index: 1;
}

.refresh-hint {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.7rem;
  margin-top: 2px;
  position: relative;
  z-index: 1;
}

.error-msg {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #ef4444;
  margin: 0;
  font-weight: 500;
}

.error-icon {
  font-size: 1rem;
}

.error-fade-enter-active,
.error-fade-leave-active {
  transition: all 0.3s ease;
}

.error-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.error-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0.5rem 0;
}

.remember-checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.remember-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #667eea;
}

.checkbox-text {
  font-size: 0.9rem;
  color: #4b5563;
  font-weight: 500;
}

.forget-password {
  color: #667eea;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.forget-password:hover {
  color: #764ba2;
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 54px;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  margin-top: 0.5rem;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.5);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top: 3px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.register-section {
  text-align: center;
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.register-text {
  font-size: 0.95rem;
  color: #6b7280;
  margin-right: 0.5rem;
}

.register-link {
  color: #667eea;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 700;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* Responsive */
@media (max-width: 1200px) {
  .brand-section {
    display: none;
  }
  
  .login-section {
    width: 100%;
    max-width: 520px;
    margin: 0 auto;
  }
}

@media (max-width: 640px) {
  .login-section {
    padding: 1.5rem;
  }
  
  .login-card {
    padding: 2rem 1.5rem;
  }
  
  .login-title {
    font-size: 1.75rem;
  }
  
  .code-wrapper {
    flex-direction: column;
  }
  
  .code-img {
    width: 100%;
  }
}
</style>