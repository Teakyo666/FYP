<template>
  <div class="place-check">
    <!-- Header section -->
    <div class="header-section">
      <div class="header-background">
        <div class="header-content">
          <div class="header-icon">📍</div>
          <h1 class="title">Find Recycling Stations</h1>
          <p class="subtitle">Discover eco-friendly recycling locations near you</p>
        </div>
      </div>
    </div>

    <!-- Search section -->
    <div class="search-wrapper">
      <div class="search-section">
        <el-card shadow="never" class="search-card">
          <div class="search-container">
            <div class="search-type-wrapper">
              <el-select 
                v-model="searchForm.searchType" 
                placeholder="Search by" 
                size="large"
                class="search-type-select"
              >
                <el-option label="City" value="city">
                  <div class="select-option">
                    <span class="option-icon">🏙️</span>
                    <span>City</span>
                  </div>
                </el-option>
                <el-option label="Country" value="country">
                  <div class="select-option">
                    <span class="option-icon">🌍</span>
                    <span>Country</span>
                  </div>
                </el-option>
                <el-option label="Name" value="name">
                  <div class="select-option">
                    <span class="option-icon">🏢</span>
                    <span>Name</span>
                  </div>
                </el-option>
              </el-select>
            </div>
            <el-input
              v-model="searchForm.searchValue"
              :placeholder="`Enter ${searchForm.searchType}...`"
              clearable
              class="search-input"
              size="large"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon class="search-icon-prefix"><Search /></el-icon>
              </template>
            </el-input>
            <el-button 
              type="primary" 
              size="large"
              @click="handleSearch"
              class="search-btn"
              :loading="loading"
            >
              <el-icon v-if="!loading"><Search /></el-icon>
              {{ loading ? 'Searching...' : 'Search' }}
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <!-- Results section -->
    <transition name="fade-slide">
      <div class="results-section" v-if="searchResults.length > 0 && !loading">
        <div class="results-header">
          <h2>
            <span class="results-icon">📋</span>
            Found Stations
          </h2>
          <div class="results-badge">
            {{ searchResults.length }} {{ searchResults.length === 1 ? 'result' : 'results' }}
          </div>
        </div>
        
        <div class="results-grid">
          <div 
            v-for="station in searchResults" 
            :key="station.id" 
            class="station-card"
          >
            <div class="station-icon-wrapper">
              <div class="station-icon-bg">♻️</div>
            </div>
            <div class="station-content">
              <h3 class="station-name">{{ station.name }}</h3>
              <p class="station-description">{{ station.describe }}</p>
              
              <div class="station-info">
                <div class="info-item">
                  <span class="info-icon">🌍</span>
                  <span class="info-text">{{ station.country }}</span>
                </div>
                <div class="info-item">
                  <span class="info-icon">🏙️</span>
                  <span class="info-text">{{ station.city }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Empty state -->
    <transition name="fade">
      <div class="empty-state" v-if="searchForm.searchValue && searchResults.length === 0 && !loading">
        <div class="empty-card">
          <div class="empty-icon">🔍</div>
          <h3>No Stations Found</h3>
          <p>We couldn't find any recycling stations matching your search</p>
          <p class="empty-hint">Try searching with different keywords</p>
        </div>
      </div>
    </transition>

    <!-- Loading state -->
    <transition name="fade">
      <div class="loading-state" v-if="loading">
        <div class="loading-card">
          <div class="loading-spinner"></div>
          <p class="loading-text">Searching for stations...</p>
        </div>
      </div>
    </transition>

    <!-- Initial state -->
    <transition name="fade">
      <div class="initial-state" v-if="!searchForm.searchValue && searchResults.length === 0 && !loading">
        <div class="initial-card">
          <div class="initial-icon">🗺️</div>
          <h3>Start Your Search</h3>
          <p>Enter a city, country, or station name to find recycling locations</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ListPlace } from '@/api/Place'

const searchResults = ref([])
const allPlaces = ref([])
const loading = ref(false)

const searchForm = ref({
  searchType: 'city',
  searchValue: ''
})

