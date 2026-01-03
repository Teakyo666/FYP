<template>
  <div class="apply-token-page">
    <div class="apply-token-card">
      <div class="apply-token-header">
        <h2 class="apply-token-title">Request Password Reset Token</h2>
        <p class="apply-token-subtitle">Enter your registered email to get a password reset token</p>
      </div>

      <!-- Request Token form -->
      <form class="apply-token-form" @submit.prevent="handleApplyToken">
        <!-- Email input -->
        <div class="form-item">
          <input
            type="email"
            v-model="tokenForm.email"
            placeholder="Enter registered email"
            class="apply-token-input"
            :class="{ 'input-focus': emailFocus }"
            @focus="emailFocus = true"
            @blur="emailFocus = false"
            required
          />
          <p class="error-msg" v-if="emailError">{{ emailError }}</p>
        </div>

        <!-- Request Token button -->
        <button type="submit" class="apply-token-btn" :disabled="isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          Request Reset Token
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
// Import request token API (expanded in login.js)
import { applyToken } from "@/api/login";

const router = useRouter();

// Form data: only email
const tokenForm = ref({
  email: "",
});

// Input focus status (animation control)
const emailFocus = ref(false);

// Form error messages
const emailError = ref("");

// Button loading status
const isLoading = ref(false);

// Form validation (only validate email)
const validateForm = () => {
  let isValid = true;
  const { email } = tokenForm.value;

  // Email format validation
  const emailReg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (!email.trim()) {
    emailError.value = "Please enter registered email";
    isValid = false;
  } else if (!emailReg.test(email)) {
    emailError.value = "Please enter a valid email format (e.g., xxx@xxx.com)";
    isValid = false;
  } else {
    emailError.value = "";
  }

  return isValid;
};

// Core: Request token logic (only modify redirect parameter: email→token)
const handleApplyToken = async () => {
  try {
    if (!validateForm()) return;
    isLoading.value = true;
    const res = await applyToken({
      username: tokenForm.value.email,
    });

    if (res.success) {
      // Extract token returned by backend
      const token = res.data?.token;
      if (!token) {
        alert("Token request failed: Backend did not return reset token!");
        isLoading.value = false;
        return;
      }
      alert("Token request successful! Redirecting to password reset page～");
      // Redirect and carry token parameter (replace the original email parameter)
      router.push({
        path: "/reset-password",
        query: { token: token },
      });
    } else {
      alert(res.message || "Token request failed, please check if the email is correct!");
    }
  } catch (error) {
    console.error("Request token API call failed:", error);
    alert("User not found, please check if the email is correct!");
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* Styles completely retained, no modifications */
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

/* Background decoration dots (add depth) */
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

/* Card container (floating texture) */
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

/* Title area */
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

/* Form container */
.apply-token-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* Form item */
.form-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* Input box style (same as login page) */
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
/* Input box focus effect */
.input-focus {
  border: double 1px transparent;
  background-image: linear-gradient(white, white),
                    linear-gradient(120deg, #667eea, #764ba2);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  box-shadow: 0 0 12px rgba(102, 126, 234, 0.25);
}
/* Placeholder style */
.apply-token-input::placeholder {
  color: #999;
  font-size: 15px;
}

/* Error message */
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

/* Request button (same style as login button) */
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

/* Loading animation */
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

/* Mobile adaptation */
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