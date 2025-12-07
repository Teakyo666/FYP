<!-- src/views/profile/Profile.vue -->
<template>
  <div class="profile-page">
    <canvas ref="particleCanvas" class="particle-bg"></canvas>

    <div class="profile-container">
      <el-card class="profile-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <div class="header-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="header-text">
                <span class="title">个人中心</span>
                <span class="subtitle">Personal Center</span>
              </div>
            </div>
          </div>
        </template>

        <div v-if="loading" class="loading-wrapper">
          <el-skeleton :rows="10" animated />
        </div>

        <div v-else class="content-wide">
          <!-- 头像区域 -->
          <div class="top-section">
            <div class="avatar-box">
              <el-avatar :size="200" :src="avatarUrl" class="avatar">
                {{ form.name?.[0]?.toUpperCase() || 'U' }}
              </el-avatar>
              <div class="glow-ring"></div>
              <div v-if="isEditMode" class="upload-overlay" @click="handleAvatarClick">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </div>

            <div class="user-info">
              <div v-if="!isEditMode">
                <h1 class="username">{{ form.name || '环保用户' }}</h1>
                <p class="motto">
                  <el-icon><MagicStick /></el-icon>
                  垃圾分类 · 循环新生
                </p>
                <div class="user-meta">
                  <el-tag type="success" effect="plain">
                    <el-icon><Clock /></el-icon>
                    加入于 {{ joinDate }}
                  </el-tag>
                </div>
              </div>
              <div v-else class="edit-name-section">
                <el-input 
                  v-model="form.name" 
                  placeholder="请输入昵称" 
                  size="large"
                  maxlength="20"
                  show-word-limit
                  class="name-input"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </div>
            </div>
          </div>

          <!-- 信息卡片 -->
          <div class="info-section">
            <div class="section-header">
              <el-icon><InfoFilled /></el-icon>
              <span>基本信息</span>
            </div>

            <div class="info-grid">
              <!-- 城市 -->
              <div class="info-card">
                <div class="card-icon city-icon">
                  <el-icon><Location /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-label">所在城市</div>
                  <div v-if="!isEditMode" class="card-value">{{ form.city || '未填写' }}</div>
                  <el-input 
                    v-else 
                    v-model="form.city" 
                    placeholder="如：深圳" 
                    size="large"
                    class="edit-input"
                  >
                    <template #prefix>
                      <el-icon><Location /></el-icon>
                    </template>
                  </el-input>
                </div>
              </div>

              <!-- 国家 -->
              <div class="info-card">
                <div class="card-icon country-icon">
                  <el-icon><Flag /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-label">国家 / 地区</div>
                  <div v-if="!isEditMode" class="card-value">{{ form.country || '未填写' }}</div>
                  <el-input 
                    v-else 
                    v-model="form.country" 
                    placeholder="如：中国" 
                    size="large"
                    class="edit-input"
                  >
                    <template #prefix>
                      <el-icon><Flag /></el-icon>
                    </template>
                  </el-input>
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="action-bar">
            <el-button 
              v-if="!isEditMode"
              type="primary" 
              size="large" 
              round 
              class="action-btn edit-btn"
              @click="toggleEditMode"
            >
              <el-icon><EditPen /></el-icon>
              编辑个人资料
            </el-button>
            
            <template v-else>
              <el-button 
                size="large" 
                round 
                class="action-btn cancel-btn"
                @click="cancelEdit"
              >
                <el-icon><Close /></el-icon>
                取消
              </el-button>
              <el-button 
                type="success" 
                size="large" 
                round 
                class="action-btn save-btn"
                :loading="saving"
                @click="handleSave"
              >
                <el-icon><Check /></el-icon>
                保存修改
              </el-button>
            </template>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/store/index'
import { 
  User, Location, Flag, EditPen, Check, 
  Camera, MagicStick, Clock, InfoFilled, Close, Edit
} from '@element-plus/icons-vue'
import { GetProfile, UpdateProfile } from '@/api/Home/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const loading = ref(true)
const saving = ref(false)
const isEditMode = ref(false)
const particleCanvas = ref(null)
let animationId = null

const form = reactive({
  name: userStore.name || '',
  city: '',
  country: ''
})

const originalForm = reactive({})
const avatarUrl = ref('')
const joinDate = ref('')
const userInfoId = ref('') // 添加这一行来存储用户信息表的ID

