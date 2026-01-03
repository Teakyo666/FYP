<template>
  <div class="sentence-management">
    <!-- Header with clean design -->
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>
            <span class="icon-wrapper">💬</span>
            Sentence Management
          </h1>
          <p>Manage environmental awareness sentences and slogans</p>
        </div>
        <div class="header-stats">
          <div class="stat-card">
            <div class="stat-icon">📝</div>
            <div class="stat-info">
              <div class="stat-number">{{ pagination.total }}</div>
              <div class="stat-label">Total Sentences</div>
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
                v-model="searchForm.sentence"
                placeholder="Search by sentence content..."
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
              <span>Add Sentence</span>
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
          <el-table-column prop="sentence" label="Sentence Content" min-width="350">
            <template #default="scope">
              <div class="sentence-cell">
                <el-icon class="sentence-icon"><ChatDotRound /></el-icon>
                <span class="sentence-text">{{ scope.row.sentence }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createBy" label="Creator" width="180">
            <template #default="scope">
              <div class="creator-cell">
                <el-icon class="creator-icon"><User /></el-icon>
                <span>{{ scope.row.createBy }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createDate" label="Created At" width="200">
            <template #default="scope">
              <div class="date-cell">
                <el-icon class="date-icon"><Calendar /></el-icon>
                <span>{{ formatDate(scope.row.createDate) }}</span>
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
        label-width="130px"
        label-position="left"
        class="sentence-form"
      >
        <el-form-item label="Sentence Content" prop="sentence">
          <el-input
            v-model="formData.sentence"
            type="textarea"
            :rows="6"
            placeholder="Enter an environmental awareness sentence or slogan"
            maxlength="500"
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
import { Search, Plus, Edit, Delete, ChatDotRound, User, Calendar, Check } from '@element-plus/icons-vue'
import { 
  ListSentence,
  CreateSentence, 
  UpdateSentence, 
  DeleteSentence 
} from '@/api/Sentence'
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
  sentence: ''
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
  sentence: '',
  createBy: '',
  createDate: ''
})

// Form validation rules
const formRules = {
  sentence: [{ required: true, message: 'Please enter sentence content', trigger: 'blur' }]
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
  searchForm.sentence = ''
  pagination.currentPage = 1
  fetchData()
}

// Fetch data from API
const fetchData = async () => {
  try {
    loading.value = true
    
    // Call the API to get the list of sentences, supporting pagination and search
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      sentence: searchForm.sentence || undefined
    }
    
    const response = await ListSentence(params)
    
    if (response && response.success) {
      // Ensure data structure is correct
      if (response.data && typeof response.data === 'object') {
        let list = Array.isArray(response.data.list) ? response.data.list : []
        
        // Get current user information
        const userStore = useUserStore()
        
        // If it's a non-admin user, only show sentences created by themselves
        if (userStore.role !== 'admin') {
          list = list.filter(item => item.createBy === userStore.id)
        }
        
        // Get the corresponding username for each sentence
        const sentencesWithUserNames = await Promise.all(list.map(async (item) => {
          // Assume the createBy field stores the user ID
          const userName = await getUserNameById(item.createBy)
          return {
            ...item,
            createBy: userName
          }
        }))
        
        tableData.value = sentencesWithUserNames
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

// Open dialog to add new sentence
const handleAdd = () => {
  dialogTitle.value = 'Add Sentence'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Open dialog to edit existing sentence
const handleEdit = (row) => {
  dialogTitle.value = 'Edit Sentence'
  isEdit.value = true
  formData.id = row.id
  formData.sentence = row.sentence
  formData.createBy = row.createBy
  formData.createDate = row.createDate
  dialogVisible.value = true
}

// Delete sentence with confirmation
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete sentence "${row.sentence.substring(0, 20)}${row.sentence.length > 20 ? '…' : ''}"?`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeleteSentence({id: row.id})
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
        if (isEdit.value) {
          const updateData = {
            id: formData.id,
            sentence: formData.sentence,
            createBy: formData.createBy,
            createDate: formData.createDate
          }
          response = await UpdateSentence(updateData)
        } else {
          // Get current logged-in user ID
          const userStore = useUserStore()
          const createData = {
            sentence: formData.sentence,
            createBy: userStore.id 
          }
          response = await CreateSentence(createData)
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
    sentence: '',
    createBy: '',
    createDate: ''
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
    // Use userId parameter to call userInfo/get API
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

// Format date for display
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// Initialize data on component mount
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* Main container with soft background */
.sentence-management {
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

/* Sentence cell styling */
.sentence-cell {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 4px 0;
}

.sentence-icon {
  color: #409eff;
  font-size: 18px;
  margin-top: 2px;
  flex-shrink: 0;
}

.sentence-text {
  color: #303133;
  font-weight: 500;
  line-height: 1.6;
  word-break: break-word;
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

/* Date cell styling */
.date-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.date-icon {
  color: #e6a23c;
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

.sentence-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.sentence-form :deep(.el-textarea__inner) {
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
  .sentence-management {
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