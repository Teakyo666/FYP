<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <h2 class="register-title">Account Registration</h2>
        <p class="register-subtitle">Fill in the following information to quickly create an account</p>
      </div>

      <!-- Registration form -->
      <form class="register-form" @submit.prevent="handleRegister">
        <!-- Email input -->
        <div class="form-item">
          <input
            type="email"
            v-model="registerForm.email"
            placeholder="Enter email"
            class="register-input"
            :class="{ 'input-focus': emailFocus }"
            @focus="emailFocus = true"
            @blur="emailFocus = false"
            required
          />
          <p class="error-msg" v-if="emailError">{{ emailError }}</p>
        </div>

        <!-- Password input -->
        <div class="form-item">
          <input
            type="password"
            v-model="registerForm.password"
            placeholder="Enter password (at least 6 characters)"
            class="register-input"
            :class="{ 'input-focus': passwordFocus }"
            @focus="passwordFocus = true"
            @blur="passwordFocus = false"
            required
          />
          <p class="error-msg" v-if="passwordError">{{ passwordError }}</p>
        </div>

        <!-- Confirm password input -->
        <div class="form-item">
          <input
            type="password"
            v-model="registerForm.confirmPassword"
            placeholder="Re-enter password"
            class="register-input"
            :class="{ 'input-focus': confirmPasswordFocus }"
            @focus="confirmPasswordFocus = true"
            @blur="confirmPasswordFocus = false"
            required
          />
          <p class="error-msg" v-if="confirmPasswordError">{{ confirmPasswordError }}</p>
        </div>

        <!-- Register button + login redirect -->
        <div class="register-actions">
          <button type="submit" class="register-btn" :disabled="isLoading">
            <span v-if="isLoading" class="loading-spinner"></span>
            Register Now
          </button>
          <div class="login-link">
            Already have an account? <span @click="toLogin" class="link-text">Login Now</span>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
// Import registration API (to be created later)
import { register } from "@/api/login";

const router = useRouter();

// Form data: email, password, confirm password
const registerForm = ref({
  email: "",
  password: "",
  confirmPassword: "",
});

// Input focus status (animation control)
const emailFocus = ref(false);
const passwordFocus = ref(false);
const confirmPasswordFocus = ref(false);

// Form error messages
const emailError = ref("");
const passwordError = ref("");
const confirmPasswordError = ref("");

// Register button loading status
const isLoading = ref(false);

// Form validation logic
const validateForm = () => {
  let isValid = true;
  const { email, password, confirmPassword } = registerForm.value;

  // Validate email
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!email.trim()) {
    emailError.value = "Please enter email";
    isValid = false;
  } else if (!emailReg.test(email)) {
    emailError.value = "Please enter a valid email format (e.g., xxx@xxx.com)";
    isValid = false;
  } else {
    emailError.value = "";
  }

  // Validate password
  if (!password.trim()) {
    passwordError.value = "Please enter password";
    isValid = false;
  } else if (password.length < 6) {
    passwordError.value = "Password length must be at least 6 characters";
    isValid = false;
  } else {
    passwordError.value = "";
  }

  // Validate confirm password
  if (!confirmPassword.trim()) {
    confirmPasswordError.value = "Please re-enter password";
    isValid = false;
  } else if (confirmPassword !== password) {
    confirmPasswordError.value = "The two passwords entered do not match";
    isValid = false;
  } else {
    confirmPasswordError.value = "";
  }

  return isValid;
};

// Core registration logic
const handleRegister = async () => {
  try {
    // 1. Frontend form validation
    if (!validateForm()) return;

    // 2. Show loading status
    isLoading.value = true;

    // 3. Call registration API (only pass email and password, confirm password is only for frontend validation)
    const res = await register({
      username: registerForm.value.email,
      password: registerForm.value.password,
    });

    // 4. API success handling
    if (res.success) {
      alert("Registration successful! Redirecting to login page～");
      router.push("/login"); // Redirect to login page after successful registration
    } else {
      // API returns failure (e.g., email already registered)
      alert(res.message || "Registration failed, please try again later!");
    }
  } catch (error) {
    // Network exception/API call failure
    console.error("Registration API call failed:", error);
    // Check if the error contains the backend response with message
    if (error && error.message) {
      alert(error.message);
    } else {
      alert("Network error, please check if the backend is running!");
    }
  } finally {
    // Close loading status
    isLoading.value = false;
  }
};

// Navigate to login page
const toLogin = () => {
  router.push("/login");
};
</script>

<style scoped>
/* Registration page full-screen container (same gradient background as login page) */
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

/* Background decoration dots (same as login page) */
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

/* Registration card (same floating texture as login page) */
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

/* Registration title area */
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

/* Form container */
.register-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Single form item */
.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* Input box style (same as login page) */
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
/* Input box focus effect (same as login page) */
.input-focus {
  border: double 1px transparent;
  background-image: linear-gradient(white, white),
                    linear-gradient(120deg, #667eea, #764ba2);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  box-shadow: 0 0 12px rgba(102, 126, 234, 0.25);
}
/* Input box placeholder */
.register-input::placeholder {
  color: #999;
  font-size: 15px;
}

/* Error message text (same as login page) */
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

/* Register button + login redirect area */
.register-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 10px;
}

/* Register button (same style as login page button) */
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

/* Loading animation (same as login page) */
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

/* Login redirect link */
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