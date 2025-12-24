<template>
  <div class="sentence-management">
    <div class="header">
      <h1>句子管理</h1>
      <p>管理系统中的句子信息</p>
    </div>

    <!-- 搜索和操作栏 -->
    <div class="toolbar">
      <el-row :gutter="20">
        <el-col :span="18">
          <el-input
            v-model="searchForm.sentence"
            placeholder="请输入句子内容"
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
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增句子</el-button>
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
        <el-table-column prop="sentence" label="句子" min-width="300" />
        <el-table-column prop="createBy" label="创建人" width="200" />
        <el-table-column prop="createDate" label="创建时间" width="200" />
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
        <el-form-item label="句子内容" prop="sentence">
          <el-input
            v-model="formData.sentence"
            type="textarea"
            :rows="4"
            placeholder="请输入句子内容"
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
  ListSentence,
  CreateSentence, 
  UpdateSentence, 
  DeleteSentence 
} from '@/api/Sentence'
import { User } from '@/api/Users'
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
  sentence: ''
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
  sentence: '',
  createBy: '',
  createDate: ''
})

// 表单验证规则
const formRules = {
  sentence: [{ required: true, message: '请输入句子内容', trigger: 'blur' }]
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
  searchForm.sentence = ''
  pagination.currentPage = 1
  fetchData()
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    
    // 调用获取句子列表的API，支持分页和搜索
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      keyword: searchForm.sentence || undefined
    }
    
    const response = await ListSentence(params)
    
    if (response && response.success) {
      // 确保数据结构正确
      if (response.data && typeof response.data === 'object') {
        let list = Array.isArray(response.data.list) ? response.data.list : []
        
        // 获取当前用户信息
        const userStore = useUserStore()
        
        // 如果是非管理员用户，只显示自己创建的句子
        if (userStore.role !== 'admin') {
          list = list.filter(item => item.createBy === userStore.id)
        }
        
        // 为每个句子获取对应的用户名
        const sentencesWithUserNames = await Promise.all(list.map(async (item) => {
          // 假设createBy字段存储的是用户ID
          const userName = await getUserNameById(item.createBy)
          return {
            ...item,
            createBy: userName // 将用户ID替换为用户名
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
  dialogTitle.value = '新增句子'
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑句子'
  isEdit.value = true
  // 仅复制需要的字段
  formData.id = row.id
  formData.sentence = row.sentence
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除句子 "${row.sentence.substring(0, 20)}${row.sentence.length > 20 ? '…' : ''}" 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await DeleteSentence({id: row.id})
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
            sentence: formData.sentence
          }
          response = await UpdateSentence(updateData)
        } else {
          // 新增
          // 获取当前登录用户ID
          const userStore = useUserStore()
          const createData = {
            sentence: formData.sentence,
            createBy: userStore.id 
          }
          response = await CreateSentence(createData)
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
    sentence: '',
    createBy: '',
    createDate: ''
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
.sentence-management {
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