<template>
  <div class="place-management">
    <div class="header">
      <h1>地点管理</h1>
      <p>管理系统中的地点信息</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入地点名称"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
          <el-button style="margin-left: 10px" @click="clearSearch">清除搜索</el-button>
        </el-col>
        <el-col :span="6" class="text-right">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增地点</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table
        :data="tableData"
        v-loading="loading"
        element-loading-text="加载中..."
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="describe" label="描述" min-width="200" />
        <el-table-column prop="country" label="国家" width="120" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="createBy" label="创建人" width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
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

    <!-- 编辑/新增对话框 -->
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
        <el-form-item label="地点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入地点名称" />
        </el-form-item>
        <el-form-item label="描述" prop="describe">
          <el-input
            v-model="formData.describe"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <el-input v-model="formData.country" placeholder="请输入国家" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="formData.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
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

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入地点名称', trigger: 'blur' }],
  describe: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  country: [{ required: true, message: '请输入国家', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
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

// 处理新增
const handleAdd = () => {
  dialogTitle.value = '新增地点'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑地点'
  isEdit.value = true
  // 正确映射后端返回的数据到表单字段
  formData.id = row.id
  formData.name = row.name
  formData.describe = row.describe
  formData.country = row.country
  formData.city = row.city
  formData.address = row.address
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除地点 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeletePlace({id: row.id})
      if (response.success) {
        ElMessage.success('删除成功')
        fetchData()
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 用户取消删除
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
          // 编辑 - 发送正确的字段
          const updateData = {
            id: formData.id,
            name: formData.name,
            describe: formData.describe,
            country: formData.country,
            city: formData.city,
            address: formData.address,
            user_id: userStore.id  // 修复：使用userStore.id而不是userStore.user_id
          }
          response = await UpdatePlace(updateData)
        } else {
          // 新增
          const createData = {
            name: formData.name,
            describe: formData.describe,
            country: formData.country,
            city: formData.city,
            address: formData.address,
            user_id: userStore.id  // 使用user_id字段关联用户
          }
          response = await AddPlace(createData)
        }
        
        if (response.success) {
          ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
          dialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.message || (isEdit.value ? '更新失败' : '新增失败'))
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '新增失败')
      }
    }
  })
}

// 关闭对话框前的处理
const handleDialogClose = (done) => {
  resetForm()
  done()
}

// 重置表单
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

// 分页相关方法
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  fetchData()
}

// 根据用户ID获取用户名
const getUserNameById = async (userId) => {
  try {
    // 使用 userId 参数调用 userInfo/get 接口
    const response = await GetProfile({ userId: userId })
    if (response && response.success && response.data) {
      return response.data.name || '未知用户'
    }
    return '未知用户'
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return '未知用户'
  }
}

// 组件挂载时获取数据
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