package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AppConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppConfigDao {
  @Query("SELECT * FROM app_config WHERE configKey = :key")
  fun getConfig(key: String): Flow<AppConfigEntity?>

  @Query("SELECT * FROM app_config WHERE configKey = :key")
  suspend fun getConfigDirect(key: String): AppConfigEntity?

  @Query("SELECT * FROM app_config")
  fun getAllConfigs(): Flow<List<AppConfigEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun setConfig(config: AppConfigEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(configs: List<AppConfigEntity>)
}
