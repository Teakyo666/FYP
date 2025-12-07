<template>
  <div class="user-management">
    <div class="header">
      <h1>用户管理</h1>
      <p>管理系统中的用户信息</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
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
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column prop="password" label="密码" min-width="150" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="信息" width="100" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleViewInfo(scope.row)">查看</el-button>
          </template>
        </el-table-column>

        <!-- 查看用户信息处理 -->
        <el-table-column label="操作" width="150" fixed="right">
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="志愿者" value="volunteer" />
            <el-option label="回收员" value="recycler" />
            <el-option label="普通用户" value="customer" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 用户信息查看对话框 -->
    <el-dialog
      v-model="infoDialogVisible"
      title="用户信息详情"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        label-width="80px"
        label-position="left"
      >
        <el-form-item label="姓名">
          <span v-if="!isEditing">{{ userInfo.name }}</span>
          <el-input v-else v-model="userInfo.name" />
        </el-form-item>
        <el-form-item label="城市">
          <span v-if="!isEditing">{{ userInfo.city }}</span>
          <el-input v-else v-model="userInfo.city" />
        </el-form-item>
        <el-form-item label="国家">
          <span v-if="!isEditing">{{ userInfo.country }}</span>
          <el-input v-else v-model="userInfo.country" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button v-if="!isEditing" @click="enableEditing">修改</el-button>
          <template v-else>
            <el-button type="primary" @click="saveUserInfo">保存</el-button>
            <el-button @click="cancelEditing">取消</el-button>
          </template>
          <el-button @click="infoDialogVisible = false">关闭</el-button>
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

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const infoDialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const isEditing = ref(false)
const currentUserInfo = ref({}) // 保存当前查看的用户信息

// 表格数据
const tableData = ref([])

// 搜索表单
const searchForm = reactive({
  username: ''
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
  username: '',
  password: '',
  role: ''
})

// 用户信息数据
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

// 表单验证规则
const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
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
  searchForm.username = ''
  pagination.currentPage = 1
  fetchData()
}

// 查看用户信息处理
const handleViewInfo = async (row) => {
  try {
    const response = await GetProfile({ userId: row.id })
    if (response && response.success) {
      // 保存当前用户信息
      currentUserInfo.value = response.data || {}
      
      // 设置需要显示的字段
      userInfo.name = (response.data && response.data.name) || ''
      userInfo.city = (response.data && response.data.city) || ''
      userInfo.country = (response.data && response.data.country) || ''
      
      // 重置编辑状态
      isEditing.value = false
      
      // 显示信息对话框
      infoDialogVisible.value = true
    } else {
      ElMessage.error(response.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败: ' + (error.message || '未知错误'))
  }
}

// 启用编辑模式
const enableEditing = () => {
  isEditing.value = true
}

// 保存用户信息
const saveUserInfo = async () => {
  try {
    const response = await UpdateProfile({
      userId: currentUserInfo.value.id,
      name: userInfo.value.name,
      city: userInfo.value.city,
      country: userInfo.value.country
    })
    
    if (response && response.success) {
      ElMessage.success('用户信息更新成功')
      isEditing.value = false
      // 更新currentUserInfo中的数据
      currentUserInfo.value.name = userInfo.value.name
      currentUserInfo.value.city = userInfo.value.city
      currentUserInfo.value.country = userInfo.value.country
      // 可能还需要刷新用户列表或其他操作
    } else {
      ElMessage.error(response.message || '更新失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新用户信息失败: ' + (error.message || '未知错误'))
  }
}

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false
  // 恢复原始数据
  userInfo.value.name = currentUserInfo.value.name || ''
  userInfo.value.city = currentUserInfo.value.city || ''
  userInfo.value.country = currentUserInfo.value.country || ''
}

// 格式化标签显示名称
const formatLabel = (key) => {
  const labelMap = {
    'id': '用户ID',
    'username': '用户名',
    'role': '角色',
    'name': '姓名',
    'email': '邮箱',
    'phone': '电话',
    'address': '地址',
    'city': '城市',
    'country': '国家'
  }
  // 如果找不到对应的标签，则将键名转换为中文（首字母大写）
  if (!labelMap[key]) {
    // 尝试将驼峰命名转换为中文描述
    const camelKey = key.replace(/([A-Z])/g, ' $1').trim();
    return camelKey.charAt(0).toUpperCase() + camelKey.slice(1);
  }
  return labelMap[key];
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    // 调用获取用户列表的API，支持分页和搜索
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.username || undefined
    }
    
    const response = await UserList(params)
    
    if (response && response.success) {
      // 确保数据结构正确
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
  dialogTitle.value = '新增用户'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  isEdit.value = true
  Object.assign(formData, row)
  // 清空密码字段，编辑时不强制修改密码
  formData.password = ''
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await UserDelete({ Id: row.id })
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
        if (isEdit.value) {
          // 编辑 - 只发送必要的字段
          const updateData = {
            Id: formData.id,
            username: formData.username,
            password: formData.password,
            role: formData.role
          }
          
          response = await UserUpdate(updateData)
        } else {
          // 新增
          const createData = {
            username: formData.username,
            password: formData.password,
            role: formData.role
          }
          response = await UserRegister(createData)
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
    username: '',
    password: '',
    role: ''
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

// 根据角色获取标签类型
const getRoleTagType = (role) => {
  const typeMap = {
    'admin': 'danger',
    'volunteer': 'success',
    'recycler': 'warning',
    'customer': 'info'
  }
  return typeMap[role] || 'info'
}

// 根据角色获取显示文本
const getRoleText = (role) => {
  const roleMap = {
    'admin': '管理员',
    'volunteer': '志愿者',
    'recycler': '回收员',
    'customer': '普通用户'
  }
  return roleMap[role] || '未知角色'
}

// 组件挂载时获取数据
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
