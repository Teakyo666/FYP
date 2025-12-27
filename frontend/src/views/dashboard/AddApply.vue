<!-- src/views/dashboard/AddApply.vue -->
<template>
  <div class="add-apply-container">
    <el-card class="apply-card">
      <template #header>
        <div class="card-header">
          <span>Apply Form</span>
        </div>
      </template>
      
      <el-form 
        :model="formData" 
        :rules="formRules" 
        ref="formRef" 
        label-width="120px"
        class="apply-form"
      >
        <!-- Apply for permission -->
        <el-form-item label="Apply for Permission" prop="to_apply">
          <el-select 
            v-model="formData.to_apply" 
            placeholder="Please select permission to apply for"
            style="width: 100%"
          >
            <el-option label="Recycler" value="recycler"></el-option>
            <el-option label="Volunteer" value="volunteer"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- Application reason -->
        <el-form-item label="Application Reason" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="6"
            placeholder="Please enter application reason"
            clearable
          />
        </el-form-item>
        
        <!-- Hidden field: User ID -->
        <el-form-item label="User ID" prop="user_id" v-show="false">
          <el-input v-model="formData.user_id" disabled></el-input>
        </el-form-item>
        
        <!-- Hidden field: Status -->
        <el-form-item label="Status" prop="status" v-show="false">
          <el-input v-model="formData.status" disabled></el-input>
        </el-form-item>
        
        <!-- Action buttons -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">Submit Application</el-button>
          <el-button @click="resetForm">Reset</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { CreateApply } from '@/api/Apply'
import { useUserStore } from '@/store'

// Form reference
const formRef = ref()
const submitting = ref(false)

// Form data
const formData = reactive({
  to_apply: '',  // Changed field name to to_apply
  reason: '',
  user_id: '',   // Ensure user_id is correctly passed
  status: 'Pending Review'
})

// Form validation rules
const formRules = reactive({
  to_apply: [  // Updated field name for validation rules
    { required: true, message: 'Please select permission to apply for', trigger: 'change' }
  ],
  reason: [
    { required: true, message: 'Please enter application reason', trigger: 'blur' }
  ],
  user_id: [
    { required: true, message: 'User ID cannot be empty', trigger: 'blur' }
  ]
})

// Get current user information
const userStore = useUserStore()

// Automatically populate user ID when page loads
onMounted(() => {
  if (userStore && userStore.id) {
    formData.user_id = userStore.id
  }
})

// Submit form
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        
        // Ensure user ID is correctly set
        if (!formData.user_id && userStore && userStore.id) {
          formData.user_id = userStore.id
        }
        
        // Check if all required fields exist
        if (!formData.to_apply || !formData.reason || !formData.user_id) {
          ElMessage.error('Please ensure all fields are filled')
          return
        }
        
        // Prepare data for submission
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

// Reset form
const resetForm = () => {
  formRef.value.resetFields()
  if (userStore && userStore.id) {
    formData.user_id = userStore.id
  }
  formData.status = 'Pending Review'
  formData.to_apply = ''  // Also clear the to_apply field when resetting
  formData.reason = ''
}
</script>

<style scoped>
.add-apply-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 80px);
}

.apply-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.apply-form {
  margin-top: 20px;
}

.apply-form :deep(.el-textarea__inner) {
  min-height: 120px !important;
}
</style>