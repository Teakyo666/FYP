<template>
  <div class="garbage-query">
    <div class="header">
      <h1>Garbage Classification Query</h1>
      <p>Enter garbage name to get garbage information</p>
    </div>
    
    <div class="query-section">
      <el-input
        v-model="garbageName"
        placeholder="Please enter garbage name"
        size="large"
        class="search-input"
        @keyup.enter="queryGarbage"
      >
        <template #append>
        <el-button :icon="Search" @click="queryGarbage" type="primary">
          Query
        </el-button>
      </template>
      </el-input>
    </div>
    
    <div v-if="result" class="result-section">
      <el-card class="result-card">
        <h2>Query Results</h2>
        <div class="result-content">
          <p><strong>Garbage Name:</strong>{{ result.name }}</p>
          <p><strong>Category:</strong>
            <el-tag :type="getTagType(result.category)" size="large">
              {{ getCategoryText(result.category) }}
            </el-tag>
          </p>
          <p><strong>Classification Reason:</strong>{{ result.reason }}</p>
          <p><strong>Tips:</strong></p>
          <el-alert
            :title="result.title"
            type="info"
            show-icon
            class="result-tip"
          />
        </div>
        <!-- Display auto-save status -->
        <div class="auto-save-status" v-if="autoSaveStatus">
          <el-tag :type="autoSaveStatus.type">{{ autoSaveStatus.message }}</el-tag>
        </div>
      </el-card>
    </div>
    
    <!-- AI hint when no results found -->
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
              Ask AI
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
    
    <!-- Common garbage classification examples -->
    <div class="examples-section">
      <h2>Common Garbage Classification Examples</h2>
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
import { runGarbageClassification, AIGarbage } from '@/api/AI'

// 响应式数据
const garbageName = ref('')
const result = ref(null)
const loading = ref(false)
const notFoundMessage = ref('')
const showAIHint = ref(false)
const autoSaveStatus = ref(null) // 自动保存状态

// Common garbage classification examples
const examples = ref([
  { name: 'Cardboard', category: 'Recyclable' },
  { name: 'Used Battery', category: 'Hazardous Waste' },
  { name: 'Leftover Food', category: 'Wet Waste' },
  { name: 'Cigarette Butt', category: 'Dry Waste' },
  { name: 'Plastic Bottle', category: 'Recyclable' },
  { name: 'Expired Medicine', category: 'Hazardous Waste' }
])

// Query garbage classification
const queryGarbage = async () => {
  if (!garbageName.value.trim()) {
    ElMessage.warning('Please enter garbage name')
    return
  }
  
  try {
    loading.value = true
    result.value = null
    
    // Call API to query garbage classification
    const response = await Garbage({ garbageName: garbageName.value.trim() })
    
    if (response.success) {
      result.value = response.data
      ElMessage.success('Query successful')
      // Hide AI hint
      showAIHint.value = false
    } else {
      const errorMsg = response.message || 'Classification information for this garbage not found'
      
      // Show AI query hint
      notFoundMessage.value = errorMsg
      showAIHint.value = true
    }
  } catch (error) {
    // No longer show network exception message, directly show AI query hint
    notFoundMessage.value = 'Classification information for this garbage not found, you can try asking AI'
    showAIHint.value = true
  } finally {
    loading.value = false
  }
}

// AI query for garbage classification
const queryByAI = async () => {
  if (!garbageName.value.trim()) {
    ElMessage.warning('Please enter garbage name')
    return
  }
  
  try {
    loading.value = true
    autoSaveStatus.value = null
    
    // Call AI API for garbage classification query
    const responseData = await runGarbageClassification(garbageName.value.trim())
    
    // Show notification indicating AI query is in progress
    ElNotification({
      title: 'AI Query',
      message: `Analyzing classification information for "${garbageName.value}" using AI model...`,
      type: 'info',
      duration: 3000
    })
    
    // Simplified response handling - extract data directly from API response
    let outputData = null
    
    // Process new API response format
    if (responseData && responseData.choices && responseData.choices.length > 0) {
      const choice = responseData.choices[0]
      if (choice.messages && choice.messages.content) {
        // Extract content
        const content = choice.messages.content.msg || choice.messages.content
        
        // If it's a string, try to parse as JSON
        if (typeof content === 'string') {
          try {
            const parsed = JSON.parse(content)
            // Check if there's an answer field
            outputData = parsed.answer || parsed
          } catch (e) {
            // If it's not JSON, use the raw content directly
            outputData = {
              type: 'Unknown Category',
              disadv: 'No classification reason provided',
              title: content
            }
          }
        } else {
          // If it's already an object, use directly
          outputData = content
        }
      }
    }
    
    if (outputData) {
      // Map fields
      result.value = {
        name: garbageName.value,
        category: outputData.type || 'Unknown Category',
        reason: outputData.disadv || 'No classification reason provided',
        title: outputData.title || outputData.content || 'No handling suggestions'
      }
      
      // Auto-save to AI library
      await autoSaveToAILibrary(result.value)
      
      ElMessage.success('AI query completed')
    } else {
      ElMessage.error('AI query failed: Failed to parse response data')
    }
    
    loading.value = false
  } catch (error) {
    console.error('AI query failed:', error)
    ElMessage.error('AI query failed: ' + (error.message || 'Unknown error'))
    loading.value = false
  }
}

// Auto-save to AI library functionality
const autoSaveToAILibrary = async (resultData) => {
  if (!resultData) {
    return
  }
  
  try {
    // Prepare data to be saved
    const aiData = {
      name: resultData.name,
      category: resultData.category,
      reason: resultData.reason,
      title: resultData.title,
      status: 'pending'
    }
    
    // Call API to save to AI library
    const response = await AIGarbage(aiData)
    
    if (response.success) {
      autoSaveStatus.value = {
        type: 'success',
        message: 'This content is AI-generated'
      }
    } else {
      autoSaveStatus.value = {
        type: 'danger',
        message: 'Failed to save to AI library'
      }
    }
  } catch (error) {
    console.error('Auto-save to AI library failed:', error)
    autoSaveStatus.value = {
      type: 'danger',
      message: 'Failed to save to AI library'
    }
  }
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

.auto-save-status {
  margin-top: 20px;
  text-align: center;
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