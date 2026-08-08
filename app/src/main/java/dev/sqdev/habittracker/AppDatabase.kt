package dev.sqdev.habittracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Category::class, ProductivityEntry::class, FoodEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productivityEntryDao(): ProductivityEntryDao
    abstract fun foodEntryDao(): FoodEntryDao
}