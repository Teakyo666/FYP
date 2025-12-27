<template>
  <div class="user-management">
    <div class="header">
      <h1>User Management</h1>
      <p>Manage user information in the system</p>
    </div>

    <!-- Search and operation bar -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-input
            v-model="searchForm.username"
            placeholder="Please enter username"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch">Search</el-button>
            </template>
          </el-input>
          <el-button style="margin-left: 10px" @click="clearSearch">Clear Search</el-button>
        </el-col>
        <el-col :span="6" class="text-right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">Add User</el-button>
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="Username" min-width="150" />
        <el-table-column prop="password" label="Password" min-width="150" />
        <el-table-column prop="role" label="Role" width="120">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Information" width="100" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleViewInfo(scope.row)">View</el-button>
          </template>
        </el-table-column>

        <!-- Handle user information viewing -->
        <el-table-column label="Operations" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">Edit</el-button>
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

    <!-- Edit/Add dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :before-close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="Username" prop="username">
          <el-input v-model="formData.username" placeholder="Please enter username" />
        </el-form-item>
        <el-form-item label="Password" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="Please enter password" />
        </el-form-item>
        <el-form-item label="Role" prop="role">
          <el-select v-model="formData.role" placeholder="Please select role" style="width: 100%">
            <el-option label="Administrator" value="admin" />
            <el-option label="Volunteer" value="volunteer" />
            <el-option label="Recycler" value="recycler" />
            <el-option label="Regular User" value="customer" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="handleSubmit">Confirm</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- User information viewing dialog -->
    <el-dialog
      v-model="infoDialogVisible"
      title="User Information Details"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        label-width="80px"
        label-position="left"
      >
        <el-form-item label="Name">
          <span v-if="!isEditing">{{ userInfo.name }}</span>
          <el-input v-else v-model="userInfo.name" />
        </el-form-item>
        <el-form-item label="City">
          <span v-if="!isEditing">{{ userInfo.city }}</span>
          <el-input v-else v-model="userInfo.city" />
        </el-form-item>
        <el-form-item label="Country">
          <span v-if="!isEditing">{{ userInfo.country }}</span>
          <el-input v-else v-model="userInfo.country" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button v-if="!isEditing" @click="enableEditing">Edit</el-button>
          <template v-else>
            <el-button type="primary" @click="saveUserInfo">Save</el-button>
            <el-button @click="cancelEditing">Cancel</el-button>
          </template>
          <el-button @click="infoDialogVisible = false">Close</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { 
  UserList,
  UserUpdate, 
  UserDelete,
  UserRegister
} from '@/api/Users'
import { GetProfile, UpdateProfile } from '@/api/Home'

// Reactive data
const loading = ref(false)
const dialogVisible = ref(false)
const infoDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const isEditing = ref(false)
const currentUserInfo = ref({}) // Save currently viewed user information

// Table data
const tableData = ref([])

// Search form
const searchForm = reactive({
  username: ''
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
  username: '',
  password: '',
  role: ''
})

// User information data
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
  username: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
  password: [{ required: true, message: 'Please enter password', trigger: 'blur' }],
  role: [{ required: true, message: 'Please select role', trigger: 'change' }]
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
  searchForm.username = ''
  pagination.currentPage = 1
  fetchData()
}

// Handle user information viewing
const handleViewInfo = async (row) => {
  try {
    const response = await GetProfile({ userId: row.id })
    if (response && response.success) {
      // Save current user information
      currentUserInfo.value = response.data || {}
      
      // Set fields to display
      userInfo.name = (response.data && response.data.name) || ''
      userInfo.city = (response.data && response.data.city) || ''
      userInfo.country = (response.data && response.data.country) || ''
      
      // Reset editing state
      isEditing.value = false
      
      // Show information dialog
      infoDialogVisible.value = true
    } else {
      ElMessage.error(response.message || 'Failed to get user information')
    }
  } catch (error) {
    console.error('Failed to get user information:', error)
    ElMessage.error('Failed to get user information: ' + (error.message || 'Unknown error'))
  }
}

