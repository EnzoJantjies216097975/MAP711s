package com.map711s.namibiahockey.screens.events

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.Event
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEntriesScreen(
    hockeyType: HockeyType,
    onNavigateBack: () -> Unit,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToEventDetails: (String, HockeyType) -> Unit,
    eventViewModel: EventViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val topAppBarState = listOf("Upcoming", "Past", "My Entries")
    val eventsEntriesState by eventViewModel.eventListState.collectAsState()
    val eventsEntries = eventsEntriesState.events
    val isLoading = eventsEntriesState.isLoading
    var selectedHockeyType by remember { mutableStateOf(hockeyType) }
    val snackbarHostState = remember { SnackbarHostState() }
    var loadingEventId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val tabs = listOf("Upcoming", "Past", "My Entries")
    val eventListState by eventViewModel.eventListState.collectAsState()
    val userProfileState by authViewModel.userProfileState.collectAsState()

    // Load events when screen opens
    LaunchedEffect(hockeyType) {
        eventViewModel.loadEventsByType(hockeyType)
    }

    // Observe event state to update loading
    LaunchedEffect(eventViewModel.eventState.collectAsState().value) {
        val eventState = eventViewModel.eventState.value
        if (!eventState.isLoading) {
            // If no longer loading, clear the loading event ID
            loadingEventId = null
        }
    }



    // Handle error messages
    LaunchedEffect(eventListState.error) {
        eventListState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Effect to show error messages
    LaunchedEffect(eventsEntriesState.error) {
        eventsEntriesState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(key1 = true) {
        eventViewModel.loadAllEvents()
    }

    // Helper function to determine if an event is in the past
    fun isEventInPast(event: Event): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = Date()
            val endDate = event.endDate.let {
                if (it.isNotEmpty()) dateFormat.parse(it) else null
            } ?: return false
            endDate.before(currentDate)
        } catch (e: Exception) {
            false
        }
    }

    // Filtered events based on search, tab, and hockey type
    val filteredEvents = if (searchQuery.isBlank()) {
        val typeFilteredEvents = eventsEntries.filter {
            it.hockeyType == selectedHockeyType || selectedHockeyType == HockeyType.BOTH
        }

        when (selectedTabIndex) {
            0 -> typeFilteredEvents.filter { !isEventInPast(it) } // Upcoming events
            1 -> typeFilteredEvents.filter { isEventInPast(it) } // Past events
            2 -> typeFilteredEvents.filter { it.isRegistered } // My entries
            else -> typeFilteredEvents
        }
    } else {
        eventsEntries.filter {
            (it.hockeyType == selectedHockeyType || selectedHockeyType == HockeyType.BOTH) &&
                    (it.title.contains(searchQuery, ignoreCase = true) ||
                            it.description.contains(searchQuery, ignoreCase = true) ||
                            it.location.contains(searchQuery, ignoreCase = true))
        }
    }

    // Check if user can add events
    val canAddEvents = userProfileState.user?.role in listOf(UserRole.ADMIN, UserRole.MANAGER, UserRole.COACH)


    // Hockey Type Selector
    TabRow(
        selectedTabIndex = if (selectedHockeyType == HockeyType.OUTDOOR) 0 else 1
    ) {
        Tab(
            selected = selectedHockeyType == HockeyType.OUTDOOR,
            onClick = { selectedHockeyType = HockeyType.OUTDOOR },
            text = { Text("Outdoor Hockey") }
        )
        Tab(
            selected = selectedHockeyType == HockeyType.INDOOR,
            onClick = { selectedHockeyType = HockeyType.INDOOR },
            text = { Text("Indoor Hockey") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Entries") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter events */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddEvent,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Events",
                    tint = Color.White
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs for filtering events
            TabRow(selectedTabIndex = selectedTabIndex) {
                topAppBarState.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search events...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                singleLine = true
            )

            // Event list
            if (isLoading && filteredEvents.isEmpty()) {
                // Show loading indicator for initial load
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredEvents.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedTabIndex) {
                            0 -> "No upcoming events found"
                            1 -> "No past events found"
                            2 -> "You haven't registered for any events"
                            else -> "No events found"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Show event list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventCard(
                            event = event,
                            onViewDetailsClick = { eventId ->
                                onNavigateToEventDetails(eventId, event.hockeyType)
                            }
                        )
                    }

                    // Add some space at the bottom for the FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onViewDetailsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep track of registration state locally to ensure UI updates
    var isRegistered by remember(event.id, event.isRegistered) {
        mutableStateOf(event.isRegistered)
    }

    var registeredTeams by remember(event.id, event.registeredTeams) {
        mutableStateOf(event.registeredTeams)
    }

    // Use useEffect to update local state when event changes
    LaunchedEffect(event) {
        isRegistered = event.isRegistered
        registeredTeams = event.registeredTeams
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewDetailsClick(event.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Event details
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${event.startDate} - ${event.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Registration Deadline: ${event.registrationDeadline}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.registeredTeams} teams registered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

            }
        }
    }
}