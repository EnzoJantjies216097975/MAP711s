package com.map711s.namibiahockey.screens.events

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.components.GameResultsDisplay
import com.map711s.namibiahockey.components.TeamSelectionDialog
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: EventViewModel = hiltViewModel(),

    onNavigateToAddEvent: () -> Unit,
    onNavigateToEventDetails: (String, HockeyType) -> Unit = { _, _ -> },
    hockeyType: HockeyType,
    eventViewModel: EventViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val eventState by viewModel.eventState.collectAsState()
    val event by viewModel.event.collectAsState()
    val isLoading = eventState.isLoading
    val isRegistered by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "Past", "My Entries")

    val registrationState by viewModel.registrationState.collectAsState()

    var selectedTeam by remember { mutableStateOf<String?>(null) }

    val showTeamSelection by eventViewModel.showTeamSelection.collectAsState()
    val availableTeams by eventViewModel.availableTeams.collectAsState()
    val registrationMessage by eventViewModel.registrationMessage.collectAsState()
    val gameResults by eventViewModel.gameResults.collectAsState()
    val teamStats by eventViewModel.teamStats.collectAsState()
    val userProfileState by authViewModel.userProfileState.collectAsState()


    LaunchedEffect(eventState.successMessage, eventState.error) {
        eventState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }

        eventState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    // Show error message if any
    LaunchedEffect(eventState.error) {
        eventState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Handle registration success/error messages
    LaunchedEffect(registrationState.isSuccess, registrationState.error, registrationState.message) {
        when {
            registrationState.isSuccess && registrationState.message != null -> {
                Toast.makeText(context, registrationState.message, Toast.LENGTH_SHORT).show()
                viewModel.resetRegistrationState()
                // Reload event to get updated registration status
                viewModel.getEvent(eventId)
            }
            registrationState.error != null -> {
                Toast.makeText(context, registrationState.error, Toast.LENGTH_LONG).show()
                viewModel.resetRegistrationState()
            }
        }
    }

    // Show error message if any
    LaunchedEffect(eventState.error) {
        eventState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Load event when screen opens
    LaunchedEffect(eventId) {
        eventViewModel.loadEvent(eventId)

        // Check registration status if user is logged in
        userProfileState.user?.id?.let { userId ->
            eventViewModel.checkIfUserIsRegistered(eventId, userId)
        }
    }

    // Handle registration messages
    LaunchedEffect(registrationMessage) {
        registrationMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            eventViewModel.clearRegistrationMessage()
        }
    }

    // Handle event state messages
    LaunchedEffect(eventState.error, eventState.successMessage) {
        eventState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            eventViewModel.clearMessages()
        }

        eventState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            eventViewModel.clearMessages()
        }
    }

//    val currentEvents = when (selectedTabIndex) {
//        0 -> eventListState.upcomingEvents.filter {
//            it.hockeyType == hockeyType || hockeyType == HockeyType.BOTH
//        }
//        1 -> eventListState.pastEvents.filter {
//            it.hockeyType == hockeyType || hockeyType == HockeyType.BOTH
//        }
//        2 -> eventListState.myRegisteredEvents.filter {
//            it.hockeyType == hockeyType || hockeyType == HockeyType.BOTH
//        }
//        else -> emptyList()
//    }

//    // Apply search filter
//    val filteredEvents = if (searchQuery.isBlank()) {
//        currentEvents
//    } else {
//        currentEvents.filter { event ->
//            event.title.contains(searchQuery, ignoreCase = true) ||
//                    event.description.contains(searchQuery, ignoreCase = true) ||
//                    event.location.contains(searchQuery, ignoreCase = true)
//        }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && event == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (event == null) {
                Text(
                    text = "Event not found",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // Event details content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Event Title
                    Text(
                        text = event!!.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hockey Type Badge
                    Surface(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = when (event!!.hockeyType) {
                            HockeyType.OUTDOOR -> MaterialTheme.colorScheme.primaryContainer
                            HockeyType.INDOOR -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.tertiaryContainer
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "${event!!.hockeyType.name.lowercase().replaceFirstChar { it.uppercase() }} Hockey",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = when (event!!.hockeyType) {
                                HockeyType.OUTDOOR -> MaterialTheme.colorScheme.onPrimaryContainer
                                HockeyType.INDOOR -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onTertiaryContainer
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Event Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Description
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = event!!.description,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))

                            // Date and Time
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Event Date",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${event!!.startDate} - ${event!!.endDate}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Location
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Location",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = event!!.location,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Registration Deadline
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Registration Deadline",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = event!!.registrationDeadline,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isRegistrationClosed(event!!.registrationDeadline)) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    if (isRegistrationClosed(event!!.registrationDeadline)) {
                                        Text(
                                            text = "Registration Closed",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Registered Teams
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Registered Teams: ${event!!.registeredTeams}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Registration Button (only if registration is still open)
                    if (!isRegistrationClosed(event!!.registrationDeadline)) {
                        if (isRegistered) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.unregisterFromEvent(eventId)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading,
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.width(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = "Unregister",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.initiateRegistration(eventId)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.width(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = "Register for Event",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Show game results if this is a past event
                    if (isPastEvent(event!!) && eventState.gameResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Event Results",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        GameResultsDisplay(
                            gameResults = eventState.gameResults,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Team Selection Dialog
    if (eventState.showTeamSelection) {
        TeamSelectionDialog(
            teams = eventState.availableTeams,
            eventDate = event?.startDate ?: "",
            conflictingEvents = eventState.conflictingEvents,
            onTeamSelected = { teamId ->
                viewModel.registerForEvent(eventId, teamId)
            },
            onDismiss = {
                viewModel.dismissTeamSelection()
            }
        )
    }
}

// Helper function to check if registration is closed
private fun isRegistrationClosed(registrationDeadline: String): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val deadline = dateFormat.parse(registrationDeadline)
        val today = Date()
        deadline?.before(today) == true
    } catch (e: Exception) {
        false
    }
}

// Helper function to check if event is in the past
private fun isPastEvent(event: com.map711s.namibiahockey.data.model.EventEntry): Boolean {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val eventDate = dateFormat.parse(event.endDate)
        val today = Date()
        eventDate?.before(today) == true
    } catch (e: Exception) {
        false
    }
}