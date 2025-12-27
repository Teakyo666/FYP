<template>
  <div class="ai-management">
    <div class="header">
      <h1>AI-Generated Content Management</h1>
      <p>Manage AI-generated garbage classification content</p>
    </div>

    <!-- Search and operation bar -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-input
            v-model="searchForm.name"
            placeholder="Please enter garbage name"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch">Search</el-button>
            </template>
          </el-input>
          <el-select
            v-model="searchForm.status"
            placeholder="Please select status"
            clearable
            style="width: 150px; margin-left: 10px"
            @change="handleSearch"
          >
            <el-option label="Pending" value="pending" />
            <el-option label="Approved" value="approved" />
            <el-option label="Rejected" value="rejected" />
          </el-select>
          <el-button style="margin-left: 10px" @click="clearSearch">Clear Search</el-button>
        </el-col>
        <el-col :span="6" class="text-right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">Add AI Content</el-button>
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
        <el-table-column prop="name" label="Garbage Name" min-width="150" />
        <el-table-column prop="category" label="Category" width="120">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.category)">
              {{ getCategoryText(scope.row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="Classification Reason" min-width="200" />
        <el-table-column prop="title" label="Handling Suggestion" min-width="200" />
        <el-table-column prop="status" label="Status" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.status || 'Unknown Status' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Operations" width="250" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">Edit</el-button>
            <el-button 
              v-if="scope.row.status !== 'approved'" 
              size="small" 
              type="success" 
              @click="handleApprove(scope.row)"
            >
              Approve
            </el-button>
            <el-button 
              v-if="scope.row.status !== 'rejected'" 
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
        <el-form-item label="Garbage Name" prop="name">
          <el-input v-model="formData.name" placeholder="Please enter garbage name" />
        </el-form-item>
        <el-form-item label="Category" prop="category">
          <el-select v-model="formData.category" placeholder="Please select category" style="width: 100%">
            <el-option label="Recyclable" value="recyclable" />
            <el-option label="Hazardous Waste" value="hazardous" />
            <el-option label="Wet Garbage" value="wet" />
            <el-option label="Dry Garbage" value="dry" />
          </el-select>
        </el-form-item>
        <el-form-item label="Classification Reason" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="3"
            placeholder="Please enter classification reason"
          />
        </el-form-item>
        <el-form-item label="Handling Suggestion" prop="title">
          <el-input
            v-model="formData.title"
            type="textarea"
            :rows="3"
            placeholder="Please enter handling suggestion"
          />
        </el-form-item>
        <el-form-item label="Status" prop="status" v-if="isEdit">
          <el-select v-model="formData.status" placeholder="Please select status" style="width: 100%">
            <el-option label="Pending Review" value="pending" />
            <el-option label="Approved" value="approved" />
            <el-option label="Rejected" value="rejected" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { 
  AIGarbage as CreateAIGarbage,
  AIGarbageList as ListAIGarbage,
  AIGarbageupdate as UpdateAIGarbage
} from '@/api/AI'
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
  name: '',
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
  name: '',
  category: '',
  reason: '',
  title: '',
  status: 'pending'
})

// Form validation rules
const formRules = {
  name: [{ required: true, message: 'Please enter garbage name', trigger: 'blur' }],
  category: [{ required: true, message: 'Please select category', trigger: 'change' }],
  reason: [{ required: true, message: 'Please enter classification reason', trigger: 'blur' }],
  title: [{ required: true, message: 'Please enter handling suggestion', trigger: 'blur' }],
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
  searchForm.name = ''
  searchForm.status = ''
  pagination.currentPage = 1
  fetchData()
}

// Fetch data
const fetchData = async () => {
  try {
    loading.value = true
    
    // Call API to get AI-generated content list, supports pagination and search
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.name || undefined,
      status: searchForm.status || undefined // Fix: correctly use searchForm.status field
    }
    
    const response = await ListAIGarbage(params)
    
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

// Handle add
const handleAdd = () => {
  dialogTitle.value = 'Add AI Content'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Handle edit
const handleEdit = (row) => {
  dialogTitle.value = 'Edit AI Content'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// Handle approve
const handleApprove = async (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to approve the classification of "${row.name}"? After approval, it will be synchronized to the official garbage classification database.`,
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
  }).catch(() => {
    // User canceled operation
  })
}

// Handle reject
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
        ElMessage.success('Rejected')
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
      // Use DELETE request to delete AI content
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
  }).catch(() => {
    // User canceled deletion
  })
}

// Submit form
const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (isEdit.value) {
          // Edit - send all fields
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
          // Add - set default status to pending review
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
        console.error('Error details:', {
          message: error.message,
          code: error.code,
          response: error.response
        })
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

// Pagination related methods
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchData()
}

// Get tag type based on category
const getTagType = (category) => {
  const typeMap = {
    'Recyclable Waste': 'recoverable',
    'Hazardous waste': 'danger',
    'Kitchen waste': 'warning',
    'Non-recyclable waste': 'info'
  }
  return typeMap[category] || 'info'
}

// Get tag type based on status
const getStatusTagType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'info'
}

// Get display text based on category
const getCategoryText = (category) => {
  return category || 'Unknown category'
}

// Fetch data when component is mounted
onMounted(() => {
  fetchData()
})

</script>

<style scoped>
.ai-management {
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