package dev.sqdev.habittracker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimeNumpad(
    onDigitPress: (String) -> Unit,
    onBackspace: () -> Unit
) {
    Column {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("⌫", "0", "")
        )

        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                row.forEach { key ->
                    if (key.isNotEmpty()) {
                        Button(
                            onClick = {
                                if (key == "⌫") onBackspace() else onDigitPress(key)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(key)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

enum class NumpadField { HOURS, MINUTES }