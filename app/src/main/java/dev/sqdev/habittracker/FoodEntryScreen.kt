package dev.sqdev.habittracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FoodEntryScreen(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    selectedCategory: Category?,
    onSave: (Long, String, String?) -> Unit,
    onBack: () -> Unit
) {
    var mealName by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select Meal Type", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        categories.chunked(3).forEach { rowCategories ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCategories.forEach { category ->
                    val isSelected = category.id == selectedCategory?.id
                    OutlinedButton(
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(category.icon, style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(category.name, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                repeat(3 - rowCategories.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = mealName,
            onValueChange = { mealName = it },
            label = { Text("What did you eat?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = remark,
            onValueChange = { remark = it },
            label = { Text("Remark") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                when {
                    selectedCategory == null -> errorMessage = "Please select a meal type"
                    mealName.isBlank() -> errorMessage = "Please enter what you ate"
                    else -> {
                        errorMessage = null
                        onSave(selectedCategory.id, mealName.trim(), remark.ifBlank { null })
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Entry")
        }
    }
}