const toggleEditMode = () => {
  isEditMode.value = true
  // 保存原始数据
  Object.assign(originalForm, { ...form })
}

const cancelEdit = () => {
  ElMessageBox.confirm('确定要取消编辑吗？未保存的更改将丢失', '提示', {
    confirmButtonText: '确定取消',
    cancelButtonText: '继续编辑',
    type: 'warning',
  }).then(() => {
    // 恢复原始数据
    Object.assign(form, { ...originalForm })
    isEditMode.value = false
  }).catch(() => {})
}

const handleSave = async () => {
  // 验证必填项
  if (!form.name || form.name.trim() === '') {
    ElMessage.warning('请输入昵称')
    return
  }

  saving.value = true

  try {
    // 准备要发送的数据
    const profileData = {
      id: userInfoId.value,  // 使用用户信息表的ID
      userId: userStore.id,  // 用户ID
      name: form.name,
      city: form.city || '',
      country: form.country || '',
      avatar: ''
    };

    // 调用更新接口
    const response = await UpdateProfile(profileData)
    
    // 更新 store
    userStore.setName(form.name)
    
    ElMessage.success('保存成功')
    isEditMode.value = false
    
    // 更新原始数据
    Object.assign(originalForm, { ...form })
  } catch (err) {
    console.error('保存失败:', err)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

const handleAvatarClick = () => {
  ElMessage.info('头像上传功能开发中～')
  // TODO: 实现头像上传
}

// 粒子背景
const initParticles = () => {
  const canvas = particleCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  let particles = []

  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  resize()
  window.addEventListener('resize', resize)

  class Particle {
    constructor() { this.reset() }
    reset() {
      this.x = Math.random() * canvas.width
      this.y = canvas.height + 40
      this.size = Math.random() * 18 + 16
      this.speedY = -(Math.random() * 0.6 + 0.3)
      this.opacity = Math.random() * 0.25 + 0.15
      this.rotation = Math.random() * Math.PI * 2
      this.rotationSpeed = (Math.random() - 0.5) * 0.02
    }
    update() { 
      this.y += this.speedY
      this.rotation += this.rotationSpeed
      if (this.y < -50) this.reset() 
    }
    draw() {
      ctx.save()
      ctx.translate(this.x, this.y)
      ctx.rotate(this.rotation)
      ctx.font = `${this.size}px Arial`
      ctx.fillStyle = `rgba(16, 185, 129, ${this.opacity})`
      ctx.fillText('♻️', -this.size/2, this.size/2)
      ctx.restore()
    }
  }

  for (let i = 0; i < 20; i++) particles.push(new Particle())

  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)
    particles.forEach(p => { p.update(); p.draw() })
    animationId = requestAnimationFrame(animate)
  }
  animate()

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resize)
    if (animationId) cancelAnimationFrame(animationId)
  })
}

