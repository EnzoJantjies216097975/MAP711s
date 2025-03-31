package com.map711s.namibiahockey.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onEventClick: (String) -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "My Events", "Past Events")
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { showSearch = false },
                    onClose = {
                        showSearch = false
                        searchQuery = ""
                    },
                    placeholder = { Text("Search events...") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Search suggestions could go here
                }
            } else {
                TopAppBar(
                    title = { Text("Hockey Events") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEventClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Event",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }

            // Calendar chip for date filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { /* Show date picker */ },
                    label = { Text("Filter by date") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Calendar",
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )

                AssistChip(
                    onClick = { /* Show type filter */ },
                    label = { Text("Event type") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Event,
                            contentDescription = "Event type",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            // Events list
            when (selectedTabIndex) {
                0 -> EventsList(
                    events = getSampleUpcomingEvents(),
                    onEventClick = onEventClick
                )
                1 -> EventsList(
                    events = getSampleMyEvents(),
                    onEventClick = onEventClick
                )
                2 -> EventsList(
                    events = getSamplePastEvents(),
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@Composable
fun EventsList(
    events: List<HockeyEvent>,
    onEventClick: (String) -> Unit
) {
    if (events.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No events found",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event.id) }
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: HockeyEvent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Event type chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = { },
                    label = { Text(event.type) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = getEventTypeColor(event.type).copy(alpha = 0.2f),
                        labelColor = getEventTypeColor(event.type)
                    )
                )

                if (event.isRegistrationOpen) {
                    Text(
                        text = "Registration Open",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Event title
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Event date and time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${event.startDate} - ${event.endDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Event location
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            if (event.participatingTeams.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))

                // Teams participating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${event.participatingTeams.size} teams registered",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Event description
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { /* View event details */ }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Details")
                }

                if (event.isRegistrationOpen) {
                    FilledTonalButton(
                        onClick = { /* Register for event */ }
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }
}

// Event type color-coding
fun getEventTypeColor(type: String): Color {
    return when (type) {
        "Tournament" -> Color(0xFF388E3C) // Green
        "League Match" -> Color(0xFF1976D2) // Blue
        "Training" -> Color(0xFF7B1FA2) // Purple
        "Friendly" -> Color(0xFFFFB300) // Amber
        else -> Color(0xFF757575) // Gray
    }
}

// Sample data models
data class HockeyEvent(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val type: String,
    val isRegistrationOpen: Boolean,
    val participatingTeams: List<String> = emptyList()
)

// Sample data
fun getSampleUpcomingEvents(): List<HockeyEvent> {
    return listOf(
        HockeyEvent(
            id = "1",
            title = "National Hockey Championship",
            description = "Annual national championship tournament for all premier league teams in Namibia.",
            location = "Windhoek Hockey Stadium",
            startDate = "April 15, 2025",
            endDate = "April 20, 2025",
            type = "Tournament",
            isRegistrationOpen = true,
            participatingTeams = listOf("Windhoek Warriors", "Coastal Sharks", "Northern Tigers")
        ),
        HockeyEvent(
            id = "2",
            title = "Premier League: Round 5",
            description = "Regular season matches for the Namibia Hockey Premier League.",
            location = "Various Venues",
            startDate = "April 8, 2025",
            endDate = "April 10, 2025",
            type = "League Match",
            isRegistrationOpen = false,
            participatingTeams = listOf("All Premier League Teams")
        ),
        HockeyEvent(
            id = "3",
            title = "Junior Development Camp",
            description = "Training camp for U16 and U18 players focused on skill development.",
            location = "Swakopmund Sports Complex",
            startDate = "April 25, 2025",
            endDate = "April 27, 2025",
            type = "Training",
            isRegistrationOpen = true
        ),
        HockeyEvent(
            id = "4",
            title = "Windhoek Invitational Cup",
            description = "Friendly tournament between local and international club teams.",
            location = "Windhoek Hockey Stadium",
            startDate = "May 5, 2025",
            endDate = "May 8, 2025",
            type = "Tournament",
            isRegistrationOpen = true,
            participatingTeams = listOf("Windhoek Warriors", "Cape Town Tigers", "Johannesburg Hawks")
        )
    )
}

fun getSampleMyEvents(): List<HockeyEvent> {
    return listOf(
        HockeyEvent(
            id = "2",
            title = "Premier League: Round 5",
            description = "Regular season matches for the Namibia Hockey Premier League.",
            location = "Various Venues",
            startDate = "April 8, 2025",
            endDate = "April 10, 2025",
            type = "League Match",
            isRegistrationOpen = false,
            participatingTeams = listOf("All Premier League Teams")
        ),
        HockeyEvent(
            id = "5",
            title = "Team Training Session",
            description = "Regular weekly training session for Windhoek Warriors.",
            location = "Windhoek Sports Ground",
            startDate = "April 5, 2025",
            endDate = "April 5, 2025",
            type = "Training",
            isRegistrationOpen = false
        )
    )
}

fun getSamplePastEvents(): List<HockeyEvent> {
    return listOf(
        HockeyEvent(
            id = "6",
            title = "Premier League: Round 4",
            description = "Regular season matches for the Namibia Hockey Premier League.",
            location = "Various Venues",
            startDate = "March 20, 2025",
            endDate = "March 22, 2025",
            type = "League Match",
            isRegistrationOpen = false,
            participatingTeams = listOf("All Premier League Teams")
        ),
        HockeyEvent(
            id = "7",
            title = "International Friendly: Namibia vs South Africa",
            description = "Friendly match between national teams.",
            location = "Windhoek Hockey Stadium",
            startDate = "March 15, 2025",
            endDate = "March 15, 2025",
            type = "Friendly",
            isRegistrationOpen = false,
            participatingTeams = listOf("Namibia National Team", "South Africa National Team")
        )
    )
}