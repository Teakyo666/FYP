<template>
  <div class="place-management">
    <div class="header">
      <h1>Place Management</h1>
      <p>Manage place information in the system</p>
    </div>

    <!-- Search and operation bar -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-input
            v-model="searchForm.name"
            placeholder="Please enter place name"
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
          <el-button type="primary" :icon="Plus" @click="handleAdd">Add Place</el-button>
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
        <el-table-column prop="name" label="Name" min-width="150" />
        <el-table-column prop="describe" label="Description" min-width="200" />
        <el-table-column prop="country" label="Country" width="120" />
        <el-table-column prop="city" label="City" width="120" />
        <el-table-column prop="createBy" label="Creator" width="150" />
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
        <el-form-item label="Place Name" prop="name">
          <el-input v-model="formData.name" placeholder="Please enter place name" />
        </el-form-item>
        <el-form-item label="Description" prop="describe">
          <el-input
            v-model="formData.describe"
            type="textarea"
            :rows="3"
            placeholder="Please enter description"
          />
        </el-form-item>
        <el-form-item label="Country" prop="country">
          <el-input v-model="formData.country" placeholder="Please enter country" />
        </el-form-item>
        <el-form-item label="City" prop="city">
          <el-input v-model="formData.city" placeholder="Please enter city" />
        </el-form-item>
        <el-form-item label="Address" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="3"
            placeholder="Please enter address"
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
  ListPlace,
  AddPlace, 
  UpdatePlace, 
  DeletePlace 
} from '@/api/Place'
import { GetProfile } from '@/api/Home'
import { useUserStore } from '@/store/index'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// 表格数据
const tableData = ref([])

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
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
  name: [{ required: true, message: 'Please enter place name', trigger: 'blur' }],
  describe: [{ required: true, message: 'Please enter description', trigger: 'blur' }],
  country: [{ required: true, message: 'Please enter country', trigger: 'blur' }],
  city: [{ required: true, message: 'Please enter city', trigger: 'blur' }],
  address: [{ required: true, message: 'Please enter address', trigger: 'blur' }]
}

// 表单引用
const formRef = ref()

// 搜索处理
const handleSearch = () => {
  pagination.currentPage = 1
  fetchData()
}

// 清除搜索处理
const clearSearch = () => {
  searchForm.name = ''
  pagination.currentPage = 1
  fetchData()
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    // 调用获取地点列表的API，支持分页和搜索
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.name || undefined
    }
    
    const response = await ListPlace(params)
    
    if (response && response.success) {
      // 确保数据结构正确
      if (response.data && typeof response.data === 'object') {
        let list = Array.isArray(response.data.list) ? response.data.list : []
        
        // 获取当前用户信息
        const userStore = useUserStore()
        
        // 如果是非管理员用户，只显示自己创建的地点
        // 注意：API返回的字段是user_id而不是createBy
        if (userStore.role !== 'admin') {
          list = list.filter(item => item.user_id === userStore.id)
        }
        
        // 为每个地点获取对应的用户名
        const placesWithUserNames = await Promise.all(list.map(async (item) => {
          // 使用user_id字段获取用户名
          const userName = await getUserNameById(item.user_id)
          return {
            id: item.id,
            name: item.name,
            describe: item.describe,
            country: item.country,
            city: item.city,
            address: item.address,
            createBy: userName // 将用户ID替换为用户名
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
      const errorMsg = response ? (response.message || '获取数据失败') : '服务器无响应'
      ElMessage.error(errorMsg)
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// Handle add
const handleAdd = () => {
  dialogTitle.value = 'Add Place'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// Handle edit
const handleEdit = (row) => {
  dialogTitle.value = 'Edit Place'
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

// Handle delete
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `Are you sure you want to delete place "${row.name}"?`,
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
  }).catch(() => {
    // User cancelled deletion
  })
}

// 提交表单
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

const handleDialogClose = (done) => {
  resetForm()
  done()
}

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

const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchData()
}

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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.place-management {
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