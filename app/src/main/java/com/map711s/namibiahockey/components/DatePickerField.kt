package com.map711s.namibiahockey.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onDateSelected: (LocalDate) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Text field that triggers the date picker
    OutlinedTextField(
        value = value,
        onValueChange = { /* Read-only */ },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = modifier,
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select $label"
                )
            }
        }
    )

    if (showDatePicker) {
        val today = LocalDate.now()
        val initialDate = try {
            if (value.isNotEmpty()) LocalDate.parse(value, dateFormatter) else today
        } catch (e: Exception) { today }

        val initialMillis = initialDate.atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    val afterMinDate = minDate?.let { !date.isBefore(it) } ?: true
                    val beforeMaxDate = maxDate?.let { !date.isAfter(it) } ?: true

                    return afterMinDate && beforeMaxDate
                }
            }
        )

        // Use Dialog with usePlatformDefaultWidth = false to allow us to control the width
        Dialog(
            onDismissRequest = { showDatePicker = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                // Set a fixed, generous width that will definitely fit all days
                modifier = Modifier
                    .width(400.dp)
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .fillMaxWidth()
                ) {
                    // Title with adequate space
                    Text(
                        text = "Select $label",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    // Display the selected date
                    datePickerState.selectedDateMillis?.let { millis ->
                        val displayDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))

                        Text(
                            text = displayDate,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    // Date picker with absolutely no horizontal padding to maximize space
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            title = null,
                            headline = null,
                            showModeToggle = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                onDateSelected(date)
                            }
                            showDatePicker = false
                        }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}