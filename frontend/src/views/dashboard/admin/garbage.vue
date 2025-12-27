<template>
  <div class="garbage-management">
    <div class="header">
      <h1>Garbage Management</h1>
      <p>Manage garbage classification information in the system</p>
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
          <el-button style="margin-left: 10px" @click="clearSearch">Clear Search</el-button>
        </el-col>
        <el-col :span="6" class="text-right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">Add Garbage</el-button>
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
        <el-table-column prop="name" label="Garbage Name" min-width="150" />
        <el-table-column prop="category" label="Category" width="120">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.category)">
              {{ getCategoryText(scope.row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="Classification Reason" min-width="200" />
        <el-table-column prop="title" label="Disposal Suggestion" min-width="200" />
        <el-table-column label="Operations" width="200" fixed="right">
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
        <el-form-item label="Garbage Name" prop="name">
          <el-input v-model="formData.name" placeholder="Please enter garbage name" />
        </el-form-item>
        <el-form-item label="Category" prop="category">
          <el-select v-model="formData.category" placeholder="Please select category" style="width: 100%">
            <el-option label="Recyclable" value="Recyclable" />
            <el-option label="Hazardous Waste" value="Hazardous Waste" />
            <el-option label="Wet Waste" value="Wet Waste" />
            <el-option label="Dry Waste" value="Dry Waste" />
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
        <el-form-item label="Disposal Suggestion" prop="title">
          <el-input
            v-model="formData.title"
            type="textarea"
            :rows="3"
            placeholder="Please enter disposal suggestion"
          />
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
  ListGarbage,
  CreateGarbage, 
  UpdateGarbage, 
  DeleteGarbage 
} from '@/api/Garbage'

// Reactive data
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// Table data
const tableData = ref([])

// Search form
const searchForm = reactive({
  name: ''
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
  title: ''
})

// Form validation rules
const formRules = {
  name: [{ required: true, message: 'Please enter garbage name', trigger: 'blur' }],
  category: [{ required: true, message: 'Please select category', trigger: 'change' }],
  reason: [{ required: true, message: 'Please enter classification reason', trigger: 'blur' }],
  title: [{ required: true, message: 'Please enter disposal suggestion', trigger: 'blur' }]
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
  pagination.currentPage = 1
  fetchData()
}

// Fetch data
const fetchData = async () => {
  try {
    loading.value = true
    
    // Call the API to get the garbage list, supporting pagination and search
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.name || undefined
    }
    
    const response = await ListGarbage(params)
    
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
  dialogTitle.value = 'Add Garbage'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Handle edit
const handleEdit = (row) => {
  dialogTitle.value = 'Edit Garbage'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// Handle delete
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete garbage "${row.name}"?`,
    'Delete Confirmation',
    {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeleteGarbage({ Id: row.id })
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
            name: formData.name,
            category: formData.category,
            reason: formData.reason,
            title: formData.title
          }
          response = await UpdateGarbage(updateData)
        } else {
          // Add
          const createData = {
            name: formData.name,
            category: formData.category,
            reason: formData.reason,
            title: formData.title
          }
          response = await CreateGarbage(createData)
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
    title: ''
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
    'Recyclable': 'success',
    'Hazardous Waste': 'danger',
    'Wet Waste': 'warning',
    'Dry Waste': 'info'
  }
  return typeMap[category] || 'info'
}

// Get display text based on category
const getCategoryText = (category) => {
  return category || 'Unknown Category'
}

// Fetch data when component mounts
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.garbage-management {
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