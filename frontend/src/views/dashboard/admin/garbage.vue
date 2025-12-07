<template>
  <div class="garbage-management">
    <div class="header">
      <h1>垃圾管理</h1>
      <p>管理系统中的垃圾分类信息</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入垃圾名称"
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
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增垃圾</el-button>
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
        <el-table-column prop="name" label="垃圾名称" min-width="150" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.category)">
              {{ getCategoryText(scope.row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="分类原因" min-width="200" />
        <el-table-column prop="title" label="处理建议" min-width="200" />
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
        <el-form-item label="垃圾名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入垃圾名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="可回收物" value="可回收物" />
            <el-option label="有害垃圾" value="有害垃圾" />
            <el-option label="湿垃圾" value="湿垃圾" />
            <el-option label="干垃圾" value="干垃圾" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类原因" prop="reason">
          <el-input
            v-model="formData.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入分类原因"
          />
        </el-form-item>
        <el-form-item label="处理建议" prop="title">
          <el-input
            v-model="formData.title"
            type="textarea"
            :rows="3"
            placeholder="请输入处理建议"
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
  ListGarbage,
  CreateGarbage, 
  UpdateGarbage, 
  DeleteGarbage 
} from '@/api/Garbage'

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
  category: '',
  reason: '',
  title: ''
})

// 表单验证规则
const formRules = {
  name: [{ required: true, message: '请输入垃圾名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  reason: [{ required: true, message: '请输入分类原因', trigger: 'blur' }],
  title: [{ required: true, message: '请输入处理建议', trigger: 'blur' }]
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
    
    // 调用获取垃圾列表的API，支持分页和搜索
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.name || undefined
    }
    
    const response = await ListGarbage(params)
    
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
  dialogTitle.value = '新增垃圾'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑垃圾'
  isEdit.value = true
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除垃圾 "${row.name}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeleteGarbage({ Id: row.id })
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
            name: formData.name,
            category: formData.category,
            reason: formData.reason,
            title: formData.title
          }
          response = await UpdateGarbage(updateData)
        } else {
          // 新增
          const createData = {
            name: formData.name,
            category: formData.category,
            reason: formData.reason,
            title: formData.title
          }
          response = await CreateGarbage(createData)
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
        console.error('Error details:', {
          message: error.message,
          code: error.code,
          response: error.response
        })
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
    category: '',
    reason: '',
    title: ''
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

// 根据分类获取标签类型
const getTagType = (category) => {
  const typeMap = {
    '可回收物': 'success',
    '有害垃圾': 'danger',
    '湿垃圾': 'warning',
    '干垃圾': 'info'
  }
  return typeMap[category] || 'info'
}

// 根据分类获取显示文本
const getCategoryText = (category) => {
  return category || '未知分类'
}

// 组件挂载时获取数据
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