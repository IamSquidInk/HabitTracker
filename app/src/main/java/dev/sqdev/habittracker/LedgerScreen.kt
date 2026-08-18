package dev.sqdev.habittracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

@Composable
fun LedgerScreen(viewModel: LedgerViewModel = viewModel()) {
    val selectedLedger by viewModel.selectedLedger.collectAsState()
    val productivityEntries by viewModel.productivityEntries.collectAsState()
    val foodEntries by viewModel.foodEntries.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val showEntryScreen by viewModel.showEntryScreen.collectAsState()
    val showSettingsScreen by viewModel.showSettingsScreen.collectAsState()
    val showCategoryManagement by viewModel.showCategoryManagement.collectAsState()
    val pendingDeleteCategory by viewModel.pendingDeleteCategory.collectAsState()
    val pendingDeleteCount by viewModel.pendingDeleteCount.collectAsState()

    var selectedProductivityEntry by remember { mutableStateOf<ProductivityEntry?>(null) }
    var selectedFoodEntry by remember { mutableStateOf<FoodEntry?>(null) }

    pendingDeleteCategory?.let { category ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeleteCategory() },
            title = { Text("Delete \"${category.name}\"?") },
            text = {
                if (pendingDeleteCount > 0) {
                    Text("This category has $pendingDeleteCount entries. They'll be kept, but the category will be hidden.")
                } else {
                    Text("This category has no entries and will be permanently removed.")
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteCategory() }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDeleteCategory() }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Bottom sheet for a selected Productivity entry
    selectedProductivityEntry?.let { entry ->
        val category = categories[entry.categoryId]
        EntryDetailSheet(
            icon = category?.icon ?: "❓",
            title = category?.name ?: "Unknown",
            trailing = "${entry.hours}h ${entry.minutes}m",
            details = listOf(
                "Date" to entry.date,
                "Time" to entry.time,
                "Remark" to (entry.note ?: "-")
            ),
            onEdit = { selectedProductivityEntry = null },
            onDuplicate = {
                viewModel.duplicateProductivityEntry(entry)
                selectedProductivityEntry = null
            },
            onDelete = {
                viewModel.deleteProductivityEntry(entry)
                selectedProductivityEntry = null
            },
            onDismiss = { selectedProductivityEntry = null }
        )
    }

    // Bottom sheet for a selected Food entry
    selectedFoodEntry?.let { entry ->
        val category = categories[entry.categoryId]
        EntryDetailSheet(
            icon = category?.icon ?: "❓",
            title = entry.name,
            trailing = "",
            details = listOf(
                "Date" to entry.date,
                "Time" to entry.time,
                "Meal Type" to (category?.name ?: "Unknown"),
                "Remark" to (entry.note ?: "-")
            ),
            onEdit = { selectedFoodEntry = null },
            onDuplicate = {
                viewModel.duplicateFoodEntry(entry)
                selectedFoodEntry = null
            },
            onDelete = {
                viewModel.deleteFoodEntry(entry)
                selectedFoodEntry = null
            },
            onDismiss = { selectedFoodEntry = null }
        )
    }

    if (showEntryScreen) {
        var selectedCategory by remember { mutableStateOf<Category?>(null) }

        when (selectedLedger) {
            SelectedLedger.PRODUCTIVITY -> {
                val productivityCategories = categories.values.filter { it.ledgerType == LedgerType.PRODUCTIVITY }
                ProductivityEntryScreen(
                    categories = productivityCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onSave = { categoryId, hours, minutes, note ->
                        viewModel.addProductivityEntry(categoryId, hours, minutes, note)
                        viewModel.closeEntryScreen()
                    },
                    onBack = { viewModel.closeEntryScreen() }
                )
            }
            SelectedLedger.FOOD -> {
                val foodCategories = categories.values.filter { it.ledgerType == LedgerType.FOOD }
                FoodEntryScreen(
                    categories = foodCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onSave = { categoryId, name, note ->
                        viewModel.addFoodEntry(categoryId, name, note)
                        viewModel.closeEntryScreen()
                    },
                    onBack = { viewModel.closeEntryScreen() }
                )
            }
        }
        return
    }

    if (showSettingsScreen) {
        if (showCategoryManagement) {
            CategorySettingsScreen(
                categories = categories.values.toList(),
                onDeleteCategory = { category -> viewModel.requestDeleteCategory(category) },
                onEditCategory = { category, newName -> viewModel.updateCategoryName(category, newName) },
                onAddCategory = { name, ledgerType -> viewModel.addCategory(name, "📌", ledgerType) },
                onBack = { viewModel.closeCategoryManagement() }
            )
        } else {
            SettingsScreen(
                onManageCategories = { viewModel.openCategoryManagement() },
                onBack = { viewModel.closeSettingsScreen() }
            )
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.openSettingsScreen() }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                SelectedLedger.entries.forEach { ledger ->
                    val isSelected = ledger == selectedLedger
                    Button(
                        onClick = { viewModel.selectLedger(ledger) },
                        modifier = Modifier.weight(1f).padding(4.dp)
                    ) {
                        Text(ledger.name)
                    }
                }
            }

            SummaryCard(ledger = selectedLedger, viewModel = viewModel)

            when (selectedLedger) {
                SelectedLedger.PRODUCTIVITY -> {
                    val grouped = productivityEntries.groupBy { it.date }
                    LazyColumn {
                        grouped.forEach { (date, entries) ->
                            item { DateHeader(date) }
                            items(entries) { entry ->
                                val category = categories[entry.categoryId]
                                EntryRow(
                                    time = entry.time,
                                    categoryName = category?.name ?: "Unknown",
                                    note = entry.note,
                                    trailing = "${entry.hours}h ${entry.minutes}m",
                                    onClick = { selectedProductivityEntry = entry }
                                )
                            }
                        }
                    }
                }
                SelectedLedger.FOOD -> {
                    val grouped = foodEntries.groupBy { it.date }
                    LazyColumn {
                        grouped.forEach { (date, entries) ->
                            item { DateHeader(date) }
                            items(entries) { entry ->
                                val category = categories[entry.categoryId]
                                EntryRow(
                                    time = entry.time,
                                    categoryName = "${category?.name ?: "Unknown"} - ${entry.name}",
                                    note = entry.note,
                                    trailing = "",
                                    onClick = { selectedFoodEntry = entry }
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.openEntryScreen() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+")
        }
    }
}

@Composable
fun SummaryCard(ledger: SelectedLedger, viewModel: LedgerViewModel) {
    val monthLabel = remember {
        java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault()).format(java.util.Date()).uppercase()
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        when (ledger) {
            SelectedLedger.PRODUCTIVITY -> {
                val (hrs, mins) = viewModel.currentMonthProductivityStats.collectAsState().value
                Text("$monthLabel - PRODUCTIVITY", style = MaterialTheme.typography.labelMedium)
                Text("$hrs HRS   $mins MIN", style = MaterialTheme.typography.headlineSmall)
            }
            SelectedLedger.FOOD -> {
                val count = viewModel.currentMonthFoodCount.collectAsState().value
                Text("$monthLabel - MEALS", style = MaterialTheme.typography.labelMedium)
                Text("TOTAL MEAL LOGGED | $count", style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun EntryRow(
    time: String,
    categoryName: String,
    note: String?,
    trailing: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = time, style = MaterialTheme.typography.bodySmall)
            Text(text = categoryName + (note?.let { " – $it" } ?: ""))
        }
        Text(text = trailing)
    }
}