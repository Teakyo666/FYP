<template>
  <div class="user-management">
    <!-- Header with clean design -->
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>
            <span class="icon-wrapper">👥</span>
            User Management
          </h1>
          <p>Manage waste classification system users efficiently</p>
        </div>
        <div class="header-stats">
          <div class="stat-card">
            <div class="stat-icon">♻️</div>
            <div class="stat-info">
              <div class="stat-number">{{ pagination.total }}</div>
              <div class="stat-label">Total Users</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Search and operation toolbar -->
    <div class="toolbar">
      <el-card shadow="hover" class="toolbar-card">
        <el-row :gutter="20" align="middle">
          <el-col :span="18">
            <div class="search-container">
              <el-input
                v-model="searchForm.username"
                placeholder="Search by email address..."
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button 
                type="primary" 
                :icon="Search" 
                @click="handleSearch"
                class="search-btn"
              >
                Search
              </el-button>
              <el-button 
                @click="clearSearch"
                class="clear-btn"
              >
                Clear
              </el-button>
            </div>
          </el-col>
          <el-col :span="6" class="text-right">
            <el-button 
              type="primary" 
              :icon="Plus" 
              @click="handleAdd"
              class="add-btn"
            >
              <span>Add User</span>
            </el-button>
          </el-col>
        </el-row>
      </el-card>
    </div>

    <!-- Data table with modern styling -->
    <div class="table-container">
      <el-card shadow="hover" class="table-card">
        <el-table
          :data="tableData"
          v-loading="loading"
          element-loading-text="Loading data..."
          stripe
          style="width: 100%"
          :header-cell-style="{ background: '#fafafa', color: '#333', fontWeight: '600', fontSize: '14px' }"
        >
          <el-table-column prop="id" label="User ID" width="280">
            <template #default="scope">
              <span class="id-text">{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="Email Address" min-width="200">
            <template #default="scope">
              <div class="email-cell">
                <el-icon class="email-icon"><Message /></el-icon>
                <span>{{ scope.row.username }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="password" label="Password" min-width="150">
            <template #default="scope">
              <span class="password-text">••••••••</span>
            </template>
          </el-table-column>
          <el-table-column prop="role" label="Role" width="150">
            <template #default="scope">
              <div class="role-cell">
                <span class="role-icon">{{ getRoleIcon(scope.row.role) }}</span>
                <el-tag 
                  :type="getRoleTagType(scope.row.role)"
                  effect="plain"
                  class="role-tag"
                >
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="280" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-button 
                  size="small" 
                  @click="handleViewInfo(scope.row)"
                  class="view-btn"
                >
                  <el-icon><View /></el-icon>
                  <span>View</span>
                </el-button>
                <el-button 
                  size="small" 
                  type="primary"
                  plain
                  @click="handleEdit(scope.row)"
                  class="edit-btn"
                >
                  <el-icon><Edit /></el-icon>
                  <span>Edit</span>
                </el-button>
                <el-button 
                  size="small" 
                  type="danger"
                  plain
                  @click="handleDelete(scope.row)"
                  class="delete-btn"
                >
                  <el-icon><Delete /></el-icon>
                  <span>Delete</span>
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- Pagination with clean styling -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        background
        class="custom-pagination"
      />
    </div>

    <!-- Edit/Add dialog with modern design -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :before-close="handleDialogClose"
      class="custom-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        label-position="left"
        class="user-form"
      >
        <el-form-item label="Email" prop="username">
          <el-input 
            v-model="formData.username" 
            placeholder="Enter user email address"
            prefix-icon="Message"
          />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input 
            v-model="formData.password" 
            type="password" 
            placeholder="Enter password"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <el-select 
            v-model="formData.role" 
            placeholder="Select user role" 
            style="width: 100%"
          >
            <el-option label="Administrator" value="admin">
              <span class="role-option">
                <span class="role-option-icon">👤</span>
                <span>Administrator</span>
              </span>
            </el-option>
            <el-option label="Volunteer" value="volunteer">
              <span class="role-option">
                <span class="role-option-icon">🤝</span>
                <span>Volunteer</span>
              </span>
            </el-option>
            <el-option label="Recycler" value="recycler">
              <span class="role-option">
                <span class="role-option-icon">♻️</span>
                <span>Recycler</span>
              </span>
            </el-option>
            <el-option label="Regular User" value="customer">
              <span class="role-option">
                <span class="role-option-icon">👥</span>
                <span>Regular User</span>
              </span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" size="default">Cancel</el-button>
          <el-button type="primary" @click="handleSubmit" size="default">
            <el-icon class="mr-1"><Check /></el-icon>
            <span>Confirm</span>
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- User information viewing dialog -->
    <el-dialog
      v-model="infoDialogVisible"
      title="User Information"
      width="500px"
      :close-on-click-modal="false"
      class="custom-dialog info-dialog"
    >
      <el-form
        label-width="100px"
        label-position="left"
        class="info-form"
      >
        <el-form-item label="Name">
          <span v-if="!isEditing" class="info-text">{{ userInfo.name || 'Not set' }}</span>
          <el-input v-else v-model="userInfo.name" placeholder="Enter name" />
        </el-form-item>
        <el-form-item label="City">
          <span v-if="!isEditing" class="info-text">{{ userInfo.city || 'Not set' }}</span>
          <el-input v-else v-model="userInfo.city" placeholder="Enter city" />
        </el-form-item>
        <el-form-item label="Country">
          <span v-if="!isEditing" class="info-text">{{ userInfo.country || 'Not set' }}</span>
          <el-input v-else v-model="userInfo.country" placeholder="Enter country" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="!isEditing" type="primary" @click="enableEditing" size="default">
            <el-icon class="mr-1"><Edit /></el-icon>
            <span>Edit</span>
          </el-button>
          <template v-else>
            <el-button @click="cancelEditing" size="default">Cancel</el-button>
            <el-button type="primary" @click="saveUserInfo" size="default">
              <el-icon class="mr-1"><Check /></el-icon>
              <span>Save</span>
            </el-button>
          </template>
          <el-button @click="infoDialogVisible = false" size="default" v-if="!isEditing">Close</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, View, Message, Check } from '@element-plus/icons-vue'
import { 
  UserList,
  UserUpdate, 
  UserDelete,
  UserRegister
} from '@/api/Users'
import { GetProfile, UpdateProfile } from '@/api/Home'

// Reactive state variables
const loading = ref(false)
const dialogVisible = ref(false)
const infoDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const isEditing = ref(false)
const currentUserInfo = ref({})

// Table data storage
const tableData = ref([])

// Search form data
const searchForm = reactive({
  username: ''
})

// Pagination configuration
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// Form data for add/edit operations
const formData = reactive({
  id: '',
  username: '',
  password: '',
  role: ''
})

// User detailed information
const userInfo = reactive({
  id: '',
  username: '',
  role: '',
  name: '',
  email: '',
  phone: '',
  address: '',
  city: '',
  country: ''
})

// Form validation rules
const formRules = {
  username: [{ required: true, message: 'Please enter email', trigger: 'blur' }],
  password: [{ required: false, message: 'Please enter password', trigger: 'blur' }],
  role: [{ required: true, message: 'Please select role', trigger: 'change' }]
}

// Form reference for validation
const formRef = ref()

// Handle search action
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// Clear search filters
const clearSearch = () => {
  searchForm.username = ''
  pagination.currentPage = 1
  fetchData()
}

// View user detailed information
const handleViewInfo = async (row) => {
  try {
    const response = await GetProfile({ userId: row.id })
    if (response && response.success) {
      currentUserInfo.value = response.data || {}
      userInfo.name = (response.data && response.data.name) || ''
      userInfo.city = (response.data && response.data.city) || ''
      userInfo.country = (response.data && response.data.country) || ''
      isEditing.value = false
      infoDialogVisible.value = true
    } else {
      ElMessage.error(response.message || 'Failed to get user information')
    }
  } catch (error) {
    console.error('Failed to get user information:', error)
    ElMessage.error('Failed to get user information: ' + (error.message || 'Unknown error'))
  }
}

// Enable editing mode for user info
const enableEditing = () => {
  isEditing.value = true
}

// Save updated user information
const saveUserInfo = async () => {
  try {
    const response = await UpdateProfile({
      id: currentUserInfo.value.id,
      name: userInfo.name,
      city: userInfo.city,
      country: userInfo.country
    })
    
    if (response && response.success) {
      ElMessage.success('User information updated successfully')
      isEditing.value = false
      currentUserInfo.value.name = userInfo.name
      currentUserInfo.value.city = userInfo.city
      currentUserInfo.value.country = userInfo.country
    } else {
      ElMessage.error(response.message || 'Update failed')
    }
  } catch (error) {
    console.error('Failed to update user information:', error)
    ElMessage.error('Failed to update user information: ' + (error.message || 'Unknown error'))
  }
}

// Cancel editing and restore original data
const cancelEditing = () => {
  isEditing.value = false
  userInfo.name = currentUserInfo.value.name || ''
  userInfo.city = currentUserInfo.value.city || ''
  userInfo.country = currentUserInfo.value.country || ''
}

// Fetch user list data from API
const fetchData = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.username || undefined
    }
    
    const response = await UserList(params)
    
    if (response && response.success) {
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
      const errorMsg = response ? (response.message || 'Failed to get data') : 'No response from server'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('Failed to get data:', error)
    ElMessage.error('Failed to get data: ' + (error.message || 'Unknown error'))
  } finally {
    loading.value = false
  }
}

