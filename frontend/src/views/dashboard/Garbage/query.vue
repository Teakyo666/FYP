<template>
  <div class="garbage-query">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-icon">♻️</div>
        <h1>Waste Classification Assistant</h1>
        <p>Smart garbage sorting made simple</p>
      </div>
    </div>
    
    <!-- Search Section -->
    <div class="search-container">
      <div class="search-box">
        <el-input
          v-model="garbageName"
          placeholder="Enter waste item name..."
          size="large"
          class="search-input"
          @keyup.enter="queryGarbage"
          clearable
        >
          <template #append>
            <el-button :icon="Search" @click="queryGarbage" type="primary">
              Search
            </el-button>
          </template>
        </el-input>
      </div>
    </div>
    
    <!-- Result Section -->
    <transition name="slide-fade">
      <div v-if="result" class="result-container">
        <div class="result-card">
          <div class="result-badge" :class="getCategoryClass(result.category)">
            <span class="badge-icon">{{ getCategoryIcon(result.category) }}</span>
            <span class="badge-text">{{ getCategoryText(result.category) }}</span>
          </div>
          
          <h2 class="result-title">{{ result.name }}</h2>
          
          <div class="info-grid">
            <div class="info-item">
              <div class="info-label">Classification Reason</div>
              <div class="info-value">{{ result.reason }}</div>
            </div>
            
            <div class="info-item full">
              <div class="info-label">Disposal Tips</div>
              <div class="info-tips">
                <span class="tip-icon">💡</span>
                <span>{{ result.title }}</span>
              </div>
            </div>
          </div>
          
          <div v-if="autoSaveStatus" class="ai-badge">
            <span class="ai-icon">✨</span>
            {{ autoSaveStatus.message }}
          </div>
        </div>
      </div>
    </transition>
    
    <!-- AI Hint -->
    <transition name="fade">
      <div v-if="showAIHint && !result && !loading" class="ai-suggestion">
        <div class="ai-card">
          <div class="ai-icon-large">
            <svg viewBox="0 0 100 100" width="64" height="64">
              <!-- 左天线 -->
              <line x1="30" y1="25" x2="30" y2="15" stroke="#3b82f6" stroke-width="2.5" stroke-linecap="round"/>
              <circle cx="30" cy="12" r="3" fill="#3b82f6"/>
              <!-- 右天线 -->
              <line x1="70" y1="25" x2="70" y2="15" stroke="#3b82f6" stroke-width="2.5" stroke-linecap="round"/>
              <circle cx="70" cy="12" r="3" fill="#3b82f6"/>
              <!-- 长方形外框 -->
              <rect x="15" y="25" width="70" height="50" rx="8" fill="none" stroke="#3b82f6" stroke-width="3"/>
              <!-- 左眼 (竖条) -->
              <rect x="32" y="42" width="6" height="16" rx="3" fill="#3b82f6"/>
              <!-- 右眼 (竖条) -->
              <rect x="62" y="42" width="6" height="16" rx="3" fill="#3b82f6"/>
            </svg>
          </div>
          <h3>Hmm, we couldn't find that item...</h3>
          <p style="max-width: 450px; margin: 0 auto 24px; text-align: center; font-size: 16px; color: #6b7280; line-height: 1.6;">Don't worry! Our AI assistant can help identify and classify items that aren't in our database yet.</p>
          <div class="ai-features">
            <div class="feature-item">
              <span class="feature-icon">✨</span>
              <span>Smart Recognition</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🎯</span>
              <span>Accurate Classification</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">💡</span>
              <span>Disposal Tips</span>
            </div>
          </div>
          <el-button 
            type="primary" 
            size="large"
            :icon="MagicStick"
            @click="queryByAI"
            class="ai-btn"
          >
            Ask AI Assistant
          </el-button>
        </div>
      </div>
    </transition>
    
    <!-- Loading -->
    <transition name="fade">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Searching...</p>
      </div>
    </transition>
    
    <!-- Quick Examples -->
    <div class="examples-container">
      <h3 class="section-title">Quick Examples</h3>
      <div class="examples-grid">
        <div 
          v-for="item in examples" 
          :key="item.name"
          class="example-chip"
          :class="getCategoryClass(item.category)"
          @click="selectExample(item.name)"
        >
          <span class="chip-icon">{{ getCategoryIcon(item.category) }}</span>
          <span class="chip-text">{{ item.name }}</span>
        </div>
      </div>
    </div>
    
    <!-- Categories Info -->
    <div class="categories-section">
      <h3 class="section-title">Waste Categories</h3>
      <div class="categories-grid">
        <div 
          v-for="cat in categoryInfo" 
          :key="cat.type"
          class="category-card"
          :class="cat.class"
        >
          <div class="category-icon">{{ cat.icon }}</div>
          <div class="category-name">{{ cat.type }}</div>
          <div class="category-desc">{{ cat.desc }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Search, MagicStick } from '@element-plus/icons-vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Garbage } from '@/api/Garbage'
import { runGarbageClassification, AIGarbage } from '@/api/AI'

