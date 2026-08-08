package dev.sqdev.habittracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LedgerScreen(viewModel: LedgerViewModel = viewModel()) {
    val selectedLedger by viewModel.selectedLedger.collectAsState()
    val productivityEntries by viewModel.productivityEntries.collectAsState()
    val foodEntries by viewModel.foodEntries.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val showEntryScreen by viewModel.showEntryScreen.collectAsState()

    if (showEntryScreen) {
        var selectedCategory by remember { mutableStateOf<Category?>(null) }
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
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Ledger switcher
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
                                    trailing = "${entry.hours}h ${entry.minutes}m"
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
                                    trailing = ""
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
fun DateHeader(date: String) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun EntryRow(time: String, categoryName: String, note: String?, trailing: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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