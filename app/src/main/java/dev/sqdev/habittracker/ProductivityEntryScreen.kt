package dev.sqdev.habittracker

import androidx.compose.foundation.clickable
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
fun ProductivityEntryScreen(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    selectedCategory: Category?,
    onSave: (Long, Int, Int, String?) -> Unit,
    onBack: () -> Unit
) {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var activeField by remember { mutableStateOf(NumpadField.HOURS) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select Category", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(categories) { category ->
                val isSelected = category.id == selectedCategory?.id
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = hours,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hrs") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (activeField == NumpadField.HOURS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = if (activeField == NumpadField.HOURS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { activeField = NumpadField.HOURS })
            }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = minutes,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Min") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (activeField == NumpadField.MINUTES) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = if (activeField == NumpadField.MINUTES) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { activeField = NumpadField.MINUTES })
            }
            OutlinedTextField(
                value = remark,
                onValueChange = { remark = it },
                label = { Text("Remark") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TimeNumpad(
            onDigitPress = { digit ->
                if (activeField == NumpadField.HOURS) {
                    val newValue = (hours + digit).toIntOrNull() ?: 0
                    hours = if (newValue > 23) "23" else newValue.toString()
                } else {
                    val newValue = (minutes + digit).toIntOrNull() ?: 0
                    minutes = if (newValue > 59) "59" else newValue.toString()
                }
            },
            onBackspace = {
                if (activeField == NumpadField.HOURS) hours = hours.dropLast(1) else minutes = minutes.dropLast(1)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                val h = hours.toIntOrNull() ?: 0
                val m = minutes.toIntOrNull() ?: 0
                when {
                    h !in 0..23 -> errorMessage = "Hours must be between 0 and 23"
                    m !in 0..59 -> errorMessage = "Minutes must be between 0 and 59"
                    else -> {
                        errorMessage = null
                        selectedCategory?.let { category ->
                            onSave(category.id, h, m, remark.ifBlank { null })
                        }
                    }
                }
            },
            enabled = selectedCategory != null && (hours.isNotEmpty() || minutes.isNotEmpty()),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Entry")
        }
    }
}