package dev.sqdev.habittracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SelectedLedger { PRODUCTIVITY, FOOD }

class LedgerViewModel(application: Application) : AndroidViewModel(application) {

    private val db = DatabaseProvider.getDatabase(application)

    private val _selectedLedger = MutableStateFlow(SelectedLedger.PRODUCTIVITY)
    val selectedLedger: StateFlow<SelectedLedger> = _selectedLedger

    val productivityEntries = db.productivityEntryDao().getAllEntries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val foodEntries = db.foodEntryDao().getAllEntries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val categories = db.categoryDao().getActiveCategories(LedgerType.PRODUCTIVITY)
        .combine(db.categoryDao().getActiveCategories(LedgerType.FOOD)) { prod, food ->
            (prod + food).associateBy { it.id }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())

    fun selectLedger(ledger: SelectedLedger) {
        _selectedLedger.value = ledger
    }

    fun addProductivityEntry(categoryId: Long, hours: Int, minutes: Int, note: String? = null) {
        viewModelScope.launch {
            val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val sdfTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val now = sdfDate.format(java.util.Date())
            val time = sdfTime.format(java.util.Date())
            db.productivityEntryDao().insert(
                ProductivityEntry(
                    categoryId = categoryId,
                    date = now,
                    time = time,
                    hours = hours,
                    minutes = minutes,
                    note = note
                )
            )
        }
    }

    fun addFoodEntry(categoryId: Long, name: String, note: String? = null) {
        viewModelScope.launch {
            val sdfDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val sdfTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val now = sdfDate.format(java.util.Date())
            val time = sdfTime.format(java.util.Date())
            db.foodEntryDao().insert(
                FoodEntry(
                    categoryId = categoryId,
                    name = name,
                    date = now,
                    time = time,
                    note = note
                )
            )
        }
    }

    private val _showEntryScreen = MutableStateFlow(false)
    val showEntryScreen: StateFlow<Boolean> = _showEntryScreen

    fun openEntryScreen() { _showEntryScreen.value = true }
    fun closeEntryScreen() { _showEntryScreen.value = false }
}