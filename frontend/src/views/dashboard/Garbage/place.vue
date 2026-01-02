<template>
  <div class="place-management">
    <!-- Header with clean design -->
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>
            <span class="icon-wrapper">📍</span>
            Recycle Station Management
          </h1>
          <p>Manage recycling stations and collection points</p>
        </div>
        <div class="header-stats">
          <div class="stat-card">
            <div class="stat-icon">🏢</div>
            <div class="stat-info">
              <div class="stat-number">{{ pagination.total }}</div>
              <div class="stat-label">Total Stations</div>
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
              <el-select 
                v-model="searchForm.searchType" 
                placeholder="Search by" 
                style="width: 120px; margin-right: 10px;"
                class="search-type-select"
              >
                <el-option label="Name" value="name" />
                <el-option label="Country" value="country" />
                <el-option label="City" value="city" />
              </el-select>
              <el-input
                v-model="searchForm.searchValue"
                :placeholder="`Search by ${searchForm.searchType || 'name'}...`"
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
              <span>Add Station</span>
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
          <el-table-column prop="name" label="Station Name" min-width="180">
            <template #default="scope">
              <div class="name-cell">
                <el-icon class="name-icon"><Location /></el-icon>
                <span class="name-text">{{ scope.row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="describe" label="Description" min-width="220">
            <template #default="scope">
              <div class="description-text">{{ scope.row.describe }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="country" label="Country" width="130">
            <template #default="scope">
              <div class="location-cell">
                <span class="location-icon">🌍</span>
                <span>{{ scope.row.country }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="city" label="City" width="130">
            <template #default="scope">
              <div class="location-cell">
                <span class="location-icon">🏙️</span>
                <span>{{ scope.row.city }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createBy" label="Created By" width="150">
            <template #default="scope">
              <div class="creator-cell">
                <el-icon class="creator-icon"><User /></el-icon>
                <span>{{ scope.row.createBy }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="200" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
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
      width="600px"
      :before-close="handleDialogClose"
      class="custom-dialog"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        label-position="left"
        class="place-form"
      >
        <el-form-item label="Station Name" prop="name">
          <el-input 
            v-model="formData.name" 
            placeholder="Enter station name"
          />
        </el-form-item>
        <el-form-item label="Description" prop="describe">
          <el-input
            v-model="formData.describe"
            type="textarea"
            :rows="3"
            placeholder="Enter station description"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Country" prop="country">
              <el-input 
                v-model="formData.country" 
                placeholder="Enter country"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="City" prop="city">
              <el-input 
                v-model="formData.city" 
                placeholder="Enter city"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="Address" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="3"
            placeholder="Enter detailed address"
            maxlength="300"
            show-word-limit
          />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Location, User, Check } from '@element-plus/icons-vue'
import { 
  ListPlace,
  AddPlace, 
  UpdatePlace, 
  DeletePlace 
} from '@/api/Place'
import { GetProfile } from '@/api/Home'
import { useUserStore } from '@/store/index'

// Reactive state variables
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// Table data storage
const tableData = ref([])

// Search form data
const searchForm = reactive({
  searchType: 'name',  // 默认搜索类型为name
  searchValue: ''
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
  name: '',
  describe: '',
  country: '',
  city: '',
  address: ''
})

// Form validation rules
const formRules = {
  name: [{ required: true, message: 'Please enter station name', trigger: 'blur' }],
  describe: [{ required: true, message: 'Please enter description', trigger: 'blur' }],
  country: [{ required: true, message: 'Please enter country', trigger: 'blur' }],
  city: [{ required: true, message: 'Please enter city', trigger: 'blur' }],
  address: [{ required: true, message: 'Please enter address', trigger: 'blur' }]
}

// Form reference for validation
const formRef = ref()

// Handle search action
// Handle search
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// Clear search
const clearSearch = () => {
  searchForm.searchType = 'name'
  searchForm.searchValue = ''
  pagination.currentPage = 1
  fetchData()
}

// Fetch data from API
const fetchData = async () => {
  try {
    loading.value = true
    
    // 根据搜索类型构建参数
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize
    }
    
    // 根据选择的搜索类型设置相应的参数
    if (searchForm.searchValue) {
      if (searchForm.searchType === 'name') {
        params.name = searchForm.searchValue
      } else if (searchForm.searchType === 'country') {
        params.country = searchForm.searchValue
      } else if (searchForm.searchType === 'city') {
        params.city = searchForm.searchValue
      }
    }
    
    const response = await ListPlace(params)
    
    if (response && response.success) {
      // Ensure data structure is correct
      if (response.data && typeof response.data === 'object') {
        let list = Array.isArray(response.data.list) ? response.data.list : []
        
        // Get current user information
        const userStore = useUserStore()
        
        // If it's a non-admin user, only show places created by the user
        if (userStore.role !== 'admin') {
          list = list.filter(item => item.user_id === userStore.id)
        }
        
        // Get the corresponding user names for each place
        const placesWithUserNames = await Promise.all(list.map(async (item) => {
          // Use the user_id field to get the user name
          const userName = await getUserNameById(item.user_id)
          return {
            id: item.id,
            name: item.name,
            describe: item.describe,
            country: item.country,
            city: item.city,
            address: item.address,
            createBy: userName
          }
        }))
        
        tableData.value = placesWithUserNames
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

// Open dialog to add new station
const handleAdd = () => {
  dialogTitle.value = 'Add Recycle Station'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Open dialog to edit existing station
const handleEdit = (row) => {
  dialogTitle.value = 'Edit Recycle Station'
  isEdit.value = true
  // Correctly map backend data to form fields
  formData.id = row.id
  formData.name = row.name
  formData.describe = row.describe
  formData.country = row.country
  formData.city = row.city
  formData.address = row.address
  dialogVisible.value = true
}

// Delete station with confirmation
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete station "${row.name}"?`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeletePlace({id: row.id})
      if (response.success) {
        ElMessage.success('Deleted successfully')
        fetchData()
      } else {
        ElMessage.error(response.message || 'Failed to delete')
      }
    } catch (error) {
      console.error('Failed to delete:', error)
      ElMessage.error('Failed to delete')
    }
  }).catch(() => {})
}

// Submit form data (add or update)
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        const userStore = useUserStore()
        if (isEdit.value) {
          const updateData = {
            id: formData.id,
            name: formData.name,
            describe: formData.describe,
            country: formData.country,
            city: formData.city,
            address: formData.address,
            user_id: userStore.id
          }
          response = await UpdatePlace(updateData)
        } else {
          const createData = {
            name: formData.name,
            describe: formData.describe,
            country: formData.country,
            city: formData.city,
            address: formData.address,
            user_id: userStore.id
          }
          response = await AddPlace(createData)
        }
        
        if (response.success) {
          ElMessage.success(isEdit.value ? 'Update successful' : 'Add successful')
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
    name: '',
    describe: '',
    country: '',
    city: '',
    address: ''
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

// Get username by user ID
const getUserNameById = async (userId) => {
  try {
    const response = await GetProfile({ userId: userId })
    if (response && response.success && response.data) {
      return response.data.name || 'Unknown User'
    }
    return 'Unknown User'
  } catch (error) {
    console.error('Failed to get user info:', error)
    return 'Unknown User'
  }
}

// Initialize data on component mount
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* Main container with soft background */
.place-management {
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

/* Station name cell styling */
.name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.name-icon {
  color: #409eff;
  font-size: 18px;
}

.name-text {
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

/* Description text styling */
.description-text {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Location cell styling */
.location-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.location-icon {
  font-size: 16px;
}

/* Creator cell styling */
.creator-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.creator-icon {
  color: #67c23a;
  font-size: 16px;
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

.place-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.place-form :deep(.el-input__wrapper) {
  border-radius: 6px;
}

.place-form :deep(.el-textarea__inner) {
  border-radius: 6px;
  line-height: 1.6;
  font-size: 14px;
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

/* Utility class for icon margin */
.mr-1 {
  margin-right: 4px;
}

/* Responsive design for mobile devices */
@media (max-width: 768px) {
  .place-management {
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