<!-- src/views/dashboard/AddApply.vue -->
<template>
  <div class="add-apply-container">
    <div class="page-header">
      <div class="header-content">
        <div class="header-icon">📝</div>
        <div class="header-text">
          <h1>Permission Application</h1>
          <p>Apply for additional permissions to access more features</p>
        </div>
      </div>
    </div>

    <div class="form-wrapper">
      <el-card class="apply-card">
        <template #header>
          <div class="card-header">
            <el-icon class="header-icon-element"><Document /></el-icon>
            <span>Application Form</span>
          </div>
        </template>
        
        <el-form 
          :model="formData" 
          :rules="formRules" 
          ref="formRef" 
          label-width="160px"
          class="apply-form"
          label-position="top"
        >
          <!-- Apply for permission -->
          <el-form-item label="Permission Type" prop="to_apply">
            <el-select 
              v-model="formData.to_apply" 
              placeholder="Select the permission you want to apply for"
              size="large"
              class="permission-select"
            >
              <el-option label="Recycler" value="recycler">
                <div class="custom-option">
                  <span class="option-icon">♻️</span>
                  <div class="option-content">
                    <span class="option-label">Recycler</span>
                    <span class="option-desc">Access recycling management features</span>
                  </div>
                </div>
              </el-option>
              <el-option label="Volunteer" value="volunteer">
                <div class="custom-option">
                  <span class="option-icon">🤝</span>
                  <div class="option-content">
                    <span class="option-label">Volunteer</span>
                    <span class="option-desc">Participate in community activities</span>
                  </div>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          
          <!-- Application reason -->
          <el-form-item label="Application Reason" prop="reason">
            <el-input
              v-model="formData.reason"
              type="textarea"
              :rows="8"
              placeholder="Please describe why you need this permission and how you plan to use it..."
              clearable
              show-word-limit
              maxlength="500"
              class="reason-textarea"
            />
            <div class="form-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>Provide detailed information to help us review your application faster</span>
            </div>
          </el-form-item>
          
          <!-- Hidden fields -->
          <el-form-item label="User ID" prop="user_id" v-show="false">
            <el-input v-model="formData.user_id" disabled></el-input>
          </el-form-item>
          
          <el-form-item label="Status" prop="status" v-show="false">
            <el-input v-model="formData.status" disabled></el-input>
          </el-form-item>
          
          <!-- Action buttons -->
          <el-form-item class="form-actions">
            <el-button 
              type="primary" 
              @click="submitForm" 
              :loading="submitting"
              size="large"
              class="submit-btn"
            >
              <el-icon v-if="!submitting"><Check /></el-icon>
              Submit Application
            </el-button>
            <el-button 
              @click="resetForm"
              size="large"
              class="reset-btn"
            >
              <el-icon><RefreshLeft /></el-icon>
              Reset Form
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- Info Cards -->
      <div class="info-section">
        <div class="info-card">
          <div class="info-icon">⏱️</div>
          <h3>Review Time</h3>
          <p>Applications are typically reviewed within 1-3 business days</p>
        </div>
        <div class="info-card">
          <div class="info-icon">📧</div>
          <h3>Notification</h3>
          <p>You will be notified via email once your application is processed</p>
        </div>
        <div class="info-card">
          <div class="info-icon">❓</div>
          <h3>Questions?</h3>
          <p>Contact our support team if you need assistance</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CreateApply } from '@/api/Apply'
import { useUserStore } from '@/store'
import { Document, Check, RefreshLeft, InfoFilled } from '@element-plus/icons-vue'

const formRef = ref()
const submitting = ref(false)

const formData = reactive({
  to_apply: '',
  reason: '',
  user_id: '',
  status: 'Pending Review'
})

const formRules = reactive({
  to_apply: [
    { required: true, message: 'Please select the permission type', trigger: 'change' }
  ],
  reason: [
    { required: true, message: 'Please provide your application reason', trigger: 'blur' },
    { min: 20, message: 'Please provide at least 20 characters', trigger: 'blur' }
  ],
  user_id: [
    { required: true, message: 'User ID cannot be empty', trigger: 'blur' }
  ]
})

const userStore = useUserStore()

onMounted(() => {
  if (userStore && userStore.id) {
    formData.user_id = userStore.id
  }
})

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        
        if (!formData.user_id && userStore && userStore.id) {
          formData.user_id = userStore.id
        }
        
        if (!formData.to_apply || !formData.reason || !formData.user_id) {
          ElMessage.error('Please ensure all required fields are filled')
          return
        }
        
        const submitData = {
          to_apply: formData.to_apply,
          reason: formData.reason,
          user_id: formData.user_id,
          status: formData.status
        }
        
        const res = await CreateApply(submitData)
        if (res.code === 200 || res.success) {
          ElMessage.success('Application submitted successfully!')
          resetForm()
        } else {
          ElMessage.error(res.msg || 'Application submission failed')
        }
      } catch (err) {
        ElMessage.error('Application submission failed: ' + (err.message || 'Unknown error'))
        console.error('Submission error:', err)
      } finally {
        submitting.value = false
      }
    }
  })
}

const resetForm = () => {
  formRef.value.resetFields()
  if (userStore && userStore.id) {
    formData.user_id = userStore.id
  }
  formData.status = 'Pending Review'
  formData.to_apply = ''
  formData.reason = ''
}
</script>

<style scoped>
.add-apply-container {
  min-height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
}

/* Page Header */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  font-size: 48px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.header-text h1 {
  color: #fff;
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
}

.header-text p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

/* Form Wrapper */
.form-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

/* Apply Card */
.apply-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.header-icon-element {
  font-size: 24px;
  color: #667eea;
}

/* Form Styles */
.apply-form {
  padding: 20px 0;
}

.apply-form :deep(.el-form-item__label) {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.permission-select {
  width: 100%;
}

.permission-select :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.permission-select :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Custom Option Styles */
.custom-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.option-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.option-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.option-label {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.option-desc {
  font-size: 13px;
  color: #909399;
}

/* Textarea Styles */
.reason-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.reason-textarea :deep(.el-textarea__inner:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.reason-textarea :deep(.el-textarea__inner:focus) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.form-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #e8f4ff 0%, #e1effe 100%);
  border-radius: 8px;
  font-size: 13px;
  color: #606266;
}

.form-tip .el-icon {
  color: #409eff;
  font-size: 16px;
}

/* Action Buttons */
.form-actions {
  margin-top: 32px;
}

.submit-btn {
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.reset-btn {
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  border: 2px solid #dcdfe6;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  border-color: #909399;
  color: #606266;
}

/* Info Section */
.info-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.info-card {
  background: #fff;
  padding: 30px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.info-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.info-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.info-card h3 {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 12px;
}

.info-card p {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

/* Responsive Design */
@media (max-width: 768px) {
  .page-header {
    padding: 30px 20px;
  }

  .header-content {
    flex-direction: column;
    text-align: center;
  }

  .header-icon {
    font-size: 40px;
  }

  .header-text h1 {
    font-size: 24px;
  }

  .apply-form {
    label-width: 100%;
  }

  .apply-form :deep(.el-form-item) {
    flex-direction: column;
  }

  .info-section {
    grid-template-columns: 1fr;
  }

  .submit-btn,
  .reset-btn {
    width: 100%;
    margin-bottom: 12px;
  }
}
</style>