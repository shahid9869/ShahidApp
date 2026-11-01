package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey
  val id: String,
  val nameUrdu: String,
  val nameEnglish: String,
  val iconName: String,
  val adType: String = "ALL", // "ALL", "BUSINESS", "SERVICE"
  val isActive: Boolean = true
)
