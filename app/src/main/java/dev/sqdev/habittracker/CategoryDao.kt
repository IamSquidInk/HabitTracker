package dev.sqdev.habittracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun hardDelete(categoryId: Long)

    @Query("UPDATE categories SET isDeleted = 1 WHERE id = :categoryId")
    suspend fun softDelete(categoryId: Long)

    @Query("SELECT * FROM categories WHERE ledgerType = :ledgerType AND isDeleted = 0")
    fun getActiveCategories(ledgerType: LedgerType): Flow<List<Category>>
}