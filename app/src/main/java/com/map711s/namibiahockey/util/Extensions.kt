package com.map711s.namibiahockey.util

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Extension functions to enhance functionality across the app
 */

// String extensions
fun String.isValidEmail(): Boolean {
    return this.matches(Constants.ValidationPatterns.EMAIL_PATTERN.toRegex())
}

fun String.isValidPhone(): Boolean {
    return this.matches(Constants.ValidationPatterns.PHONE_PATTERN.toRegex())
}

fun String.isValidName(): Boolean {
    return this.matches(Constants.ValidationPatterns.NAME_PATTERN.toRegex())
}

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}

// Date extensions
fun Date.toFormattedString(format: String = Constants.DateFormats.DATE_DISPLAY_FORMAT): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(this)
}

fun String.toDate(format: String = Constants.DateFormats.DATE_FORMAT): Date? {
    return try {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun Date.isToday(): Boolean {
    val today = Calendar.getInstance()
    val thisDate = Calendar.getInstance().apply { time = this@isToday }

    return today.get(Calendar.YEAR) == thisDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == thisDate.get(Calendar.DAY_OF_YEAR)
}

fun Date.isFuture(): Boolean {
    return this.after(Date())
}

fun Date.isPast(): Boolean {
    return this.before(Date())
}

// Context extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.createImageTempFile(): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = this.cacheDir
    return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
}

fun Context.getUriForFile(file: File): Uri {
    return FileProvider.getUriForFile(
        this,
        "${this.packageName}.fileprovider",
        file
    )
}

// Color extensions
fun Color.lighter(factor: Float = 0.3f): Color {
    return Color(
        red = (this.red + (1f - this.red) * factor).coerceIn(0f, 1f),
        green = (this.green + (1f - this.green) * factor).coerceIn(0f, 1f),
        blue = (this.blue + (1f - this.blue) * factor).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}

fun Color.darker(factor: Float = 0.3f): Color {
    return Color(
        red = (this.red * (1f - factor)).coerceIn(0f, 1f),
        green = (this.green * (1f - factor)).coerceIn(0f, 1f),
        blue = (this.blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}

fun Color.withAlpha(alpha: Float): Color {
    return this.copy(alpha = alpha)
}

// Collection extensions
fun <T> List<T>.isNotNullOrEmpty(): Boolean {
    return this.isNotEmpty()
}

// Integer extensions
fun Int.toOrdinalString(): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (this % 100) {
        11, 12, 13 -> "${this}th"
        else -> "$this${suffixes[this % 10]}"
    }
}

// Double extensions
fun Double.format(digits: Int): String {
    return "%.${digits}f".format(this)
}

// Uri extensions
fun Uri.getFileName(context: Context): String? {
    var result: String? = null
    if (this.scheme == "content") {
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("_display_name")
                if (columnIndex != -1) {
                    result = it.getString(columnIndex)
                }
            }
        }
    }
    if (result == null) {
        result = this.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result?.substring(cut + 1)
        }
    }
    return result
}

// File extensions
fun File.sizeInMb(): Double {
    return this.length() / (1024.0 * 1024.0)
}

// Map extensions
fun <K, V> Map<K, V>.toMutableMapWithDefaults(): MutableMap<K, V> {
    return this.toMutableMap()
}

// Player and Team related extensions
fun String.getInitials(): String {
    return this.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}