const garbageName = ref('')
const result = ref(null)
const loading = ref(false)
const notFoundMessage = ref('')
const showAIHint = ref(false)
const autoSaveStatus = ref(null)

const examples = ref([
  { name: 'Cardboard', category: 'Recyclable waste' },
  { name: 'Used Battery', category: 'Hazardous waste' },
  { name: 'Leftover Food', category: 'Kitchen waste' },
  { name: 'Cigarette Butt', category: 'Non-recyclable waste' },
  { name: 'Plastic Bottle', category: 'Recyclable waste' },
  { name: 'Expired Medicine', category: 'Hazardous waste' }
])

const categoryInfo = ref([
  { type: 'Recyclable Waste', icon: '♻️', class: 'recyclable', desc: 'Materials that can be reprocessed and reused' },
  { type: 'Hazardous Waste', icon: '⚠️', class: 'hazardous', desc: 'Harmful materials requiring special disposal' },
  { type: 'Kitchen Waste', icon: '🍃', class: 'kitchen', desc: 'Organic food waste and biodegradable items' },
  { type: 'Non-recyclable Waste', icon: '🗑️', class: 'non-recyclable', desc: 'General waste that cannot be recycled' }
])

const selectExample = (name) => {
  garbageName.value = name
  queryGarbage()
}

const queryGarbage = async () => {
  if (!garbageName.value.trim()) {
    ElMessage.warning('Please enter waste item name')
    return
  }
  
  try {
    loading.value = true
    result.value = null
    showAIHint.value = false
    
    const response = await Garbage({ garbageName: garbageName.value.trim() })
    
    if (response.success) {
      result.value = response.data
      ElMessage.success('Classification found')
    } else {
      notFoundMessage.value = response.message || 'Classification not found in database'
      showAIHint.value = true
    }
  } catch (error) {
    notFoundMessage.value = 'Item not found. Try our AI assistant for help.'
    showAIHint.value = true
  } finally {
    loading.value = false
  }
}

const queryByAI = async () => {
  if (!garbageName.value.trim()) {
    ElMessage.warning('Please enter waste item name')
    return
  }
  
  try {
    loading.value = true
    showAIHint.value = false
    autoSaveStatus.value = null
    
    const responseData = await runGarbageClassification(garbageName.value.trim())
    
    ElNotification({
      title: 'AI Processing',
      message: `Analyzing "${garbageName.value}" classification...`,
      type: 'info',
      duration: 3000
    })
    
    let outputData = null
    
    if (responseData && responseData.choices && responseData.choices.length > 0) {
      const choice = responseData.choices[0]
      if (choice.messages && choice.messages.content) {
        const content = choice.messages.content.msg || choice.messages.content
        
        if (typeof content === 'string') {
          try {
            const parsed = JSON.parse(content)
            outputData = parsed.answer || parsed
          } catch (e) {
            outputData = {
              type: 'Unknown Category',
              disadv: 'No classification reason provided',
              title: content
            }
          }
        } else {
          outputData = content
        }
      }
    }
    
    if (outputData) {
      result.value = {
        name: garbageName.value,
        category: outputData.type || 'Unknown Category',
        reason: outputData.disadv || 'No classification reason provided',
        title: outputData.title || outputData.content || 'No disposal tips available'
      }
      
      await autoSaveToAILibrary(result.value)
      ElMessage.success('AI analysis complete')
    } else {
      ElMessage.error('AI query failed: Unable to parse response')
    }
    
    loading.value = false
  } catch (error) {
    console.error('AI query failed:', error)
    ElMessage.error('AI query failed: ' + (error.message || 'Unknown error'))
    loading.value = false
  }
}

const autoSaveToAILibrary = async (resultData) => {
  if (!resultData) return
  
  try {
    const aiData = {
      name: resultData.name,
      category: resultData.category,
      reason: resultData.reason,
      title: resultData.title,
      status: 'pending'
    }
    
    const response = await AIGarbage(aiData)
    
    if (response.success) {
      autoSaveStatus.value = {
        type: 'success',
        message: 'AI Generated Content'
      }
    }
  } catch (error) {
    console.error('Auto-save failed:', error)
  }
}

const getCategoryIcon = (category) => {
  const iconMap = {
    'Recyclable waste': '♻️',
    'Hazardous waste': '⚠️',
    'Kitchen waste': '🍃',
    'Non-recyclable waste': '🗑️'
  }
  return iconMap[category] || '📦'
}

const getCategoryClass = (category) => {
  const classMap = {
    'Recyclable waste': 'recyclable',
    'Hazardous waste': 'hazardous',
    'Kitchen waste': 'kitchen',
    'Non-recyclable waste': 'non-recyclable'
  }
  return classMap[category] || 'non-recyclable'
}

