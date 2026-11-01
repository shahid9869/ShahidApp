package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_config")
data class AppConfigEntity(
  @PrimaryKey
  val configKey: String,
  val configValue: String
)
