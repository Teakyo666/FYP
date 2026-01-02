<template>
  <div class="ai-management">
    <!-- Header with clean design -->
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>
            <span class="icon-wrapper">🤖</span>
            AI Content Management
          </h1>
          <p>Manage and review AI-generated waste classification content</p>
        </div>
        <div class="header-stats">
          <div class="stat-card pending">
            <div class="stat-icon">⏳</div>
            <div class="stat-info">
              <div class="stat-number">{{ getStatusCount('pending') }}</div>
              <div class="stat-label">Pending</div>
            </div>
          </div>
          <div class="stat-card approved">
            <div class="stat-icon">✅</div>
            <div class="stat-info">
              <div class="stat-number">{{ getStatusCount('approved') }}</div>
              <div class="stat-label">Approved</div>
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
                v-model="searchForm.name"
                placeholder="Search by waste name..."
                clearable
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-select
                v-model="searchForm.status"
                placeholder="Filter by status"
                clearable
                class="status-select"
                @change="handleSearch"
              >
                <el-option label="Pending" value="pending" />
                <el-option label="Approved" value="approved" />
                <el-option label="Rejected" value="rejected" />
              </el-select>
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
              <span>Add Content</span>
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
          <el-table-column prop="name" label="Waste Name" min-width="150">
            <template #default="scope">
              <div class="name-cell">
                <span class="name-text">{{ scope.row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="Category" width="150">
            <template #default="scope">
              <div class="category-cell">
                <el-tag 
                  :type="getTagType(scope.row.category)"
                  effect="plain"
                  class="category-tag"
                >
                  {{ getCategoryText(scope.row.category) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="Classification Reason" min-width="200">
            <template #default="scope">
              <div class="reason-text">{{ scope.row.reason }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="Handling Suggestion" min-width="200">
            <template #default="scope">
              <div class="suggestion-text">{{ scope.row.title }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status" width="120">
            <template #default="scope">
              <el-tag 
                :type="getStatusTagType(scope.row.status)"
                effect="plain"
                class="status-tag"
              >
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="320" fixed="right">
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
                  v-if="scope.row.status !== 'approved'" 
                  size="small" 
                  type="success"
                  plain
                  @click="handleApprove(scope.row)"
                  class="approve-btn"
                >
                  <el-icon><Check /></el-icon>
                  <span>Approve</span>
                </el-button>
                <el-button 
                  v-if="scope.row.status !== 'rejected'" 
                  size="small" 
                  type="warning"
                  plain
                  @click="handleReject(scope.row)"
                  class="reject-btn"
                >
                  <el-icon><Close /></el-icon>
                  <span>Reject</span>
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
        label-width="140px"
        label-position="left"
        class="content-form"
      >
        <el-form-item label="Waste Name" prop="name">
          <el-input 
            v-model="formData.name" 
            placeholder="Enter waste item name"
          />
        </el-form-item>
        <el-form-item label="Category" prop="category">
          <el-select 
            v-model="formData.category" 
            placeholder="Select waste category" 
            style="width: 100%"
          >
            <el-option label="Recyclable" value="recyclable">
              <span class="category-option">
                <span class="category-option-icon">♻️</span>
                <span>Recyclable</span>
              </span>
            </el-option>
            <el-option label="Hazardous Waste" value="hazardous">
              <span class="category-option">
                <span class="category-option-icon">☢️</span>
                <span>Hazardous Waste</span>
              </span>
            </el-option>
            <el-option label="Wet Garbage" value="wet">
              <span class="category-option">
                <span class="category-option-icon">🥬</span>
                <span>Wet Garbage</span>
              </span>
            </el-option>
            <el-option label="Dry Garbage" value="dry">
              <span class="category-option">
                <span class="category-option-icon">🗑️</span>
                <span>Dry Garbage</span>
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Classification Reason" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="3"
            placeholder="Explain why this item belongs to this category"
          />
        </el-form-item>
        <el-form-item label="Handling Suggestion" prop="title">
          <el-input
            v-model="formData.title"
            type="textarea"
            :rows="3"
            placeholder="Provide disposal or recycling suggestions"
          />
        </el-form-item>
        <el-form-item label="Status" prop="status" v-if="isEdit">
          <el-select 
            v-model="formData.status" 
            placeholder="Select review status" 
            style="width: 100%"
          >
            <el-option label="Pending Review" value="pending" />
            <el-option label="Approved" value="approved" />
            <el-option label="Rejected" value="rejected" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Check, Close } from '@element-plus/icons-vue'
import { 
  AIGarbage as CreateAIGarbage,
  AIGarbageList as ListAIGarbage,
  AIGarbageupdate as UpdateAIGarbage
} from '@/api/AI'
import request from "@/utils/request";

// Reactive state variables
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// Table data storage
const tableData = ref([])

// Search form data
const searchForm = reactive({
  name: '',
  status: ''
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
  category: '',
  reason: '',
  title: '',
  status: 'pending'
})

// Form validation rules
const formRules = {
  name: [{ required: true, message: 'Please enter waste name', trigger: 'blur' }],
  category: [{ required: true, message: 'Please select category', trigger: 'change' }],
  reason: [{ required: true, message: 'Please enter classification reason', trigger: 'blur' }],
  title: [{ required: true, message: 'Please enter handling suggestion', trigger: 'blur' }],
  status: [{ required: true, message: 'Please select status', trigger: 'change' }]
}

// Form reference for validation
const formRef = ref()

// Calculate status count for stats
const getStatusCount = (status) => {
  return tableData.value.filter(item => item.status === status).length
}

// Handle search action
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// Clear search filters
const clearSearch = () => {
  searchForm.name = ''
  searchForm.status = ''
  pagination.currentPage = 1
  fetchData()
}

// Fetch data from API
const fetchData = async () => {
  try {
    loading.value = true
    
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.name || undefined,
      status: searchForm.status || undefined
    }
    
    const response = await ListAIGarbage(params)
    
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

// Open dialog to add new content
const handleAdd = () => {
  dialogTitle.value = 'Add AI Content'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Open dialog to edit existing content
const handleEdit = (row) => {
  dialogTitle.value = 'Edit AI Content'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// Approve AI content
const handleApprove = async (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to approve the classification of "${row.name}"? After approval, it will be synchronized to the official waste classification database.`,
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
        name: row.name,
        category: row.category,
        reason: row.reason,
        title: row.title,
        status: 'approved'
      }
      const response = await UpdateAIGarbage(updateData)
      if (response.success) {
        ElMessage.success('Approved and synchronized to official database')
        fetchData()
      } else {
        ElMessage.error(response.message || 'Operation failed')
      }
    } catch (error) {
      console.error('Operation failed:', error)
      ElMessage.error('Operation failed')
    }
  }).catch(() => {})
}

// Reject AI content
const handleReject = async (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to reject the classification of "${row.name}"?`,
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
        name: row.name,
        category: row.category,
        reason: row.reason,
        title: row.title,
        status: 'rejected'
      }
      const response = await UpdateAIGarbage(updateData)
      if (response.success) {
        ElMessage.success('Content rejected')
        fetchData()
      } else {
        ElMessage.error(response.message || 'Operation failed')
      }
    } catch (error) {
      console.error('Operation failed:', error)
      ElMessage.error('Operation failed')
    }
  }).catch(() => {})
}

// Delete AI content with confirmation
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete AI content "${row.name}"?`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await request({
        url: "AI/delete",
        method: "post",
        params: { id: row.id }
      })
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
            name: formData.name,
            category: formData.category,
            reason: formData.reason,
            title: formData.title,
            status: formData.status
          }
          response = await UpdateAIGarbage(updateData)
        } else {
          const createData = {
            name: formData.name,
            category: formData.category,
            reason: formData.reason,
            title: formData.title,
            status: 'pending'
          }
          response = await CreateAIGarbage(createData)
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
    name: '',
    category: '',
    reason: '',
    title: '',
    status: 'pending'
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

// Get tag color type based on category
const getTagType = (category) => {
  const typeMap = {
    'recyclable': 'success',
    'hazardous': 'danger',
    'wet': 'warning',
    'dry': ''
  }
  return typeMap[category] || ''
}

// Get category display text
const getCategoryText = (category) => {
  const textMap = {
    'recyclable': 'Recyclable',
    'hazardous': 'Hazardous',
    'wet': 'Wet Waste',
    'dry': 'Dry Waste'
  }
  return textMap[category] || category || 'Unknown'
}

// Get category icon
const getCategoryIcon = (category) => {
  const iconMap = {
    'recyclable': '♻️',
    'hazardous': '☢️',
    'wet': '🥬',
    'dry': '🗑️'
  }
  return iconMap[category] || '📦'
}

// Get status tag color type
const getStatusTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || ''
}

// Get status display text
const getStatusText = (status) => {
  const textMap = {
    'pending': 'Pending',
    'approved': 'Approved',
    'rejected': 'Rejected'
  }
  return textMap[status] || status || 'Unknown'
}

// Initialize data on component mount
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* Main container with soft background */
.ai-management {
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

/* Statistics cards with color coding */
.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  padding: 16px 24px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 16px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-card.pending {
  background: linear-gradient(135deg, #f39c12 0%, #f1c40f 100%);
}

.stat-card.approved {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
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
  width: 300px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.status-select {
  width: 180px;
}

.status-select :deep(.el-input__wrapper) {
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

/* Waste name cell styling */
.name-cell {
  display: flex;
  align-items: center;
}

.name-text {
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

/* Category cell styling */
.category-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-icon {
  font-size: 18px;
}

.category-tag {
  font-weight: 600;
  font-size: 13px;
  border: none;
}

/* Reason and suggestion text */
.reason-text,
.suggestion-text {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Status tag styling */
.status-tag {
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

.content-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.content-form :deep(.el-input__wrapper) {
  border-radius: 6px;
}

.content-form :deep(.el-textarea__inner) {
  border-radius: 6px;
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

/* Category option in select dropdown */
.category-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-option-icon {
  font-size: 16px;
}

/* Utility class for icon margin */
.mr-1 {
  margin-right: 4px;
}

/* Responsive design for mobile devices */
@media (max-width: 768px) {
  .ai-management {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }
  
  .header-stats {
    width: 100%;
    flex-direction: column;
  }
  
  .stat-card {
    width: 100%;
  }
  
  .search-container {
    flex-direction: column;
    width: 100%;
  }
  
  .search-input,
  .status-select {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>