const getCategoryText = (category) => {
  return category || 'Unknown Category'
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.garbage-query {
  min-height: 100vh;
  background: #f8f9fa;
  padding-bottom: 60px;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 80px 20px 100px;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><circle cx="50" cy="50" r="2" fill="white" opacity="0.1"/></svg>');
  background-size: 50px 50px;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-icon {
  font-size: 72px;
  margin-bottom: 20px;
  animation: float 3s ease-in-out infinite;
}

.hero-section h1 {
  font-size: 48px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 16px;
  letter-spacing: -0.5px;
}

.hero-section p {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

/* Search */
.search-container {
  max-width: 700px;
  margin: -50px auto 40px;
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

.search-box {
  background: #fff;
  padding: 12px;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.search-input :deep(.el-input__wrapper) {
  box-shadow: none;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
}

.search-input :deep(.el-input__inner) {
  font-size: 16px;
  padding: 8px 16px;
}

.search-input :deep(.el-input-group__append) {
  background: #667eea;
  border: none;
  padding: 0 24px;
}

.search-input :deep(.el-input-group__append .el-button) {
  background: transparent;
  color: #fff;
  border: none;
  font-weight: 600;
  font-size: 16px;
}

/* Result */
.result-container {
  max-width: 700px;
  margin: 0 auto 40px;
  padding: 0 20px;
}

.result-card {
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  position: relative;
}

.result-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 50px;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 20px;
}

.result-badge.recyclable {
  background: #e7f7ef;
  color: #0d9f6e;
}

.result-badge.hazardous {
  background: #fdecea;
  color: #dc2626;
}

.result-badge.organic {
  background: #fef3e2;
  color: #d97706;
}

.result-badge.kitchen {
  background: #fef3e2;
  color: #d97706;
}

.result-badge.general {
  background: #f3f4f6;
  color: #6b7280;
}

.result-badge.non-recyclable {
  background: #f3f4f6;
  color: #6b7280;
}

.badge-icon {
  font-size: 18px;
}

.badge-text {
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.result-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 30px;
}

.info-grid {
  display: grid;
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 16px;
  color: #374151;
  line-height: 1.6;
}

.info-tips {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  background: #f9fafb;
  padding: 16px;
  border-radius: 12px;
  border-left: 4px solid #667eea;
  font-size: 15px;
  color: #374151;
  line-height: 1.6;
}

.tip-icon {
  font-size: 20px;
  flex-shrink: 0;
}

.ai-badge {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
  text-align: center;
  font-size: 13px;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.ai-icon {
  font-size: 16px;
}

/* AI Suggestion */
.ai-suggestion {
  max-width: 700px;
  margin: 0 auto 40px;
  padding: 0 20px;
}

.ai-card {
  background: #fff;
  border-radius: 20px;
  padding: 50px 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.ai-icon-large {
  margin-bottom: 20px;
  display: inline-block;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.05);
    opacity: 0.9;
  }
}

.ai-card h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 12px;
}

.ai-card p {
  font-size: 16px;
  color: #6b7280;
  margin: 0 0 24px;
  line-height: 1.6;
}

.ai-description {
  max-width: 450px;
  margin: 0 auto 24px;
  text-align: center;
}

.ai-features {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #4b5563;
  font-weight: 500;
}

.feature-icon {
  font-size: 18px;
}

.ai-btn {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
}

/* Loading */
.loading-container {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top-color: #667eea;
  border-radius: 50%;
  margin: 0 auto 20px;
  animation: spin 0.8s linear infinite;
}

.loading-container p {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

/* Examples */
.examples-container {
  max-width: 900px;
  margin: 0 auto 50px;
  padding: 0 20px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 24px;
  text-align: center;
}

.examples-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.example-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.example-chip.recyclable {
  background: #e7f7ef;
  color: #0d9f6e;
}

.example-chip.hazardous {
  background: #fdecea;
  color: #dc2626;
}

.example-chip.organic {
  background: #fef3e2;
  color: #d97706;
}

.example-chip.kitchen {
  background: #fef3e2;
  color: #d97706;
}

.example-chip.general {
  background: #f3f4f6;
  color: #6b7280;
}

.example-chip.non-recyclable {
  background: #f3f4f6;
  color: #6b7280;
}

.example-chip:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chip-icon {
  font-size: 22px;
}

.chip-text {
  font-size: 15px;
}

/* Categories */
.categories-section {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
}

.category-card {
  background: #fff;
  padding: 32px 24px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  border-top: 4px solid;
}

.category-card.recyclable {
  border-color: #0d9f6e;
}

.category-card.hazardous {
  border-color: #dc2626;
}

.category-card.organic {
  border-color: #d97706;
}

.category-card.kitchen {
  border-color: #d97706;
}

.category-card.general {
  border-color: #6b7280;
}

.category-card.non-recyclable {
  border-color: #6b7280;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.category-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.category-name {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 8px;
}

.category-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
}

/* Animations */
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.slide-fade-enter-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  transform: translateY(20px);
  opacity: 0;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 20px 80px;
  }
  
  .hero-icon {
    font-size: 56px;
  }
  
  .hero-section h1 {
    font-size: 36px;
  }
  
  .hero-section p {
    font-size: 18px;
  }
  
  .result-card {
    padding: 30px 24px;
  }
  
  .result-title {
    font-size: 26px;
  }
  
  .ai-card {
    padding: 40px 24px;
  }
  
  .examples-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
  }
}
</style>