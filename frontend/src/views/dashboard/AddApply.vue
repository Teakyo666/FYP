<!-- src/views/dashboard/AddApply.vue -->
<template>
  <div class="add-apply-container">
    <el-card class="apply-card">
      <template #header>
        <div class="card-header">
          <span>添加申请</span>
        </div>
      </template>
      
      <el-form 
        :model="formData" 
        :rules="formRules" 
        ref="formRef" 
        label-width="120px"
        class="apply-form"
      >
        <!-- 申请权限 -->
        <el-form-item label="申请权限" prop="to_apply">
          <el-select 
            v-model="formData.to_apply" 
            placeholder="请选择申请权限"
            style="width: 100%"
          >
            <el-option label="回收员" value="recycler"></el-option>
            <el-option label="志愿者" value="volunteer"></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 申请原因 -->
        <el-form-item label="申请原因" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="6"
            placeholder="请输入申请原因"
            clearable
          />
        </el-form-item>
        
        <!-- 隐藏字段：用户ID -->
        <el-form-item label="用户ID" prop="user_id" v-show="false">
          <el-input v-model="formData.user_id" disabled></el-input>
        </el-form-item>
        
        <!-- 隐藏字段：状态 -->
        <el-form-item label="状态" prop="status" v-show="false">
          <el-input v-model="formData.status" disabled></el-input>
        </el-form-item>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
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

// 表单引用
const formRef = ref()
const submitting = ref(false)

// 表单数据
const formData = reactive({
  to_apply: '',  // 修改字段名为 to_apply
  reason: '',
  user_id: '',   // 确保 user_id 被正确传递
  status: '待审核'
})

// 表单验证规则
const formRules = reactive({
  to_apply: [  // 更新验证规则的字段名
    { required: true, message: '请选择申请权限', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请输入申请原因', trigger: 'blur' }
  ],
  user_id: [
    { required: true, message: '用户ID不能为空', trigger: 'blur' }
  ]
})

// 获取当前用户信息
const userStore = useUserStore()

// 页面加载时自动填充用户ID
onMounted(() => {
  if (userStore && userStore.id) {
    formData.user_id = userStore.id
  }
})

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitting.value = true
        
        // 确保用户ID被正确设置
        if (!formData.user_id && userStore && userStore.id) {
          formData.user_id = userStore.id
        }
        
        // 检查所有必需字段是否存在
        if (!formData.to_apply || !formData.reason || !formData.user_id) {
          ElMessage.error('请确保所有字段都已填写')
          return
        }
        
        // 准备提交的数据
        const submitData = {
          to_apply: formData.to_apply,
          reason: formData.reason,
          user_id: formData.user_id,
          status: formData.status
        }
        
        const res = await CreateApply(submitData)
        if (res.code === 200 || res.success) {
          ElMessage.success('申请提交成功！')
          resetForm()
        } else {
          ElMessage.error(res.msg || '申请提交失败')
        }
      } catch (err) {
        ElMessage.error('申请提交失败：' + (err.message || '未知错误'))
        console.error('提交错误:', err)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value.resetFields()
  if (userStore && userStore.id) {
    formData.user_id = userStore.id
  }
  formData.status = '待审核'
  formData.to_apply = ''  // 重置时也清空 to_apply 字段
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