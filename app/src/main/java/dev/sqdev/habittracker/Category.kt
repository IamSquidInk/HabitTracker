package dev.sqdev.habittracker

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LedgerType {
    PRODUCTIVITY,
    FOOD
}

@Entity(tableName = "categories")

data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val ledgerType: LedgerType,
    val isCustom: Boolean = true,
    val isDeleted: Boolean = false
)