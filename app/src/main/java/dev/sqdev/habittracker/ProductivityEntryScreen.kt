package dev.sqdev.habittracker

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction


@Composable
fun ProductivityEntryScreen(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit,
    selectedCategory: Category?,
    editingEntry: ProductivityEntry? = null,
    onSave: (Long, Int, Int, String?) -> Unit,
    onSaveAndAddAnother: ((Long, Int, Int, String?) -> Unit)? = null,
    onBack: () -> Unit
) {
    var hours by remember { mutableStateOf(editingEntry?.hours?.toString() ?: "") }
    var minutes by remember { mutableStateOf(editingEntry?.minutes?.toString() ?: "") }
    var remark by remember { mutableStateOf(editingEntry?.note ?: "") }
    var activeField by remember { mutableStateOf(NumpadField.HOURS) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    fun validated(): Pair<Int, Int>? {
        val h = hours.toIntOrNull() ?: 0
        val m = minutes.toIntOrNull() ?: 0
        return when {
            h !in 0..23 -> { errorMessage = "Hours must be between 0 and 23"; null }
            m !in 0..59 -> { errorMessage = "Minutes must be between 0 and 59"; null }
            else -> { errorMessage = null; Pair(h, m) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Select Category", style = MaterialTheme.typography.titleMedium)
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

                // Left column: Hrs/Min fields + numpad
                Column(modifier = Modifier.weight(0.62f)) {
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
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Right column: Remark + Save buttons
                Column(
                    modifier = Modifier.weight(0.38f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = remark,
                        onValueChange = { remark = it },
                        label = { Text("Remark") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }

                    if (editingEntry == null && onSaveAndAddAnother != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                validated()?.let { (h, m) ->
                                    selectedCategory?.let { category ->
                                        onSaveAndAddAnother(category.id, h, m, remark.ifBlank { null })
                                        hours = ""
                                        minutes = ""
                                        remark = ""
                                        activeField = NumpadField.HOURS
                                    }
                                }
                            },
                            enabled = selectedCategory != null && (hours.isNotEmpty() || minutes.isNotEmpty()),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlaylistAdd, contentDescription = "Save and add another")
                        }
                    }
                }
            }
        }
    }
}