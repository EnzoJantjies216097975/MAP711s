package com.map711s.namibiahockey.screens.events

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.components.DatePickerField
import com.map711s.namibiahockey.components.HockeyTypeHeader
import com.map711s.namibiahockey.components.HockeyTypeOptions
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    hockeyType: HockeyType,
    onNavigateBack: () -> Unit,
    onNavigateToEvents: () -> Unit,
    viewModel: EventViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val eventState by viewModel.eventState.collectAsState()
    val context = LocalContext.current

    // Form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Date fields using LocalDate to properly manage dates
    var startLocalDate by remember { mutableStateOf<LocalDate?>(null) }
    var endLocalDate by remember { mutableStateOf<LocalDate?>(null) }
    var registrationDeadlineLocalDate by remember { mutableStateOf<LocalDate?>(null) }

    // Selected hockey type
    var selectedHockeyType by remember { mutableStateOf(hockeyType) }

    // Date formatter
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Validation state
    val formIsValid = title.isNotBlank() &&
            description.isNotBlank() &&
            location.isNotBlank() &&
            startLocalDate != null &&
            endLocalDate != null &&
            registrationDeadlineLocalDate != null

    // Minimum allowed dates for the pickers
    val today = LocalDate.now()

    LaunchedEffect(eventState) {
        if (eventState.isSuccess) {
            Toast.makeText(context, "Event created successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetEventState()
            onNavigateToEvents()
        }

        eventState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Event") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hockey type header
            HockeyTypeHeader(hockeyType = selectedHockeyType)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Event Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Hockey Type Selection
                    Text(
                        text = "Hockey Type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    HockeyTypeOptions(
                        selectedType = selectedHockeyType,
                        onTypeSelected = { selectedHockeyType = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        placeholder = { Text("Enter event title") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = title.isEmpty() && eventState.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Enter event description") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 3,
                        isError = description.isEmpty() && eventState.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Location field
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        placeholder = { Text("Enter event location") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        isError = location.isEmpty() && eventState.error != null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Start Date field
                    DatePickerField(
                        value = startLocalDate?.format(dateFormatter) ?: "",
                        onDateSelected = { date ->
                            startLocalDate = date
                            // If end date is before new start date, update it
                            if (endLocalDate != null && endLocalDate!!.isBefore(date)) {
                                endLocalDate = date
                            }
                        },
                        label = "Start Date",
                        placeholder = "Select start date",
                        modifier = Modifier.fillMaxWidth(),
                        minDate = today
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // End Date field
                    DatePickerField(
                        value = endLocalDate?.format(dateFormatter) ?: "",
                        onDateSelected = { date ->
                            endLocalDate = date
                        },
                        label = "End Date",
                        placeholder = "Select end date",
                        modifier = Modifier.fillMaxWidth(),
                        minDate = startLocalDate ?: today
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Registration Deadline field
                    DatePickerField(
                        value = registrationDeadlineLocalDate?.format(dateFormatter) ?: "",
                        onDateSelected = { date ->
                            registrationDeadlineLocalDate = date
                        },
                        label = "Registration Deadline",
                        placeholder = "Select registration deadline",
                        modifier = Modifier.fillMaxWidth(),
                        minDate = today,
                        maxDate = startLocalDate
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val event = EventEntry(
                        title = title,
                        description = description,
                        location = location,
                        startDate = startLocalDate?.format(dateFormatter) ?: "",
                        endDate = endLocalDate?.format(dateFormatter) ?: "",
                        registrationDeadline = registrationDeadlineLocalDate?.format(dateFormatter) ?: "",
                        registeredTeams = 0,
                        isRegistered = false,
                        hockeyType = selectedHockeyType
                    )
                    viewModel.createEvent(event)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = formIsValid && !eventState.isLoading
            ) {
                if (eventState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Add Event",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Optional: Add a visual indicator for required fields
            if (!formIsValid) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "All fields are required",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}