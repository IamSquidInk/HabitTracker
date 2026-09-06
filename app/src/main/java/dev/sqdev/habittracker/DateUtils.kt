package dev.sqdev.habittracker

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val storageFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
private val displayFormat = SimpleDateFormat("MMM d", Locale.getDefault())

fun todayDateString(): String = storageFormat.format(java.util.Date())

fun dateStringToMillis(date: String): Long {
    return try {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        utcFormat.parse(date)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

fun millisToDateString(millis: Long): String {
    val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return utcFormat.format(java.util.Date(millis))
}

fun formatDateForDisplay(date: String): String {
    return try {
        displayFormat.format(storageFormat.parse(date)!!)
    } catch (e: Exception) {
        date
    }
}