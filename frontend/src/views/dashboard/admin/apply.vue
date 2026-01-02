<template>
  <div class="apply-management">
    <!-- Header with clean design -->
    <div class="header">
      <div class="header-content">
        <div class="header-text">
          <h1>
            <span class="icon-wrapper">📋</span>
            Apply Management
          </h1>
          <p>Review and manage user role change apply List</p>
        </div>
        <div class="header-stats">
          <div class="stat-card">
            <div class="stat-icon">📝</div>
            <div class="stat-info">
              <div class="stat-number">{{ pagination.total }}</div>
              <div class="stat-label">Total Apply List</div>
            </div>
          </div>
          <div class="stat-card-small pending">
            <div class="stat-small-icon">⏳</div>
            <div class="stat-small-text">{{ getStatusCount('pending') }}</div>
          </div>
          <div class="stat-card-small approved">
            <div class="stat-small-icon">✅</div>
            <div class="stat-small-text">{{ getStatusCount('approved') }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Search and operation toolbar -->
    <div class="toolbar">
      <el-card shadow="hover" class="toolbar-card">
        <el-row :gutter="20" align="middle">
          <el-col :span="24">
            <div class="search-container">
              <el-select
                v-model="searchForm.to_apply"
                placeholder="Filter by apply type"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option label="Recycler" value="recycler">
                  <span class="filter-option">
                    <span class="filter-icon">♻️</span>
                    <span>Recycler</span>
                  </span>
                </el-option>
                <el-option label="Volunteer" value="volunteer">
                  <span class="filter-option">
                    <span class="filter-icon">🤝</span>
                    <span>Volunteer</span>
                  </span>
                </el-option>
              </el-select>
              <el-select
                v-model="searchForm.status"
                placeholder="Filter by status"
                clearable
                class="search-select"
                @change="handleSearch"
              >
                <el-option label="Pending" value="pending" />
                <el-option label="Approved" value="approved" />
                <el-option label="Rejected" value="rejected" />
              </el-select>
              <el-button 
                @click="clearSearch"
                class="clear-btn"
              >
                Clear Filters
              </el-button>
            </div>
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
          <el-table-column prop="to_apply" label="Apply Type" min-width="150">
            <template #default="scope">
              <div class="type-cell">
                <span class="type-icon">{{ getTypeIcon(scope.row.to_apply) }}</span>
                <span class="type-text">{{ formatType(scope.row.to_apply) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="Apply Reason" min-width="250">
            <template #default="scope">
              <div class="reason-text">{{ scope.row.reason }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="user_id" label="User ID" width="220">
            <template #default="scope">
              <div class="user-cell">
                <el-icon class="user-icon"><User /></el-icon>
                <span class="user-id">{{ scope.row.user_id }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status" width="130">
            <template #default="scope">
              <el-tag 
                :type="getStatusTagType(scope.row.status)"
                effect="plain"
                class="status-tag"
              >
                {{ formatStatus(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="350" fixed="right">
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
                  v-if="scope.row.status === 'pending'"
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
                  v-if="scope.row.status === 'pending'"
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

    <!-- Edit dialog with modern design -->
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
        class="apply-form"
      >
        <el-form-item label="Apply Type" prop="to_apply">
          <el-select 
            v-model="formData.to_apply" 
            placeholder="Select apply type" 
            style="width: 100%"
          >
            <el-option label="Recycler" value="recycler">
              <span class="type-option">
                <span class="type-option-icon">♻️</span>
                <span>Recycler</span>
              </span>
            </el-option>
            <el-option label="Volunteer" value="volunteer">
              <span class="type-option">
                <span class="type-option-icon">🤝</span>
                <span>Volunteer</span>
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Apply Reason" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="5"
            placeholder="Enter the reason for this role change apply"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="User ID" prop="user_id">
          <el-input 
            v-model="formData.user_id" 
            placeholder="Enter user ID"
            disabled
          />
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select 
            v-model="formData.status" 
            placeholder="Select apply status" 
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
            <span>Save Changes</span>
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, User, Check, Close } from '@element-plus/icons-vue'
import { ListApply, UpdateApply, DeleteApply } from '@/api/Apply'

// Reactive state variables
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// Table data storage
const tableData = ref([])

// Search form data
const searchForm = reactive({
  to_apply: '',
  status: ''
})

// Pagination configuration
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// Form data for edit operations
const formData = reactive({
  id: '',
  user_id: '',
  to_apply: '',
  reason: '',
  status: 'pending'
})

// Form validation rules
const formRules = {
  to_apply: [{ required: true, message: 'Please select apply type', trigger: 'change' }],
  reason: [{ required: true, message: 'Please enter apply reason', trigger: 'blur' }],
  user_id: [{ required: true, message: 'Please enter user ID', trigger: 'blur' }],
  status: [{ required: true, message: 'Please select status', trigger: 'change' }]
}

// Form reference for validation
const formRef = ref()

// Calculate status count for stats
const getStatusCount = (status) => {
  return tableData.value.filter(item => item.status === status).length
}

// Get status tag color type
const getStatusTagType = (status) => {
  switch (status) {
    case 'approved': return 'success'
    case 'rejected': return 'danger'
    case 'pending': return 'warning'
    default: return ''
  }
}

// Format status text
const formatStatus = (status) => {
  if (!status) return 'Unknown'
  return status.charAt(0).toUpperCase() + status.slice(1)
}

// Get apply type icon
const getTypeIcon = (type) => {
  const iconMap = {
    'recycler': '♻️',
    'volunteer': '🤝'
  }
  return iconMap[type] || '📋'
}

// Format apply type text
const formatType = (type) => {
  if (!type) return 'Unknown'
  return type.charAt(0).toUpperCase() + type.slice(1)
}

// Handle search action
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// Clear search filters
const clearSearch = () => {
  searchForm.to_apply = ''
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
      to_apply: searchForm.to_apply || undefined,
      status: searchForm.status || undefined
    }
    const response = await ListApply(params)
    if (response?.success && response.data) {
      tableData.value = Array.isArray(response.data.list) ? response.data.list : []
      pagination.total = Number(response.data.total) || 0
    } else {
      tableData.value = []
      pagination.total = 0
      ElMessage.error(response?.message || 'Failed to load data')
    }
  } catch (error) {
    console.error('Fetch error:', error)
    ElMessage.error('Failed to load data')
  } finally {
    loading.value = false
  }
}

// Open dialog to edit apply
const handleEdit = (row) => {
  Object.assign(formData, { ...row })
  dialogTitle.value = 'Edit Apply'
  isEdit.value = true
  dialogVisible.value = true
}

// Approve apply
const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to approve this apply? The user\'s role will be updated.',
      'Confirm Approval',
      { confirmButtonText: 'Approve', cancelButtonText: 'Cancel', type: 'success' }
    )
    const response = await UpdateApply({ ...row, status: 'approved' })
    if (response?.success) {
      ElMessage.success('Apply approved successfully')
      fetchData()
    } else {
      ElMessage.error(response?.message || 'Approval failed')
    }
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('Operation cancelled or failed')
  }
}

// Reject apply
const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm(
      'Are you sure you want to reject this apply?',
      'Confirm Rejection',
      { confirmButtonText: 'Reject', cancelButtonText: 'Cancel', type: 'warning' }
    )
    const response = await UpdateApply({ ...row, status: 'rejected' })
    if (response?.success) {
      ElMessage.success('Apply rejected successfully')
      fetchData()
    } else {
      ElMessage.error(response?.message || 'Rejection failed')
    }
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('Operation cancelled or failed')
  }
}

