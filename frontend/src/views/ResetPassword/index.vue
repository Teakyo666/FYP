<template>
  <div class="reset-password-page">
    <div class="reset-password-card">
      <div class="reset-password-header">
        <h2 class="reset-password-title">Reset Password</h2>
        <p class="reset-password-subtitle">Enter Token and new password to complete password reset</p>
      </div>

      <form class="reset-password-form" @submit.prevent="handleResetPassword">
        <!-- Token input: bind to resetToken -->
        <div class="form-item">
          <input
            type="text"
            v-model="resetForm.resetToken"
            placeholder="Enter reset Token"
            class="reset-password-input"
            :class="{ 'input-focus': tokenFocus }"
            @focus="tokenFocus = true"
            @blur="tokenFocus = false"
            required
          />
          <p class="error-msg" v-if="tokenError">{{ tokenError }}</p>
        </div>

        <!-- New password input -->
        <div class="form-item">
          <input
            type="password"
            v-model="resetForm.newPassword"
            placeholder="Enter new password (at least 6 characters)"
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
          Confirm Password Reset
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

// Form fields: resetToken (match backend) + newPassword
const resetForm = ref({
  resetToken: "",
  newPassword: "",
});

// Input focus status
const tokenFocus = ref(false);
const passwordFocus = ref(false);

// Error messages
const tokenError = ref("");
const passwordError = ref("");

// Button loading status
const isLoading = ref(false);

// On page mount: get token from route params → assign to form resetToken
onMounted(() => {
  // Route parameter is token, assign to form's resetToken
  const routeToken = route.query.token;
  if (routeToken) {
    resetForm.value.resetToken = routeToken; // Key: assign ?token=xxx to resetToken
    console.log("Token from route:", routeToken); // For debugging, can be removed
  } else {
    alert("Please apply for a reset Token first!");
    router.push("/apply-token");
  }
});

// Form validation: validate resetToken and newPassword
const validateForm = () => {
  let isValid = true;
  const { resetToken, newPassword } = resetForm.value;

  // Validate resetToken
  if (!resetToken.trim()) {
    tokenError.value = "Please enter reset Token";
    isValid = false;
  } else {
    tokenError.value = "";
  }

  // Validate new password
  if (!newPassword.trim()) {
    passwordError.value = "Please enter new password";
    isValid = false;
  } else if (newPassword.length < 6) {
    passwordError.value = "New password must be at least 6 characters";
    isValid = false;
  } else {
    passwordError.value = "";
  }

  return isValid;
};

// Core password reset logic: pass resetToken (match backend)
const handleResetPassword = async () => {
  try {
    if (!validateForm()) return;
    isLoading.value = true;

    // Call backend API: pass resetToken + newPassword (fully match backend requirements)
    const res = await resetPassword({
      resetToken: resetForm.value.resetToken,
      newPassword: resetForm.value.newPassword,
    });

    if (res.success) {
      alert("Password reset successful! Redirecting to login page...");
      router.push("/login");
    } else {
      alert(res.message || "Token is invalid or expired, please apply again!");
    }
  } catch (error) {
    console.error("Password reset error details:", error);
    // Accurately distinguish error types
    if (error.message.includes("Network Error") || error.message.includes("timeout")) {
      alert("Network error, please check if the backend is running!");
    } else {
      alert(`Password reset failed: ${error.message}`);
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* Reset password page styles */
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