package dev.sqdev.habittracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

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

    val currentMonthProductivityStats: StateFlow<Pair<Int, Int>> = productivityEntries
        .map { entries ->
            val currentMonth = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault())
                .format(java.util.Date())
            val thisMonthEntries = entries.filter { it.date.startsWith(currentMonth) }
            val totalMinutes = thisMonthEntries.sumOf { it.hours * 60 + it.minutes }
            Pair(totalMinutes / 60, totalMinutes % 60)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Pair(0, 0))

    val currentMonthFoodCount: StateFlow<Int> = foodEntries
        .map { entries ->
            val currentMonth = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault())
                .format(java.util.Date())
            entries.count { it.date.startsWith(currentMonth) }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _showEntryScreen = MutableStateFlow(false)
    val showEntryScreen: StateFlow<Boolean> = _showEntryScreen

    fun openEntryScreen() { _showEntryScreen.value = true }
    fun closeEntryScreen() { _showEntryScreen.value = false }

    private val _showSettingsScreen = MutableStateFlow(false)
    val showSettingsScreen: StateFlow<Boolean> = _showSettingsScreen

    fun openSettingsScreen() { _showSettingsScreen.value = true }
    fun closeSettingsScreen() { _showSettingsScreen.value = false }

    private val _showCategoryManagement = MutableStateFlow(false)
    val showCategoryManagement: StateFlow<Boolean> = _showCategoryManagement

    fun openCategoryManagement() { _showCategoryManagement.value = true }
    fun closeCategoryManagement() { _showCategoryManagement.value = false }

    private val _pendingDeleteCategory = MutableStateFlow<Category?>(null)
    val pendingDeleteCategory: StateFlow<Category?> = _pendingDeleteCategory

    private val _pendingDeleteCount = MutableStateFlow(0)
    val pendingDeleteCount: StateFlow<Int> = _pendingDeleteCount

    fun requestDeleteCategory(category: Category) {
        viewModelScope.launch {
            val count = when (category.ledgerType) {
                LedgerType.PRODUCTIVITY -> db.productivityEntryDao().countByCategory(category.id)
                LedgerType.FOOD -> db.foodEntryDao().countByCategory(category.id)
            }
            _pendingDeleteCount.value = count
            _pendingDeleteCategory.value = category
        }
    }

    fun confirmDeleteCategory() {
        viewModelScope.launch {
            val category = _pendingDeleteCategory.value ?: return@launch
            if (_pendingDeleteCount.value == 0) {
                db.categoryDao().hardDelete(category.id)
            } else {
                db.categoryDao().softDelete(category.id)
            }
            _pendingDeleteCategory.value = null
        }
    }

    fun cancelDeleteCategory() {
        _pendingDeleteCategory.value = null
    }

    fun addCategory(name: String, icon: String, ledgerType: LedgerType) {
        viewModelScope.launch {
            db.categoryDao().insert(
                Category(
                    name = name,
                    icon = icon,
                    ledgerType = ledgerType,
                    isCustom = true
                )
            )
        }
    }

    fun updateCategoryName(category: Category, newName: String) {
        viewModelScope.launch {
            db.categoryDao().update(category.copy(name = newName))
        }
    }

}