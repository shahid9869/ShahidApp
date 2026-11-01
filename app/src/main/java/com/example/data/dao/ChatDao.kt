package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
  @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
  fun getAllMessages(): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMessage(message: ChatMessageEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(messages: List<ChatMessageEntity>)

  @Query("SELECT COUNT(*) FROM chat_messages WHERE senderRole = 'USER' AND isRead = 0")
  fun getUnreadUserMessagesCount(): Flow<Int>

  @Query("UPDATE chat_messages SET isRead = 1 WHERE senderRole = 'USER'")
  suspend fun markUserMessagesAsRead()

  @Query("DELETE FROM chat_messages WHERE id = :id")
  suspend fun deleteMessage(id: Long)

  @Query("DELETE FROM chat_messages")
  suspend fun clearAllMessages()
}
