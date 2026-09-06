package dev.sqdev.habittracker

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusDirection

@Composable
fun FoodEntryScreen(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    selectedCategory: Category?,
    editingEntry: FoodEntry? = null,
    onSave: (Long, String, String?) -> Unit,
    onSaveAndAddAnother: ((Long, String, String?) -> Unit)? = null,
    onBack: () -> Unit
) {
    var mealName by remember { mutableStateOf(editingEntry?.name ?: "") }
    var remark by remember { mutableStateOf(editingEntry?.note ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

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

        Spacer(modifier = Modifier.weight(1f))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {

                // Left column: meal name + remark
                Column(modifier = Modifier.weight(0.62f)) {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text("What did you eat?") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = remark,
                        onValueChange = { remark = it },
                        label = { Text("Remark") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Right column: Save buttons
                Column(
                    modifier = Modifier.weight(0.38f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }

                    if (editingEntry == null && onSaveAndAddAnother != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                when {
                                    selectedCategory == null -> errorMessage = "Please select a meal type"
                                    mealName.isBlank() -> errorMessage = "Please enter what you ate"
                                    else -> {
                                        errorMessage = null
                                        onSaveAndAddAnother(selectedCategory.id, mealName.trim(), remark.ifBlank { null })
                                        mealName = ""
                                        remark = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Save and add another")
                        }
                    }
                }
            }
        }
    }
}