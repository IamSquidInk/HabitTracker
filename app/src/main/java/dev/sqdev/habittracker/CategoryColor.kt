package dev.sqdev.habittracker

import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor

fun categoryColor(categoryId: Long): Color {
    val hue = (categoryId * 47) % 360
    val hsv = floatArrayOf(hue.toFloat(), 0.35f, 0.92f)
    return Color(AndroidColor.HSVToColor(hsv))
}

val CategoryEntryTextColor = Color(0xFF1C1C1C)