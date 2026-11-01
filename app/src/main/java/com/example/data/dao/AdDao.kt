package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdDao {
  @Query("SELECT * FROM ads ORDER BY isFeatured DESC, createdAt DESC")
  fun getAllAds(): Flow<List<AdEntity>>

  @Query("SELECT * FROM ads WHERE status = 'APPROVED' ORDER BY isFeatured DESC, createdAt DESC")
  fun getApprovedAds(): Flow<List<AdEntity>>

  @Query("SELECT * FROM ads WHERE status = 'APPROVED' AND isFeatured = 1 ORDER BY createdAt DESC")
  fun getFeaturedAds(): Flow<List<AdEntity>>

  @Query("SELECT * FROM ads WHERE status = 'PENDING' ORDER BY createdAt DESC")
  fun getPendingAds(): Flow<List<AdEntity>>

  @Query("SELECT * FROM ads WHERE id = :id")
  fun getAdById(id: Long): Flow<AdEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAd(ad: AdEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(ads: List<AdEntity>)

  @Update
  suspend fun updateAd(ad: AdEntity)

  @Delete
  suspend fun deleteAd(ad: AdEntity)

  @Query("DELETE FROM ads WHERE id = :id")
  suspend fun deleteAdById(id: Long)

  @Query("UPDATE ads SET status = :status, rejectionReason = :reason WHERE id = :id")
  suspend fun updateAdStatus(id: Long, status: String, reason: String = "")

  @Query("UPDATE ads SET isFeatured = :isFeatured WHERE id = :id")
  suspend fun toggleFeatured(id: Long, isFeatured: Boolean)

  @Query("UPDATE ads SET viewsCount = viewsCount + 1 WHERE id = :id")
  suspend fun incrementViews(id: Long)

  @Query("SELECT COUNT(*) FROM ads WHERE status = 'PENDING'")
  fun getPendingCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM ads WHERE status = 'APPROVED'")
  fun getApprovedCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM ads")
  fun getTotalCount(): Flow<Int>
}
