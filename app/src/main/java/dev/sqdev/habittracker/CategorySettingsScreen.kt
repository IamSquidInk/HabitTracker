package dev.sqdev.habittracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategorySettingsScreen(
    categories: List<Category>,
    onDeleteCategory: (Category) -> Unit,
    onAddCategory: (String, LedgerType) -> Unit,
    onBack: () -> Unit
) {
    val productivityCategories = categories.filter { it.ledgerType == LedgerType.PRODUCTIVITY }
    val foodCategories = categories.filter { it.ledgerType == LedgerType.FOOD }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Manage Categories", style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

            item {
                CategorySection(
                    title = "Productivity",
                    categories = productivityCategories,
                    ledgerType = LedgerType.PRODUCTIVITY,
                    onDeleteCategory = onDeleteCategory,
                    onAddCategory = onAddCategory
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                CategorySection(
                    title = "Food",
                    categories = foodCategories,
                    ledgerType = LedgerType.FOOD,
                    onDeleteCategory = onDeleteCategory,
                    onAddCategory = onAddCategory
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    title: String,
    categories: List<Category>,
    ledgerType: LedgerType,
    onDeleteCategory: (Category) -> Unit,
    onAddCategory: (String, LedgerType) -> Unit
) {
    var newCategoryName by remember { mutableStateOf("") }

    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    categories.forEach { category ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category.name)
            TextButton(onClick = { onDeleteCategory(category) }) {
                Text("Delete")
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = newCategoryName,
            onValueChange = { newCategoryName = it },
            label = { Text("New category") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = {
            if (newCategoryName.isNotBlank()) {
                onAddCategory(newCategoryName.trim(), ledgerType)
                newCategoryName = ""
            }
        }) {
            Text("Add")
        }
    }
}