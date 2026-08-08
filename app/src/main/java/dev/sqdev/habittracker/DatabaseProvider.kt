package dev.sqdev.habittracker

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "habit_tracker_db"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedDefaults(getDatabase(context).categoryDao())
                        }
                    }
                })
                .build()
            INSTANCE = instance
            instance
        }
    }

    private suspend fun seedDefaults(dao: CategoryDao) {
        val foodDefaults = listOf("Breakfast", "Lunch", "Dinner", "Snack")
        val productivityDefaults = listOf("Exercise", "Coding", "Studying", "Reading")

        foodDefaults.forEach {
            dao.insert(Category(name = it, icon = "🍽️", ledgerType = LedgerType.FOOD, isCustom = false))
        }
        productivityDefaults.forEach {
            dao.insert(Category(name = it, icon = "⏱️", ledgerType = LedgerType.PRODUCTIVITY, isCustom = false))
        }
    }
}