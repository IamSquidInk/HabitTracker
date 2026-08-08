package dev.sqdev.habittracker

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    @Insert
    suspend fun insert(entry: FoodEntry): Long

    @Update
    suspend fun update(entry: FoodEntry)

    @Delete
    suspend fun delete(entry: FoodEntry)

    @Query("SELECT * FROM food_entries ORDER BY date DESC, time DESC")
    fun getAllEntries(): Flow<List<FoodEntry>>

    @Query("SELECT COUNT(*) FROM food_entries WHERE categoryId = :categoryId")
    suspend fun countByCategory(categoryId: Long): Int
}