// Enable editing mode
const enableEditing = () => {
  isEditing.value = true
}

// Save user information
const saveUserInfo = async () => {
  try {
    const response = await UpdateProfile({
      userId: currentUserInfo.value.id,
      name: userInfo.value.name,
      city: userInfo.value.city,
      country: userInfo.value.country
    })
    
    if (response && response.success) {
      ElMessage.success('User information updated successfully')
      isEditing.value = false
      // Update data in currentUserInfo
      currentUserInfo.value.name = userInfo.value.name
      currentUserInfo.value.city = userInfo.value.city
      currentUserInfo.value.country = userInfo.value.country
      // May need to refresh user list or other operations
    } else {
      ElMessage.error(response.message || 'Update failed')
    }
  } catch (error) {
    console.error('Failed to update user information:', error)
    ElMessage.error('Failed to update user information: ' + (error.message || 'Unknown error'))
  }
}

// Cancel editing
const cancelEditing = () => {
  isEditing.value = false
  // Restore original data
  userInfo.value.name = currentUserInfo.value.name || ''
  userInfo.value.city = currentUserInfo.value.city || ''
  userInfo.value.country = currentUserInfo.value.country || ''
}

// Format label display name
const formatLabel = (key) => {
  const labelMap = {
    'id': 'User ID',
    'username': 'Username',
    'role': 'Role',
    'name': 'Name',
    'email': 'Email',
    'phone': 'Phone',
    'address': 'Address',
    'city': 'City',
    'country': 'Country'
  }
  // If the corresponding label is not found, convert the key name to English (capitalize first letter)
  if (!labelMap[key]) {
    // Try to convert camel case to English description
    const camelKey = key.replace(/([A-Z])/g, ' $1').trim();
    return camelKey.charAt(0).toUpperCase() + camelKey.slice(1);
  }
  return labelMap[key];
}

// Fetch data
const fetchData = async () => {
  try {
    loading.value = true
    
    // Call the API to get the user list, supporting pagination and search
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.username || undefined
    }
    
    const response = await UserList(params)
    
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

// Handle add
const handleAdd = () => {
  dialogTitle.value = 'Add User'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Handle edit
const handleEdit = (row) => {
  dialogTitle.value = 'Edit User'
  isEdit.value = true
  Object.assign(formData, row)
  // Clear password field, no need to force password change when editing
  formData.password = ''
  dialogVisible.value = true
}

// Handle delete
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
      const response = await UserDelete({ Id: row.id })
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
    // User cancelled deletion
  })
}

// Submit form
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (isEdit.value) {
          // Edit - only send necessary fields
          const updateData = {
            Id: formData.id,
            username: formData.username,
            password: formData.password,
            role: formData.role
          }
          
          response = await UserUpdate(updateData)
        } else {
          // Add
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

// Handle dialog close
const handleDialogClose = (done) => {
  resetForm()
  done()
}

// Reset form
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

// Pagination related methods
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchData()
}

// Get tag type based on role
const getRoleTagType = (role) => {
  const typeMap = {
    'admin': 'danger',
    'volunteer': 'success',
    'recycler': 'warning',
    'customer': 'info'
  }
  return typeMap[role] || 'info'
}

// Get display text based on role
const getRoleText = (role) => {
  const roleMap = {
    'admin': 'Administrator',
    'volunteer': 'Volunteer',
    'recycler': 'Recycler',
    'customer': 'Regular User'
  }
  return roleMap[role] || 'Unknown Role'
}

// Fetch data when component mounts
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.header {
  margin-bottom: 20px;
}

.header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.header p {
  color: #666;
}

.toolbar {
  margin-bottom: 20px;
}

.text-right {
  text-align: right;
}

.table-container {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
