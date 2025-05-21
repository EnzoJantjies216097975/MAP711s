
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.components.HockeyTypeHeader
import com.map711s.namibiahockey.components.HockeyTypeOptions
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.viewmodel.EventViewModel
import java.util.Calendar

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
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var registrationDeadline by remember { mutableStateOf("") }
    var registeredTeams by remember { mutableStateOf(0) }
    var isRegistered by remember { mutableStateOf(false) }
    var hockeyType by remember { mutableStateOf(HockeyType.OUTDOOR) }

    var selectedHockeyType by remember { mutableStateOf(hockeyType) }

    // Date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showRegistrationDeadlinePicker by remember { mutableStateOf(false) }

    LaunchedEffect(eventState) {
        if (eventState.isSuccess) {
            Toast.makeText(context, "Event created successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetEventState() // Reset the form state
            onNavigateToEvents()
        }
        if (eventState.error != null) {
            snackbarHostState.showSnackbar(eventState.error!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Event") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                        singleLine = true
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
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Start Date field
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { }, // Date is selected via the dialog.
                        label = { Text("Start Date") },
                        placeholder = { Text("Select start date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showStartDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Select Start Date"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                    )
                    // Date Picker Dialog
                    if (showStartDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showStartDatePicker = false },
                            confirmButton = {
                                Button(onClick = {
                                    // Handle the date and format it as you need
                                    showStartDatePicker = false
                                    // You can use a Calendar instance to get the date
                                    val calendar = Calendar.getInstance()
                                    // Or use the date from date picker
                                    startDate = "2024-08-03" // hardcoded example
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showStartDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            // DatePicker() // From  androidx.compose.material3.DatePicker
                            Text("Date Picker") // placeholder
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // End Date field
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {  },
                        label = { Text("End Date") },
                        placeholder = { Text("Select end date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showEndDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Select End Date"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )
                    if (showEndDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showEndDatePicker = false },
                            confirmButton = {
                                Button(onClick = {
                                    showEndDatePicker = false
                                    endDate = "2024-08-10"
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showEndDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            Text("Date Picker")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Registration Deadline field
                    OutlinedTextField(
                        value = registrationDeadline,
                        onValueChange = {  },
                        label = { Text("Registration Deadline") },
                        placeholder = { Text("Select registration deadline") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showRegistrationDeadlinePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Select Registration Deadline"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true
                    )
                    if (showRegistrationDeadlinePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showRegistrationDeadlinePicker = false },
                            confirmButton = {
                                Button(onClick = {
                                    showRegistrationDeadlinePicker = false;
                                    registrationDeadline = "2024-07-20"
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showRegistrationDeadlinePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            Text("Date Picker")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val event = EventEntry(
                        title = title,
                        description = description,
                        location = location,
                        startDate = startDate,
                        endDate = endDate,
                        registrationDeadline = registrationDeadline,
                        registeredTeams = registeredTeams,
                        isRegistered = isRegistered,
                        hockeyType = selectedHockeyType // Store hockey type as string
                    )

                    viewModel.createEvent(event)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = title.isNotBlank() && description.isNotBlank() && location.isNotBlank() &&
                        startDate.isNotBlank() && endDate.isNotBlank() && registrationDeadline.isNotBlank()
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEventScreenPreview() {
    //  AddEventScreen(onNavigateBack = {}, onNavigateToEvents = {})
}