// Open dialog to add new user
const handleAdd = () => {
  dialogTitle.value = 'Add User'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Open dialog to edit existing user
const handleEdit = (row) => {
  dialogTitle.value = 'Edit User'
  isEdit.value = true
  Object.assign(formData, row)
  formData.password = ''
  dialogVisible.value = true
}

// Delete user with confirmation
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete user "${row.username}"?`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await UserDelete({ id: row.id })
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
  }).catch(() => {})
}

// Submit form data (add or update)
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (isEdit.value) {
          const updateData = {
            id: formData.id,
            username: formData.username,
            password: formData.password,
            role: formData.role
          }
          response = await UserUpdate(updateData)
        } else {
          const createData = {
            username: formData.username,
            password: formData.password,
            role: formData.role
          }
          response = await UserRegister(createData)
        }
        
        if (response.success) {
          ElMessage.success(isEdit.value ? 'Updated successfully' : 'Added successfully')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || (isEdit.value ? 'Update failed' : 'Add failed'))
        }
      } catch (error) {
        console.error('Operation failed:', error)
        ElMessage.error(isEdit.value ? 'Update failed' : 'Add failed')
      }
    }
  })
}

// Handle dialog close event
const handleDialogClose = (done) => {
  resetForm()
  done()
}

// Reset form to initial state
const resetForm = () => {
  Object.assign(formData, {
    id: '',
    username: '',
    password: '',
    role: ''
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// Handle page size change
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

// Handle current page change
const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchData()
}

// Get tag color type based on role
const getRoleTagType = (role) => {
  const typeMap = {
    'admin': 'danger',
    'volunteer': 'success',
    'recycler': 'warning',
    'customer': ''
  }
  return typeMap[role] || ''
}

// Get role display text
const getRoleText = (role) => {
  const roleMap = {
    'admin': 'Admin',
    'volunteer': 'Volunteer',
    'recycler': 'Recycler',
    'customer': 'User'
  }
  return roleMap[role] || 'Unknown'
}

// Get role icon for waste classification system
const getRoleIcon = (role) => {
  const iconMap = {
    'admin': '👤',
    'volunteer': '🤝',
    'recycler': '♻️',
    'customer': '👥'
  }
  return iconMap[role] || '👤'
}

// Initialize data on component mount
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* Main container with soft background */
.user-management {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Header section with clean design */
.header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px 32px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.header-text h1 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
  letter-spacing: -0.5px;
}

.icon-wrapper {
  font-size: 32px;
}

.header-text p {
  color: #606266;
  font-size: 14px;
  margin: 0;
  font-weight: 400;
}

/* Statistics card with modern styling */
.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 16px 24px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
}

.stat-icon {
  font-size: 36px;
}

.stat-info {
  text-align: left;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.9;
}

/* Toolbar section styling */
.toolbar {
  margin-bottom: 20px;
}

.toolbar-card {
  border-radius: 12px;
  border: none;
}

.toolbar-card :deep(.el-card__body) {
  padding: 20px;
}

.search-container {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 400px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.search-btn {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
}

.clear-btn {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
}

.add-btn {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.text-right {
  text-align: right;
}

/* Table container styling */
.table-container {
  margin-bottom: 20px;
}

.table-card {
  border-radius: 12px;
  border: none;
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

.table-card :deep(.el-table) {
  border-radius: 12px;
  font-size: 14px;
}

.table-card :deep(.el-table th) {
  font-weight: 600;
  color: #303133;
}

.table-card :deep(.el-table td) {
  color: #606266;
  font-weight: 400;
}

.table-card :deep(.el-table__row) {
  transition: background-color 0.2s;
}

.table-card :deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

/* ID text styling */
.id-text {
  color: #606266;
  font-size: 13px;
  font-family: 'Monaco', 'Menlo', 'Courier New', monospace;
  font-weight: 500;
}

/* Email cell with icon */
.email-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
  font-weight: 500;
}

.email-icon {
  color: #409eff;
  font-size: 16px;
}

/* Password display styling */
.password-text {
  color: #c0c4cc;
  font-family: monospace;
  letter-spacing: 2px;
  font-size: 14px;
}

/* Role tag styling */
.role-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-icon {
  font-size: 18px;
}

.role-tag {
  font-weight: 600;
  font-size: 13px;
  border: none;
}

/* Action buttons container */
.action-buttons {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  border-radius: 6px;
  padding: 7px 12px;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.view-btn {
  color: #606266;
  border-color: #dcdfe6;
  background: #fff;
}

.view-btn:hover {
  color: #409eff;
  border-color: #c6e2ff;
  background: #ecf5ff;
}

.edit-btn {
  padding: 7px 12px;
}

.delete-btn {
  padding: 7px 12px;
}

/* Pagination styling */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.custom-pagination {
  padding: 0;
}

/* Dialog styling */
.custom-dialog :deep(.el-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

.custom-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  background: #fafafa;
  border-bottom: 1px solid #e4e7ed;
}

.custom-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.custom-dialog :deep(.el-dialog__body) {
  padding: 24px;
}

.user-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.user-form :deep(.el-input__wrapper) {
  border-radius: 6px;
}

.info-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.info-text {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
}

/* Dialog footer */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  border-radius: 6px;
  height: 36px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Role option in select dropdown */
.role-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-option-icon {
  font-size: 16px;
}

/* Utility class for icon margin */
.mr-1 {
  margin-right: 4px;
}

/* Responsive design for mobile devices */
@media (max-width: 768px) {
  .user-management {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }
  
  .search-container {
    flex-direction: column;
    width: 100%;
  }
  
  .search-input {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .stat-card {
    width: 100%;
  }
}
</style>