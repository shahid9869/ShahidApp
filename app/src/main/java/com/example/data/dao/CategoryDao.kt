package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories ORDER BY id ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY id ASC")
  fun getActiveCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(categories: List<CategoryEntity>)

  @Update
  suspend fun updateCategory(category: CategoryEntity)

  @Query("UPDATE categories SET isActive = :isActive WHERE id = :id")
  suspend fun toggleCategoryActive(id: String, isActive: Boolean)

  @Query("DELETE FROM categories WHERE id = :id")
  suspend fun deleteCategory(id: String)
}