// Delete apply with confirmation
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      'This action cannot be undone. Delete this apply?',
      'Confirm Deletion',
      { confirmButtonText: 'Delete', cancelButtonText: 'Cancel', type: 'danger' }
    )
    const response = await DeleteApply(row.id)
    if (response?.success) {
      ElMessage.success('Apply List deleted successfully')
      fetchData()
    } else {
      ElMessage.error(response?.message || 'Deletion failed')
    }
  } catch (err) {
    if (err !== 'cancel') ElMessage.error('Operation cancelled or failed')
  }
}

// Handle dialog close event
const handleDialogClose = () => {
  dialogVisible.value = false
  formRef.value?.clearValidate()
}

// Submit form validation and data
const handleSubmit = async () => {
  await formRef.value?.validate((valid) => {
    if (valid) submitForm()
    else ElMessage.error('Please correct the form errors')
  })
}

// Submit form data to API
const submitForm = async () => {
  try {
    const response = await UpdateApply(formData)
    if (response?.success) {
      ElMessage.success('Apply List saved successfully')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(response?.message || 'Save failed')
    }
  } catch (error) {
    ElMessage.error('An error occurred while saving')
  }
}

// Handle page size change
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.currentPage = 1
  fetchData()
}

// Handle current page change
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchData()
}

// Initialize data on component mount
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* Main container with soft background */
.apply-management {
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
  gap: 12px;
  align-items: center;
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

/* Small status stat cards */
.stat-card-small {
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  min-width: 70px;
}

.stat-card-small.pending {
  background: linear-gradient(135deg, #f39c12 0%, #f1c40f 100%);
}

.stat-card-small.approved {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%);
}

.stat-small-icon {
  font-size: 24px;
}

.stat-small-text {
  font-size: 18px;
  font-weight: 700;
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

.search-select {
  width: 220px;
}

.search-select :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.clear-btn {
  border-radius: 8px;
  height: 40px;
  padding: 0 20px;
}

/* Filter option styling */
.filter-option,
.type-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-icon,
.type-option-icon {
  font-size: 16px;
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

/* Apply type cell styling */
.type-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.type-icon {
  font-size: 20px;
}

.type-text {
  color: #303133;
  font-weight: 600;
  font-size: 14px;
}

/* Reason text styling */
.reason-text {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* User cell styling */
.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-icon {
  color: #409eff;
  font-size: 16px;
}

.user-id {
  color: #606266;
  font-size: 13px;
  font-family: 'Monaco', 'Menlo', 'Courier New', monospace;
  font-weight: 500;
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

.apply-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.apply-form :deep(.el-input__wrapper) {
  border-radius: 6px;
}

.apply-form :deep(.el-textarea__inner) {
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
  .apply-management {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }
  
  .header-stats {
    width: 100%;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .stat-card,
  .stat-card-small {
    flex: 1;
    min-width: 100px;
  }
  
  .search-container {
    flex-direction: column;
    width: 100%;
  }
  
  .search-select {
    width: 100%;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>