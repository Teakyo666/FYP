<template>
  <div class="garbage-query">
    <div class="header">
      <h1>垃圾分类查询</h1>
      <p>输入垃圾名称，获取垃圾信息</p>
    </div>
    
    <div class="query-section">
      <el-input
        v-model="garbageName"
        placeholder="请输入垃圾名称"
        size="large"
        class="search-input"
        @keyup.enter="queryGarbage"
      >
        <template #append>
        <el-button :icon="Search" @click="queryGarbage" type="primary">
          查询
        </el-button>
      </template>
      </el-input>
    </div>
    
    <div v-if="result" class="result-section">
      <el-card class="result-card">
        <h2>查询结果</h2>
        <div class="result-content">
          <p><strong>垃圾名称：</strong>{{ result.name }}</p>
          <p><strong>分类：</strong>
            <el-tag :type="getTagType(result.category)" size="large">
              {{ getCategoryText(result.category) }}
            </el-tag>
          </p>
          <p><strong>分类原因：</strong>{{ result.reason }}</p>
          <p><strong>处理建议：</strong></p>
          <el-alert
            :title="result.title"
            type="info"
            show-icon
            class="result-tip"
          />
        </div>
      </el-card>
    </div>
    
    <!-- 未找到结果时的AI提示 -->
    <div v-if="showAIHint && !result && !loading" class="ai-hint-card">
      <el-alert
        :title="notFoundMessage"
        type="warning"
        show-icon
        :closable="false"
      >
        <template #default>
          <p>{{ notFoundMessage }}</p>
          <div style="margin-top: 15px;">
            <el-button 
              type="success" 
              :icon="MagicStick" 
              @click="queryByAI"
            >
              询问AI
            </el-button>
          </div>
        </template>
      </el-alert>
    </div>
    
    <div v-if="loading" class="loading-section">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="h3" style="width: 50%" />
          <div style="margin-top: 20px">
            <el-skeleton-item variant="p" style="width: 100%" />
          </div>
          <div style="margin-top: 10px">
            <el-skeleton-item variant="p" style="width: 80%" />
          </div>
        </template>
      </el-skeleton>
    </div>
    
    <!-- 常见垃圾分类示例 -->
    <div class="examples-section">
      <h2>常见垃圾分类示例</h2>
      <el-row :gutter="20">
        <el-col :span="12" v-for="item in examples" :key="item.name">
          <el-card class="example-card" shadow="hover">
            <div class="example-content">
              <span class="example-name">{{ item.name }}</span>
              <el-tag :type="getTagType(item.category)" size="small">
                {{ getCategoryText(item.category) }}
              </el-tag>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search, MagicStick } from '@element-plus/icons-vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Garbage } from '@/api/Garbage'

// 响应式数据
const garbageName = ref('')
const result = ref(null)
const loading = ref(false)
const notFoundMessage = ref('')
const showAIHint = ref(false)

// 常见垃圾分类示例
const examples = ref([
  { name: '废纸箱', category: '可回收物' },
  { name: '废电池', category: '有害垃圾' },
  { name: '剩菜剩饭', category: '湿垃圾' },
  { name: '烟蒂', category: '干垃圾' },
  { name: '塑料瓶', category: '可回收物' },
  { name: '过期药品', category: '有害垃圾' }
])

// 查询垃圾分类
const queryGarbage = async () => {
  if (!garbageName.value.trim()) {
    ElMessage.warning('请输入垃圾名称')
    return
  }
  
  try {
    loading.value = true
    result.value = null
    
    // 调用API查询垃圾分类
    const response = await Garbage({ garbageName: garbageName.value.trim() })
    
    if (response.success) {
      result.value = response.data
      ElMessage.success('查询成功')
      // 隐藏AI提示
      showAIHint.value = false
    } else {
      // 查询失败时显示后端返回的错误信息，并提供AI查询选项
      const errorMsg = response.message || '未找到该垃圾的分类信息'
      // 不再显示ElMessage错误提示，只在AI提示区域显示
      
      // 显示AI查询提示
      notFoundMessage.value = errorMsg
      showAIHint.value = true
    }
  } catch (error) {
    console.error('查询失败:', error)
    // 不再显示通用错误提示，只在AI提示区域显示
    notFoundMessage.value = '你可以尝试询问AI'
    showAIHint.value = true
  } finally {
    loading.value = false
  }
}

// AI查询垃圾分类（占位功能）
const queryByAI = () => {
  if (!garbageName.value.trim()) {
    ElMessage.warning('请输入垃圾名称')
    return
  }
  
  // 显示通知，说明这是占位功能
  ElNotification({
    title: 'AI查询',
    message: `AI查询功能尚未实现，您输入的"${garbageName.value}"将通过AI模型进行分析（预留功能）`,
    type: 'info',
    duration: 5000
  })
  
  // 这里可以添加实际的AI查询逻辑
  // 例如调用AI API等
  console.log('AI查询请求:', garbageName.value)
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
</script>

<style scoped>
.garbage-query {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.header p {
  font-size: 16px;
  color: #666;
}

.query-section {
  margin-bottom: 30px;
}

.search-input {
  max-width: 500px;
  margin: 0 auto;
}

.result-section {
  margin-top: 30px;
}

.result-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.result-card h2 {
  margin-bottom: 20px;
  color: #333;
}

.result-content p {
  margin-bottom: 15px;
  font-size: 16px;
  line-height: 1.6;
}

.result-tip {
  margin: 15px 0;
}

.ai-hint-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.loading-section {
  margin-top: 30px;
}

.examples-section {
  margin-top: 40px;
}

.examples-section h2 {
  margin-bottom: 20px;
  color: #333;
}

.example-card {
  margin-bottom: 15px;
  border-radius: 8px;
}

.example-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.example-name {
  font-size: 14px;
  color: #666;
}
</style>