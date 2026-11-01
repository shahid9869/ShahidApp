package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageRole {
  USER,
  ADMIN
}

enum class MessageType {
  TEXT,
  IMAGE,
  AUDIO
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val senderRole: String, // "USER" or "ADMIN"
  val senderName: String,
  val messageType: String, // "TEXT", "IMAGE", "AUDIO"
  val textContent: String = "",
  val mediaPath: String = "",
  val audioDurationSec: Int = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false
)
