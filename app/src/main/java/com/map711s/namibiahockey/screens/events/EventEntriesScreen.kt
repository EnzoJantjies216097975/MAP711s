package com.map711s.namibiahockey.screens.events

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.map711s.namibiahockey.data.model.EventEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEntriesScreen(
    onNavigateBack: () -> Unit
){
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val topAppBarState = listOf("Upcoming", "Past", "My Entries")

    // Sample events for demonstration
    val events = remember {
        listOf(
            EventEntry(
                id = "1",
                title = "National Hockey Tournament",
                description = "Annual national tournament for all hockey clubs in Namibia.",
                startDate = "15 Apr 2025",
                endDate = "20 Apr 2025",
                location = "Windhoek Hockey Stadium",
                registrationDeadline = "05 Apr 2025",
                isRegistered = false,
                registeredTeams = 8
            ),
            EventEntry(
                id = "2",
                title = "Junior Development Camp",
                description = "Training camp for junior hockey players under 16.",
                startDate = "22 Apr 2025",
                endDate = "25 Apr 2025",
                location = "Swakopmund Sports Center",
                registrationDeadline = "15 Apr 2025",
                isRegistered = true,
                registeredTeams = 12
            ),
            EventEntry(
                id = "3",
                title = "Women's League - First Round",
                description = "First round of matches for the Women's Hockey League.",
                startDate = "05 May 2025",
                endDate = "10 May 2025",
                location = "Various Venues",
                registrationDeadline = "25 Apr 2025",
                isRegistered = false,
                registeredTeams = 6
            )
        )
    }

    // Filtered events based on search and tab
    val filteredEvents = if (searchQuery.isBlank()) {
        when (selectedTabIndex) {
            0 -> events.filter { true }  // Upcoming (all for demo)
            1 -> emptyList<EventEntry>() // Past events (empty for demo)
            2 -> events.filter { it.isRegistered } //My Entries
            else -> events
        }
    } else {
        events.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Entries") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                onClick = { /* Create new event */ },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Events",
                    tint = Color.White
                )
            }
        }
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
            if (filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No events found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventCard(
                            event = event,
                            onRegisterClick = { /* Register for event */},
                            onViewDetailsClick = { /* View event details */}
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
    event: EventEntry,
    onRegisterClick: (String) -> Unit,
    onViewDetailsClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
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
                    tint =  MaterialTheme.colorScheme.primary
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

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "${event.registeredTeams} teams registered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (event.isRegistered) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Registered",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Button(
                        onClick = { onRegisterClick(event.id) },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "Register")
                    }
                }
            }
        }
    }
}