const fetchAllPlaces = async () => {
  try {
    loading.value = true
    const response = await ListPlace({ page: 1, pageSize: 10000 })

    if (response.success === true) {
      allPlaces.value = response.data.list || []
      searchResults.value = [...allPlaces.value]
    } else {
      ElMessage.error(response.message || 'Failed to load stations')
      searchResults.value = []
    }
  } catch (error) {
    console.error('Error fetching places:', error)
    ElMessage.error('Failed to load recycling stations')
    searchResults.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  if (!searchForm.value.searchValue?.trim()) {
    searchResults.value = [...allPlaces.value]
    return
  }

  try {
    loading.value = true

    const params = {
      page: 1,
      pageSize: 1000
    }

    if (searchForm.value.searchType === 'name') {
      params.name = searchForm.value.searchValue
    } else if (searchForm.value.searchType === 'country') {
      params.country = searchForm.value.searchValue
    } else if (searchForm.value.searchType === 'city') {
      params.city = searchForm.value.searchValue
    }

    const response = await ListPlace(params)

    if (response.success === true) {
      searchResults.value = response.data.list || []
      if (searchResults.value.length === 0) {
        ElMessage.info('No stations found for this search')
      }
    } else {
      ElMessage.error(response.message || 'Search failed')
      searchResults.value = []
    }
  } catch (error) {
    console.error('Search error:', error)
    ElMessage.error('Search failed. Please try again.')
    searchResults.value = []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchAllPlaces()
})
</script>

<style scoped>
.place-check {
  min-height: 100vh;
  background: #f8f9fa;
}

/* Header Section */
.header-section {
  margin-bottom: -60px;
}

.header-background {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 20px 120px;
  position: relative;
  overflow: hidden;
}

.header-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg width="100" height="100" xmlns="http://www.w3.org/2000/svg"><circle cx="50" cy="50" r="2" fill="white" opacity="0.1"/></svg>');
  background-size: 50px 50px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  position: relative;
  z-index: 1;
}

.header-icon {
  font-size: 64px;
  margin-bottom: 20px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.title {
  font-size: 42px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 16px;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
}

/* Search Section */
.search-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 10;
}

.search-section {
  margin-bottom: 40px;
}

.search-card {
  border-radius: 20px;
  border: none;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  background: #fff;
}

.search-card :deep(.el-card__body) {
  padding: 32px;
}

.search-container {
  display: flex;
  gap: 12px;
  align-items: stretch;
}

.search-type-wrapper {
  flex-shrink: 0;
}

.search-type-select {
  width: 140px;
}

.search-type-select :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.select-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.option-icon {
  font-size: 18px;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-icon-prefix {
  color: #909399;
  font-size: 18px;
}

.search-btn {
  padding: 0 32px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

/* Results Section */
.results-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
}

.results-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.results-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.results-icon {
  font-size: 32px;
}

.results-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 8px 20px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.station-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.station-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.station-icon-wrapper {
  background: linear-gradient(135deg, #e8f4ff 0%, #e1effe 100%);
  padding: 24px;
  text-align: center;
}

.station-icon-bg {
  font-size: 48px;
  display: inline-block;
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.station-content {
  padding: 24px;
}

.station-name {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 12px;
}

.station-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0 0 16px;
  min-height: 42px;
}

.station-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4b5563;
}

.info-icon {
  font-size: 18px;
}

.station-footer {
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.creator-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #9ca3af;
}

.creator-icon {
  font-size: 16px;
}

/* Empty State */
.empty-state,
.loading-state,
.initial-state {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.empty-card,
.loading-card,
.initial-card {
  background: #fff;
  border-radius: 20px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon,
.initial-icon {
  font-size: 72px;
  margin-bottom: 24px;
}

.empty-card h3,
.initial-card h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 12px;
}

.empty-card p,
.initial-card p {
  font-size: 16px;
  color: #6b7280;
  margin: 8px 0;
  line-height: 1.6;
}

.empty-hint {
  color: #9ca3af !important;
  font-size: 14px !important;
}

/* Loading State */
.loading-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #e5e7eb;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

/* Animations */
.fade-slide-enter-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive Design */
@media (max-width: 768px) {
  .header-background {
    padding: 40px 20px 100px;
  }

  .header-icon {
    font-size: 48px;
  }

  .title {
    font-size: 32px;
  }

  .subtitle {
    font-size: 16px;
  }

  .search-card :deep(.el-card__body) {
    padding: 20px;
  }

  .search-container {
    flex-direction: column;
  }

  .search-type-select {
    width: 100%;
  }

  .search-btn {
    width: 100%;
  }

  .results-grid {
    grid-template-columns: 1fr;
  }

  .results-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>