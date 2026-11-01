package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AdType(val titleUrdu: String, val titleEnglish: String) {
  BUSINESS("کاروباری اشتہار", "Business Ad"),
  SERVICE("سروسز کا اشتہار", "Service Ad")
}

enum class AdStatus(val titleUrdu: String, val titleEnglish: String) {
  PENDING("منظوری کا منتظر", "Pending Approval"),
  APPROVED("منظور شدہ (لائیو)", "Approved & Live"),
  REJECTED("مسترد شدہ", "Rejected")
}

@Entity(tableName = "ads")
data class AdEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val businessName: String,
  val category: String,
  val type: String, // "BUSINESS" or "SERVICE"
  val ownerName: String,
  val phone: String,
  val whatsapp: String,
  val city: String,
  val address: String,
  val priceOrRate: String,
  val description: String,
  val status: String = AdStatus.PENDING.name, // Default pending for admin review
  val isFeatured: Boolean = false,
  val viewsCount: Int = 0,
  val createdAt: Long = System.currentTimeMillis(),
  val rejectionReason: String = ""
)
