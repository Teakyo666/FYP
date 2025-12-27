<template>
  <div class="apply-management">
    <div class="header">
      <h1>Application Content Management</h1>
      <p>Manage application content submitted by users</p>
    </div>

    <!-- Search and operation bar -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-select
            v-model="searchForm.to_apply"
            placeholder="Please select application permission"
            clearable
            style="width: 300px"
            @change="handleSearch"
          >
            <el-option label="Recycler" value="recycler" />
            <el-option label="Volunteer" value="volunteer" />
          </el-select>
          <el-select
            v-model="searchForm.status"
            placeholder="Please select status"
            clearable
            style="width: 150px; margin-left: 10px"
            @change="handleSearch"
          >
            <el-option label="Pending Review" value="待审核" />
            <el-option label="Approved" value="同意" />
            <el-option label="Rejected" value="拒绝" />
          </el-select>
          <el-button style="margin-left: 10px" @click="clearSearch">Clear Search</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- Data table -->
    <div class="table-container">
      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="Loading..."
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="to_apply" label="Application Permission" min-width="50" />
        <el-table-column prop="reason" label="Application Reason" min-width="200" />
        <el-table-column prop="user_id" label="User ID" width="200" />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.status || 'Unknown Status' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Operations" width="300" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button 
              v-if="scope.row.status !== '同意'" 
              size="small" 
              type="success" 
              @click="handleApprove(scope.row)"
            >
              Approve
            </el-button>
            <el-button 
              v-if="scope.row.status !== '拒绝'" 
              size="small" 
              type="warning" 
              @click="handleReject(scope.row)"
            >
              Reject
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Pagination -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { 
  ListApply,
  UpdateApply,
  DeleteApply
} from '@/api/Apply'
import request from "@/utils/request";

// Reactive data
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// Table data
const tableData = ref([])

// Search form
const searchForm = reactive({
  to_apply: '',
  status: ''
})

// Pagination
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// Form data
const formData = reactive({
  id: '',
  user_id: '',
  to_apply: '',
  reason: '',
  status: '待审核' // Default status
})

// Form validation rules
const formRules = {
  to_apply: [{ required: true, message: 'Please enter application content', trigger: 'blur' }],
  reason: [{ required: true, message: 'Please enter application reason', trigger: 'blur' }],
  user_id: [{ required: true, message: 'Please enter user ID', trigger: 'blur' }],
  status: [{ required: true, message: 'Please select status', trigger: 'change' }]
}

// Form reference
const formRef = ref()

// Search handler
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// Clear search handler
const clearSearch = () => {
  searchForm.to_apply = ''
  searchForm.status = ''
  pagination.currentPage = 1
  fetchData()
}

// Fetch data
const fetchData = async () => {
  try {
    loading.value = true
    
    // Call API to get application content list, supports pagination and search
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      to_apply: searchForm.to_apply || undefined,  // Fix: use to_apply instead of keyword
      status: searchForm.status || undefined
    }
    
    const response = await ListApply(params)
    
    if (response && response.success) {
      // Ensure data structure is correct
      if (response.data && typeof response.data === 'object') {
        tableData.value = Array.isArray(response.data.list) ? response.data.list : []
        pagination.total = typeof response.data.total === 'number' ? response.data.total : 0
      } else {
        tableData.value = []
        pagination.total = 0
      }
    } else {
      tableData.value = []
      pagination.total = 0
      const errorMsg = response ? (response.message || 'Failed to fetch data') : 'Server no response'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('Failed to fetch data:', error)
    ElMessage.error('Failed to fetch data: ' + (error.message || 'Unknown error'))
  } finally {
    loading.value = false
  }
}

// Handle edit
const handleEdit = (row) => {
  dialogTitle.value = 'Edit Application Content'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// Handle approve
const handleApprove = async (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to approve the application for "${row.to_apply}"?`,
    'Approve Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const updateData = {
        id: row.id,
        user_id: row.user_id,
        to_apply: row.to_apply,
        reason: row.reason,
        status: '同意'
      }
      const response = await UpdateApply(updateData)
      if (response.success) {
        ElMessage.success('Application approved')
        fetchData()
      } else {
        ElMessage.error(response.message || 'Operation failed')
      }
    } catch (error) {
      console.error('Operation failed:', error)
      ElMessage.error('Operation failed')
    }
  }).catch(() => {
    // User canceled operation
  })
}

// Handle reject
const handleReject = async (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to reject the application for "${row.to_apply}"?`,
    'Reject Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const updateData = {
        id: row.id,
        user_id: row.user_id,
        to_apply: row.to_apply,
        reason: row.reason,
        status: '拒绝'
      }
      const response = await UpdateApply(updateData)
      if (response.success) {
        ElMessage.success('Application rejected')
        fetchData()
      } else {
        ElMessage.error(response.message || 'Operation failed')
      }
    } catch (error) {
      console.error('Operation failed:', error)
      ElMessage.error('Operation failed')
    }
  }).catch(() => {
    // User canceled operation
  })
}

// Handle delete
const handleDelete = async (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete the application for "${row.to_apply}"? This action cannot be undone!`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeleteApply({ id: row.id })
      if (response.success) {
        ElMessage.success('Deleted successfully')
        fetchData()
      } else {
        ElMessage.error(response.message || 'Deletion failed')
      }
    } catch (error) {
      console.error('Deletion failed:', error)
      ElMessage.error('Deletion failed')
    }
  }).catch(() => {
    // User canceled operation
  })
}

// Form submission handler
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // Only keep edit mode, remove add mode
        const response = await UpdateApply(formData)
        
        if (response.success) {
          ElMessage.success('Updated successfully')
          dialogVisible.value = false
          resetForm()
          fetchData()
        } else {
          ElMessage.error(response.message || 'Update failed')
        }
      } catch (error) {
        console.error('Operation failed:', error)
        ElMessage.error('Operation failed: ' + (error.message || 'Unknown error'))
      }
    }
  })
}

// Handle dialog close
const handleDialogClose = (done) => {
  resetForm()
  done()
}

// Reset form
const resetForm = () => {
  Object.assign(formData, {
    id: '',
    user_id: '',
    to_apply: '',
    reason: '',
    status: '待审核'
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// Pagination related methods
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchData()
}

// Get tag type based on status
const getStatusTagType = (status) => {
  const typeMap = {
    '待审核': 'warning',
    '同意': 'success',
    '拒绝': 'danger'
  }
  return typeMap[status] || 'info'
}

// Fetch data when component is mounted
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.apply-management {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 80px);
}

.header {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.header h1 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.header p {
  color: #666;
  font-size: 14px;
}

.toolbar {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.table-container {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.pagination-container {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
}

.text-right {
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>