onMounted(async () => {
  initParticles()

  try {
    const res = await GetProfile({ userId: userStore.id })
    if (res?.data) {
      const d = res.data
      form.name = d.name || userStore.name || '环保用户'
      form.city = d.city || ''
      form.country = d.country || ''
      userInfoId.value = d.id || '' // 保存用户信息表的ID
      
      // 保存原始数据
      Object.assign(originalForm, { ...form })
      
      userStore.setName(form.name)
      
      // 模拟加入日期
      joinDate.value = new Date().toLocaleDateString('zh-CN')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 50%, #bbf7d0 100%);
  padding: 2rem 1rem;
  position: relative;
  overflow: hidden;
}

.particle-bg { 
  position: fixed; 
  inset: 0; 
  z-index: 0; 
  pointer-events: none; 
}

.profile-container { 
  max-width: 1200px; 
  margin: 0 auto; 
  position: relative; 
  z-index: 1; 
}

.profile-card {
  border-radius: 32px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(16, 185, 129, 0.2);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.card-header {
  padding: 2rem 3rem;
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.header-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  backdrop-filter: blur(10px);
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title { 
  font-size: 2rem; 
  font-weight: 800; 
  line-height: 1;
}

.subtitle {
  font-size: 0.9rem;
  opacity: 0.85;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.loading-wrapper {
  padding: 3rem;
}

.content-wide {
  padding: 0;
}

/* 顶部区域 */
.top-section { 
  display: flex; 
  align-items: center; 
  padding: 3rem 4rem; 
  gap: 3rem;
  background: linear-gradient(180deg, rgba(16, 185, 129, 0.03) 0%, transparent 100%);
  border-bottom: 1px solid rgba(16, 185, 129, 0.1);
}

.avatar-box { 
  position: relative; 
  flex-shrink: 0; 
}

.avatar {
  width: 200px; 
  height: 200px;
  border: 6px solid white;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.2);
  font-size: 5rem;
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  transition: all 0.3s ease;
}

.avatar:hover {
  transform: scale(1.05);
}

.glow-ring {
  position: absolute; 
  inset: -20px; 
  border-radius: 50%;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.4) 0%, transparent 70%);
  animation: breathe 4s ease-in-out infinite;
  pointer-events: none;
}

@keyframes breathe { 
  0%, 100% { transform: scale(1); opacity: 0.6; } 
  50% { transform: scale(1.15); opacity: 0.3; } 
}

.upload-overlay {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: white;
  font-size: 0.9rem;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-box:hover .upload-overlay {
  opacity: 1;
}

.upload-overlay .el-icon {
  font-size: 2rem;
}

.user-info {
  flex: 1;
}

.username { 
  font-size: 3rem; 
  font-weight: 900; 
  color: #0f172a; 
  margin: 0 0 1rem; 
  line-height: 1.2;
}

.motto { 
  font-size: 1.3rem; 
  color: #10b981; 
  font-weight: 600; 
  margin: 0 0 1.5rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.user-meta .el-tag {
  padding: 8px 16px;
  font-size: 0.9rem;
}

.edit-name-section {
  max-width: 500px;
}

.name-input {
  font-size: 1.2rem;
}

.name-input :deep(.el-input__inner) {
  font-size: 1.2rem;
  font-weight: 600;
}

/* 信息区域 */
.info-section {
  padding: 3rem 4rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.5rem;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 2rem;
}

.section-header .el-icon {
  font-size: 1.8rem;
  color: #10b981;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.info-card {
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(52, 211, 153, 0.05) 100%);
  border: 2px solid rgba(16, 185, 129, 0.15);
  border-radius: 20px;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #10b981 0%, #34d399 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.info-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(16, 185, 129, 0.2);
  border-color: rgba(16, 185, 129, 0.3);
}

.info-card:hover::before {
  transform: scaleX(1);
}

.card-icon {
  width: 70px;
  height: 70px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: white;
  flex-shrink: 0;
}

.city-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #60a5fa 100%);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.country-icon {
  background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
  box-shadow: 0 8px 20px rgba(139, 92, 246, 0.3);
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-label {
  font-size: 0.9rem;
  color: #64748b;
  font-weight: 600;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-value {
  font-size: 1.4rem;
  font-weight: 700;
  color: #0f172a;
  word-break: break-all;
}

.edit-input :deep(.el-input__inner) {
  font-size: 1.2rem;
  font-weight: 600;
}

/* 操作按钮 */
.action-bar {
  text-align: center;
  padding: 2rem 4rem 3rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.action-btn {
  padding: 1rem 3rem;
  font-size: 1.1rem;
  font-weight: 700;
  min-width: 180px;
  transition: all 0.3s ease;
}

.edit-btn {
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.4);
}

.edit-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(16, 185, 129, 0.5);
}

.save-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.4);
}

.save-btn:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(16, 185, 129, 0.5);
}

.cancel-btn {
  background: white;
  border: 2px solid #e5e7eb;
  color: #6b7280;
}

.cancel-btn:hover {
  transform: translateY(-4px);
  border-color: #d1d5db;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

/* 响应式 */
@media (max-width: 1024px) {
  .top-section { 
    flex-direction: column; 
    text-align: center; 
    padding: 2rem;
  }
  
  .user-meta {
    justify-content: center;
  }
  
  .motto {
    justify-content: center;
  }
  
  .info-section {
    padding: 2rem 1.5rem;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .action-bar {
    flex-direction: column;
    padding: 2rem 1.5rem;
  }
  
  .action-btn {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
    padding: 1.5rem;
  }
  
  .header-left {
    flex-direction: column;
    gap: 1rem;
  }
  
  .avatar {
    width: 150px;
    height: 150px;
    font-size: 4rem;
  }
  
  .username {
    font-size: 2rem;
  }
  
  .info-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>