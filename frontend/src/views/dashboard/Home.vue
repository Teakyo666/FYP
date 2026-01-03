<!-- src/views/dashboard/Home.vue -->
<template>
  <div class="home-dashboard">
    <!-- Hero section -->
    <div class="hero-wrapper">
      <div class="hero-bg"></div>
      <canvas ref="flowCanvas" class="flow-canvas"></canvas>
      
      <!-- Top decoration bar -->
      <div class="hero-top-bar">
        <div class="top-bar-item">
          <el-icon class="pulse-icon"><RefreshRight /></el-icon>
          <span class="top-bar-text">Waste Sorting · Circular Renewal</span>
        </div>
        <div class="top-bar-badge">
          <span class="badge-dot"></span>
          <span>Online</span>
        </div>
      </div>

      <!-- Central content -->
      <div class="hero-content">
        <div class="hero-label">
          <el-icon><Sunny /></el-icon>
          <span>Today's Environmental Quote</span>
        </div>
        <div class="hero-sentence">
          {{ cardSentence || 'Loading today\'s environmental quote...' }}
        </div>
        <div class="hero-footer">
            <el-icon><TrophyBase /></el-icon>
            <span>Environmental protection starts with sorting, making the Earth more beautiful</span>
          </div>
      </div>

      <!-- Floating decorative elements -->
      <div class="floating-elements">
        <div class="float-circle circle-1"></div>
        <div class="float-circle circle-2"></div>
        <div class="float-circle circle-3"></div>
      </div>
    </div>

    <!-- Information cards section -->
    <el-row :gutter="24" class="stats-row">
      <el-col :xs="24" :sm="12" :md="8">
        <div class="data-card time-card">
          <div class="card-icon-wrapper time-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-header">Current Time</div>
            <div class="card-value">{{ currentTime }}</div>
            <div class="card-footer">
              <el-tag type="success" effect="plain" size="small">
                <el-icon><CircleCheck /></el-icon>
                System Running Normally
              </el-tag>
            </div>
          </div>
          <div class="card-decoration time-decoration"></div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="8">
        <div class="data-card earth-card">
          <div class="card-icon-wrapper earth-icon">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-header">Earth Day</div>
            <div class="card-value countdown">{{ daysToEarthDay }}</div>
            <div class="card-sub">
              <el-icon><Bell /></el-icon>
              Every April 22
            </div>
          </div>
          <div class="card-decoration earth-decoration"></div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :md="8">
        <div class="data-card tip-card" @click="randomTip">
          <div class="card-icon-wrapper tip-icon">
            <el-icon><InfoFilled /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-header">
              <span>Waste Sorting Tips</span>
              <el-icon class="refresh-icon"><Refresh /></el-icon>
            </div>
            <div class="card-tip">{{ tip }}</div>
            <div class="card-hint">Click to refresh more tips</div>
          </div>
          <div class="card-decoration tip-decoration"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { sentence } from '@/api/Home'
import { 
  Clock, Calendar, Refresh, RefreshRight, InfoFilled, 
  Sunny, TrophyBase, CircleCheck, Bell
} from '@element-plus/icons-vue'

const cardSentence = ref('')
const currentTime = ref('')
const daysToEarthDay = ref('')
const tip = ref('')
const flowCanvas = ref(null)
let animationId = null

const tips = [
  'Separate wet and dry waste, kitchen waste should not be oily',
  'Batteries, light tubes, medicines → Hazardous waste (red bin)',
  'Tissues, ceramics, cigarette butts → Other waste (gray bin)',
  'Plastic bottles should be flattened, caps disposed separately',
  'Delivery cardboard boxes should be folded before disposal (blue bin)',
  'Milk tea cups belong to recyclables after cleaning',
  'Takeout containers with food → Other waste',
  'Broken glass should be wrapped in paper before disposal'
]

const calculateDaysToEarthDay = () => {
  const today = new Date()
  const year = today.getFullYear()
  let earthDay = new Date(year, 3, 22)
  if (today > earthDay) earthDay = new Date(year + 1, 3, 22)
  const diff = Math.ceil((earthDay - today) / (1000 * 60 * 60 * 24))
  return diff === 0 ? 'Today!' : diff === 1 ? 'Tomorrow!' : `${diff} days`
}

const updateTime = () => {
  currentTime.value = new Date().toLocaleTimeString('en-US', { hour12: false })
  daysToEarthDay.value = calculateDaysToEarthDay()
}

const randomTip = () => {
  let newTip
  do { newTip = tips[Math.floor(Math.random() * tips.length)] }
  while (newTip === tip.value && tips.length > 1)
  tip.value = newTip
}

