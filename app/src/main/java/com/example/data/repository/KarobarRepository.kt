package com.example.data.repository

import com.example.data.dao.AdDao
import com.example.data.dao.AppConfigDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.ChatDao
import com.example.data.model.AdEntity
import com.example.data.model.AdStatus
import com.example.data.model.AppConfigEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

class KarobarRepository(
  private val adDao: AdDao,
  private val categoryDao: CategoryDao,
  private val appConfigDao: AppConfigDao,
  private val chatDao: ChatDao
) {
  val approvedAds: Flow<List<AdEntity>> = adDao.getApprovedAds()
  val featuredAds: Flow<List<AdEntity>> = adDao.getFeaturedAds()
  val pendingAds: Flow<List<AdEntity>> = adDao.getPendingAds()
  val allAds: Flow<List<AdEntity>> = adDao.getAllAds()

  val activeCategories: Flow<List<CategoryEntity>> = categoryDao.getActiveCategories()
  val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
  val allConfigs: Flow<List<AppConfigEntity>> = appConfigDao.getAllConfigs()

  // Chat flows
  val allChatMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()
  val unreadUserMessagesCount: Flow<Int> = chatDao.getUnreadUserMessagesCount()

  fun getAdById(id: Long): Flow<AdEntity?> = adDao.getAdById(id)

  suspend fun insertAd(ad: AdEntity): Long = adDao.insertAd(ad)

  suspend fun updateAd(ad: AdEntity) = adDao.updateAd(ad)

  suspend fun deleteAd(id: Long) = adDao.deleteAdById(id)

  suspend fun approveAd(id: Long) {
    adDao.updateAdStatus(id, AdStatus.APPROVED.name, "")
  }

  suspend fun rejectAd(id: Long, reason: String) {
    adDao.updateAdStatus(id, AdStatus.REJECTED.name, reason)
  }

  suspend fun toggleFeatured(id: Long, isFeatured: Boolean) {
    adDao.toggleFeatured(id, isFeatured)
  }

  suspend fun incrementViews(id: Long) {
    adDao.incrementViews(id)
  }

  suspend fun setConfig(key: String, value: String) {
    appConfigDao.setConfig(AppConfigEntity(key, value))
  }

  suspend fun getConfigDirect(key: String): String? {
    return appConfigDao.getConfigDirect(key)?.configValue
  }

  suspend fun toggleCategory(id: String, isActive: Boolean) {
    categoryDao.toggleCategoryActive(id, isActive)
  }

  suspend fun addCategory(category: CategoryEntity) {
    categoryDao.insertCategory(category)
  }

  // Chat methods
  suspend fun sendChatMessage(message: ChatMessageEntity): Long {
    return chatDao.insertMessage(message)
  }

  suspend fun markUserMessagesAsRead() {
    chatDao.markUserMessagesAsRead()
  }

  suspend fun deleteChatMessage(id: Long) {
    chatDao.deleteMessage(id)
  }
}
