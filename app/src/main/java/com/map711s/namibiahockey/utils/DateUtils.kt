package com.map711s.namibiahockey.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object DateUtils {

    private val displayDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
    private val fullDateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())

    fun formatDisplayDate(date: Date): String = displayDateFormat.format(date)

    fun formatShortDate(date: Date): String = shortDateFormat.format(date)

    fun formatFullDate(date: Date): String = fullDateFormat.format(date)

    fun formatLocalDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }

    fun calculateAge(birthDate: LocalDate): Int {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now()).toInt()
    }

    fun getRelativeTime(date: Date): String {
        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

        return when {
            diffInDays == 0L -> "Today"
            diffInDays == 1L -> "Yesterday"
            diffInDays < 7 -> "$diffInDays days ago"
            diffInDays < 30 -> "${diffInDays / 7} weeks ago"
            diffInDays < 365 -> "${diffInDays / 30} months ago"
            else -> "${diffInDays / 365} years ago"
        }
    }

    fun isInFuture(date: LocalDate): Boolean {
        return date.isAfter(LocalDate.now())
    }

    fun isInPast(date: LocalDate): Boolean {
        return date.isBefore(LocalDate.now())
    }
}