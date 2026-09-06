package dev.sqdev.habittracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip


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
    var editingProductivityEntry by remember { mutableStateOf<ProductivityEntry?>(null) }
    var editingFoodEntry by remember { mutableStateOf<FoodEntry?>(null) }
    var showReportsComingSoon by remember { mutableStateOf(false) }

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

    if (showReportsComingSoon) {
        AlertDialog(
            onDismissRequest = { showReportsComingSoon = false },
            title = { Text("Reports") },
            text = { Text("Coming soon!") },
            confirmButton = {
                TextButton(onClick = { showReportsComingSoon = false }) {
                    Text("OK")
                }
            }
        )
    }

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
            onEdit = {
                editingProductivityEntry = entry
                selectedProductivityEntry = null
                viewModel.openEntryScreen()
            },
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
            onEdit = {
                editingFoodEntry = entry
                selectedFoodEntry = null
                viewModel.openEntryScreen()
            },
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
        var selectedCategory by remember(editingProductivityEntry, editingFoodEntry) {
            mutableStateOf(
                when (selectedLedger) {
                    SelectedLedger.PRODUCTIVITY -> editingProductivityEntry?.let { categories[it.categoryId] }
                    SelectedLedger.FOOD -> editingFoodEntry?.let { categories[it.categoryId] }
                }
            )
        }

        when (selectedLedger) {
            SelectedLedger.PRODUCTIVITY -> {
                val productivityCategories = categories.values.filter { it.ledgerType == LedgerType.PRODUCTIVITY }
                ProductivityEntryScreen(
                    categories = productivityCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    editingEntry = editingProductivityEntry,
                    onSave = { categoryId, hours, minutes, note ->
                        val entry = editingProductivityEntry
                        if (entry != null) {
                            viewModel.updateProductivityEntry(entry, categoryId, hours, minutes, note)
                        } else {
                            viewModel.addProductivityEntry(categoryId, hours, minutes, note)
                        }
                        editingProductivityEntry = null
                        viewModel.closeEntryScreen()
                    },
                    onSaveAndAddAnother = { categoryId, hours, minutes, note ->
                        viewModel.addProductivityEntry(categoryId, hours, minutes, note)
                    },
                    onBack = {
                        editingProductivityEntry = null
                        viewModel.closeEntryScreen()
                    }
                )
            }
            SelectedLedger.FOOD -> {
                val foodCategories = categories.values.filter { it.ledgerType == LedgerType.FOOD }
                FoodEntryScreen(
                    categories = foodCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    editingEntry = editingFoodEntry,
                    onSave = { categoryId, name, note ->
                        val entry = editingFoodEntry
                        if (entry != null) {
                            viewModel.updateFoodEntry(entry, categoryId, name, note)
                        } else {
                            viewModel.addFoodEntry(categoryId, name, note)
                        }
                        editingFoodEntry = null
                        viewModel.closeEntryScreen()
                    },
                    onSaveAndAddAnother = { categoryId, name, note ->
                        viewModel.addFoodEntry(categoryId, name, note)
                    },
                    onBack = {
                        editingFoodEntry = null
                        viewModel.closeEntryScreen()
                    }
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
                onEditCategory = { category, newName, newIcon -> viewModel.updateCategory(category, newName, newIcon) },
                onAddCategory = { name, icon, ledgerType -> viewModel.addCategory(name, icon, ledgerType) },
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

            Text(
                text = selectedLedger.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            SummaryCard(ledger = selectedLedger, viewModel = viewModel)

            when (selectedLedger) {
                SelectedLedger.PRODUCTIVITY -> {
                    val groupedByDate = productivityEntries.groupBy { it.date }
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        groupedByDate.forEach { (date, dateEntries) ->
                            item { DateHeader(date) }
                            val groupedByHour = dateEntries.groupBy { it.time.substring(0, 2) }
                            groupedByHour.forEach { (hour, hourEntries) ->
                                item { HourHeader(hour) }
                                items(hourEntries) { entry ->
                                    val category = categories[entry.categoryId]
                                    EntryRow(
                                        categoryId = entry.categoryId,
                                        icon = category?.icon ?: "❓",
                                        categoryName = category?.name ?: "Unknown",
                                        note = entry.note,
                                        trailing = "${entry.hours}h ${entry.minutes}m",
                                        onClick = { selectedProductivityEntry = entry }
                                    )
                                }
                            }
                        }
                    }
                }
                SelectedLedger.FOOD -> {
                    val groupedByDate = foodEntries.groupBy { it.date }
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        groupedByDate.forEach { (date, dateEntries) ->
                            item { DateHeader(date) }
                            val groupedByHour = dateEntries.groupBy { it.time.substring(0, 2) }
                            groupedByHour.forEach { (hour, hourEntries) ->
                                item { HourHeader(hour) }
                                items(hourEntries) { entry ->
                                    val category = categories[entry.categoryId]
                                    EntryRow(
                                        categoryId = entry.categoryId,
                                        icon = category?.icon ?: "❓",
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = {
                            val next = if (selectedLedger == SelectedLedger.PRODUCTIVITY)
                                SelectedLedger.FOOD else SelectedLedger.PRODUCTIVITY
                            viewModel.selectLedger(next)
                        }) {
                            Text("${selectedLedger.name}  ⇄")
                        }

                        OutlinedButton(onClick = { showReportsComingSoon = true }) {
                            Text("Reports")
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { viewModel.openSettingsScreen() }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { viewModel.openEntryScreen() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 110.dp)
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
    val displayDate = remember(date) {
        try {
            val parser = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val formatter = java.text.SimpleDateFormat("EEE, MMM d", java.util.Locale.getDefault())
            formatter.format(parser.parse(date)!!)
        } catch (e: Exception) {
            date
        }
    }
    Text(
        text = displayDate,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun HourHeader(hour: String) {
    Text(
        text = "$hour:00",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 2.dp)
    )
}

@Composable
fun EntryRow(
    categoryId: Long,
    icon: String,
    categoryName: String,
    note: String?,
    trailing: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(36.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(categoryColor(categoryId))
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = categoryName + (note?.let { " – $it" } ?: ""),
                modifier = Modifier.weight(1f)
            )
            Text(text = trailing)
        }
    }
}