<template>
  <div class="apply-management">
    <div class="header">
      <h1>申请内容管理</h1>
      <p>管理用户提交的申请内容</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-select
            v-model="searchForm.to_apply"
            placeholder="请选择申请权限"
            clearable
            style="width: 300px"
            @change="handleSearch"
          >
            <el-option label="回收员" value="recycler" />
            <el-option label="志愿者" value="volunteer" />
          </el-select>
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px; margin-left: 10px"
            @change="handleSearch"
          >
            <el-option label="待审核" value="待审核" />
            <el-option label="同意" value="同意" />
            <el-option label="拒绝" value="拒绝" />
          </el-select>
          <el-button style="margin-left: 10px" @click="clearSearch">清除搜索</el-button>
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
        <el-table-column prop="to_apply" label="申请权限" min-width="50" />
        <el-table-column prop="reason" label="申请原因" min-width="200" />
        <el-table-column prop="user_id" label="用户ID" width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ scope.row.status || '未知状态' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              v-if="scope.row.status !== '同意'" 
              size="small" 
              type="success" 
              @click="handleApprove(scope.row)"
            >
              同意
            </el-button>
            <el-button 
              v-if="scope.row.status !== '拒绝'" 
              size="small" 
              type="warning" 
              @click="handleReject(scope.row)"
            >
              拒绝
            </el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { 
  ListApply,
  UpdateApply,
  DeleteApply
} from '@/api/Apply'
import request from "@/utils/request";

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// 表格数据
const tableData = ref([])

// 搜索表单
const searchForm = reactive({
  to_apply: '',
  status: ''
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
  user_id: '',
  to_apply: '',
  reason: '',
  status: '待审核' // 默认状态
})

// 表单验证规则
const formRules = {
  to_apply: [{ required: true, message: '请输入申请内容', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }],
  user_id: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
  searchForm.to_apply = ''
  searchForm.status = ''
  pagination.currentPage = 1
  fetchData()
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    // 调用获取申请内容列表的API，支持分页和搜索
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      to_apply: searchForm.to_apply || undefined,  // 修改这里，使用to_apply而不是keyword
      status: searchForm.status || undefined
    }
    
    const response = await ListApply(params)
    
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

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑申请内容'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 处理同意
const handleApprove = async (row) => {
  ElMessageBox.confirm(
    `确定要同意"${row.to_apply}"的申请吗？`,
    '同意确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const updateData = {
        id: row.id,
        user_id: row.user_id,
        to_apply: row.to_apply,
        reason: row.reason,
        status: '同意'
      }
      const response = await UpdateApply(updateData)
      if (response.success) {
        ElMessage.success('已同意申请')
        fetchData()
      } else {
        ElMessage.error(response.message || '操作失败')
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 处理拒绝
const handleReject = async (row) => {
  ElMessageBox.confirm(
    `确定要拒绝"${row.to_apply}"的申请吗？`,
    '拒绝确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const updateData = {
        id: row.id,
        user_id: row.user_id,
        to_apply: row.to_apply,
        reason: row.reason,
        status: '拒绝'
      }
      const response = await UpdateApply(updateData)
      if (response.success) {
        ElMessage.success('已拒绝申请')
        fetchData()
      } else {
        ElMessage.error(response.message || '操作失败')
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 处理删除
const handleDelete = async (row) => {
  ElMessageBox.confirm(
    `确定要删除"${row.to_apply}"的申请吗？此操作不可恢复！`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeleteApply({ id: row.id })
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
    // 用户取消操作
  })
}

// 表单提交处理
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 只保留编辑模式，移除新增模式
        const response = await UpdateApply(formData)
        
        if (response.success) {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          resetForm()
          fetchData()
        } else {
          ElMessage.error(response.message || '更新失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败: ' + (error.message || '未知错误'))
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
    user_id: '',
    to_apply: '',
    reason: '',
    status: '待审核'
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

// 根据状态获取标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    '待审核': 'warning',
    '同意': 'success',
    '拒绝': 'danger'
  }
  return typeMap[status] || 'info'
}

// 组件挂载时获取数据
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.apply-management {
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