const initFlowParticles = () => {
  const canvas = flowCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  let particles = []

  const resize = () => {
    canvas.width = canvas.parentElement.offsetWidth
    canvas.height = canvas.parentElement.offsetHeight
  }
  resize()
  window.addEventListener('resize', resize)

  class Particle {
    constructor() { this.reset() }
    reset() {
      this.x = Math.random() * canvas.width
      this.y = canvas.height + 40
      this.size = Math.random() * 20 + 18
      this.speedY = -(Math.random() * 0.6 + 0.3)
      this.opacity = Math.random() * 0.3 + 0.2
      this.rotation = Math.random() * Math.PI * 2
      this.rotationSpeed = (Math.random() - 0.5) * 0.02
    }
    update() {
      this.y += this.speedY
      this.rotation += this.rotationSpeed
      if (this.y < -60) this.reset()
    }
    draw() {
      ctx.save()
      ctx.translate(this.x, this.y)
      ctx.rotate(this.rotation)
      ctx.font = `${this.size}px Arial`
      ctx.fillStyle = `rgba(255, 255, 255, ${this.opacity})`
      ctx.fillText('♻️', -this.size/2, this.size/2)
      ctx.restore()
    }
  }

  for (let i = 0; i < 15; i++) particles.push(new Particle())

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
  try {
    const res = await sentence({})
    cardSentence.value = (res?.success && res?.data?.sentence) ? res.data.sentence : 'Waste sorting, everyone\'s responsibility'
  } catch {
    cardSentence.value = 'Waste sorting starts with me'
  }

  tip.value = tips[Math.floor(Math.random() * tips.length)]
  updateTime()
  setInterval(updateTime, 1000)
  initFlowParticles()
})
</script>

<style scoped>
.home-dashboard { 
  padding: 0;
  min-height: 100vh; 
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 50%, #f0fdf4 100%);
}

/* Hero section */
.hero-wrapper {
  position: relative;
  border-radius: 0 0 40px 40px;
  overflow: hidden;
  margin-bottom: 2rem;
  height: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: url('https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80') center/cover no-repeat;
  filter: brightness(0.85) contrast(1.1);
}

.hero-bg::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.7) 0%, rgba(59, 130, 246, 0.6) 100%);
}

.flow-canvas { 
  position: absolute; 
  inset: 0; 
  z-index: 1; 
  pointer-events: none; 
}

/* Top bar */
.hero-top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 3;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 40px;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.3) 0%, transparent 100%);
}

.top-bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
}

.pulse-icon {
  font-size: 28px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.top-bar-text {
  font-size: 1.3rem;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.top-bar-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  color: white;
  font-size: 0.9rem;
  font-weight: 600;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: #10b981;
  border-radius: 50%;
  animation: blink 1.5s ease-in-out infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

/* Central content */
.hero-content {
  position: relative;
  z-index: 2;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  padding: 3rem 2rem;
  color: white;
}

.hero-label {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 25px;
  backdrop-filter: blur(10px);
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: 2rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.hero-sentence {
  font-size: clamp(2.5rem, 5vw, 4.5rem);
  font-weight: 900;
  line-height: 1.2;
  margin: 0 0 2rem 0;
  letter-spacing: -1px;
  text-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
  max-width: 2000px;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 2rem;
  margin: 2rem 0;
  padding: 24px 48px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-number {
  font-size: 2rem;
  font-weight: 800;
  color: white;
}

.stat-label {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.3);
}

.hero-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  opacity: 0.95;
}

/* Floating decorations */
.floating-elements {
  position: absolute;
  inset: 0;
  z-index: 1;
  pointer-events: none;
}

.float-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation: float 20s ease-in-out infinite;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  right: 10%;
  animation: float 15s ease-in-out infinite reverse;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 20%;
  right: -50px;
  animation: float 18s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(30px, 30px) scale(1.1); }
}

/* Information cards */
.stats-row {
  padding: 0 1rem;
  margin-bottom: 2rem;
}

.data-card {
  position: relative;
  height: 200px;
  border-radius: 24px;
  padding: 24px;
  background: white;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  margin-bottom: 1rem;
}

.data-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card-icon-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  flex-shrink: 0;
}

.time-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.earth-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
}

.tip-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
  box-shadow: 0 8px 20px rgba(245, 158, 11, 0.4);
}

.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.card-header {
  font-size: 0.95rem;
  color: #64748b;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-value {
  font-size: 2.5rem;
  font-weight: 800;
  color: #0f172a;
  line-height: 1;
}

.card-value.countdown {
  color: #0f172a;
  -webkit-background-clip: initial;
  -webkit-text-fill-color: initial;
  background-clip: initial;
}

.card-footer {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-sub, .card-hint {
  color: #94a3b8;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-tip {
  font-size: 1.05rem;
  line-height: 1.6;
  color: #334155;
  font-weight: 500;
  margin: 8px 0;
}

.refresh-icon {
  font-size: 1.2rem;
  color: #94a3b8;
  cursor: pointer;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

.refresh-icon:hover {
  transform: rotate(360deg);
  color: #f59e0b;
}

.card-decoration {
  position: absolute;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  opacity: 0.1;
  right: -30px;
  bottom: -30px;
}

.time-decoration { background: #667eea; }
.earth-decoration { background: #10b981; }
.tip-decoration { background: #f59e0b; }

/* Responsive */
@media (max-width: 768px) {
  .hero-wrapper { height: 400px; border-radius: 0 0 30px 30px; }
  .hero-top-bar { padding: 16px 20px; }
  .top-bar-text { font-size: 1.1rem; }
  .hero-sentence { font-size: 2rem !important; }
  .data-card { height: auto; padding: 20px; flex-direction: column; text-align: center; }
  .card-icon-wrapper { margin-bottom: 12px; }
  .card-header { justify-content: center; }
}
</style>