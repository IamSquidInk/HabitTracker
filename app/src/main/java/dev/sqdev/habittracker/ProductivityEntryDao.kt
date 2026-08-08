package dev.sqdev.habittracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductivityEntryDao {
    @Insert
    suspend fun insert(entry: ProductivityEntry): Long

    @Update
    suspend fun update(entry: ProductivityEntry)

    @Delete
    suspend fun delete(entry: ProductivityEntry)

    @Query("SELECT * FROM productivity_entries ORDER BY date DESC, time DESC")
    fun getAllEntries(): Flow<List<ProductivityEntry>>

    @Query("SELECT COUNT(*) FROM productivity_entries WHERE categoryId = :categoryId")
    suspend fun countByCategory(categoryId: Long): Int
}