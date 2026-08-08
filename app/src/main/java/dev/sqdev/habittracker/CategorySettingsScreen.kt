package dev.sqdev.habittracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategorySettingsScreen(
    categories: List<Category>,
    onDeleteCategory: (Category) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(categories) { category ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = category.name)
                TextButton(onClick = { onDeleteCategory(category) }) {
                    Text("Delete")
                }
            }
        }
    }
}