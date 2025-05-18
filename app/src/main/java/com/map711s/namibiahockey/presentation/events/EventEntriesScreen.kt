package com.map711s.namibiahockey.presentation.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.di.EventViewModelFactory
import com.map711s.namibiahockey.ui.theme.NHUSpacing
import com.map711s.namibiahockey.ui.components.errorstates.ErrorType
import com.map711s.namibiahockey.ui.components.loaders.EventListShimmer
import com.map711s.namibiahockey.ui.components.cards.NHUElevatedCard
import com.map711s.namibiahockey.ui.components.errorstates.NHUErrorScreen
import com.map711s.namibiahockey.ui.components.navigation.NHUFloatingActionButton
import com.map711s.namibiahockey.ui.components.buttons.NHUPrimaryButton
import com.map711s.namibiahockey.ui.components.navigation.NHUTabRow
import com.map711s.namibiahockey.ui.components.inputs.NHUTextField
import com.map711s.namibiahockey.ui.components.navigation.NHUTopAppBar
import com.map711s.namibiahockey.ui.components.loaders.ShimmerCardItem
import com.map711s.namibiahockey.util.WindowSize
import com.map711s.namibiahockey.util.WindowSizeClass


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEntriesScreen(
    onNavigateBack: () -> Unit,
    // viewModel: EventViewModel = hiltViewModel(),
    onNavigateToAddEvent: () -> Unit,
    windowSize: WindowSize
) {
    val viewModel: EventViewModel = viewModel(factory = EventViewModelFactory())
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val topAppBarState = listOf("Upcoming", "Past", "My Entries")

    // Get state from ViewModel
    val eventsEntriesState by viewModel.eventListState.collectAsState()
    val isLoading = eventsEntriesState.isLoading
    val isLoadingMore = eventsEntriesState.isLoadingMore
    val canLoadMore = eventsEntriesState.canLoadMore
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val error = eventsEntriesState.error
    val eventListState by viewModel.eventListState.collectAsState()
    val events = eventsEntriesState.events


    // Handle errors with Toast or Snackbar
    LaunchedEffect(eventListState.error) {
        eventListState.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.loadEvents()
    }

    // Filtered events based on search and tab
    val filteredEvents = if (searchQuery.isBlank()) {
        when (selectedTabIndex) {
            0 -> events.filter { true } // Upcoming (all for demo)
            1 -> emptyList() // Past events (empty for demo)
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

    // Determine number of columns based on window size
    val gridColumns = when (windowSize.width) {
        WindowSizeClass.COMPACT -> 1
        WindowSizeClass.MEDIUM -> 2
        WindowSizeClass.EXPANDED -> 3
    }

    Scaffold(
        topBar = {
            NHUTopAppBar(
                title = "Event Entries",
                onBackClick = onNavigateBack,
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
            NHUFloatingActionButton(
                onClick = onNavigateToAddEvent,
                icon = Icons.Default.Add,
                contentDescription = "Add Event"
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs for filtering events
            NHUTabRow(
                selectedTabIndex = selectedTabIndex,
                tabs = topAppBarState,
                onTabSelected = { selectedTabIndex = it }
            )

            // Search field
            NHUTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NHUSpacing.md),
                placeholder = "Search events...",
                leadingIcon = Icons.Default.Search,
                singleLine = true
            )

            // Show different UI based on state
            when {
                isLoading -> {
                    // Show shimmer loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (gridColumns == 1) {
                            EventListShimmer(itemCount = 3)
                        } else {
                            // Grid shimmer for tablet/desktop
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(gridColumns),
                                contentPadding = PaddingValues(NHUSpacing.md),
                                verticalArrangement = Arrangement.spacedBy(NHUSpacing.md),
                                horizontalArrangement = Arrangement.spacedBy(NHUSpacing.md)
                            ) {
                                items(gridColumns * 3) {
                                    ShimmerCardItem()
                                }
                            }
                        }
                    }
                }
                error != null -> {
                    // Show error state
                    NHUErrorScreen(
                        errorType = ErrorType.Network,
                        onRetry = { viewModel.loadAllEvents() }
                    )
                }
                filteredEvents.isEmpty() -> {
                    // Show empty state
                    NHUErrorScreen(
                        errorType = ErrorType.Empty,
                        onRetry = { searchQuery = "" }
                    )
                }
                else -> {
                    // Show content based on screen size
                    if (gridColumns == 1) {
                        // Single column list for phones
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(NHUSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(NHUSpacing.md)
                        ) {
                            items(
                                items = filteredEvents,
                                key = { it.id }
                            ) { event ->
                                EventCard(
                                    event = event,
                                    onRegisterClick = { /* Register for event */ },
                                    onViewDetailsClick = { /* View event details */ }
                                )
                            }

                            // Add some space at the bottom for the FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    } else {
                        // Multi-column grid for tablets/desktops
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridColumns),
                            contentPadding = PaddingValues(NHUSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(NHUSpacing.md),
                            horizontalArrangement = Arrangement.spacedBy(NHUSpacing.md)
                        ) {
                            items(
                                items = filteredEvents,
                                key = { it.id }
                            ) { event ->
                                EventCard(
                                    event = event,
                                    onRegisterClick = { /* Register for event */ },
                                    onViewDetailsClick = { /* View event details */ }
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
    }
}

@Composable
fun EventCard(
    event: EventEntry,
    onRegisterClick: (String) -> Unit,
    onViewDetailsClick: (String) -> Unit
) {
    NHUElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onViewDetailsClick(event.id) }
    ) {
        Column(
            modifier = Modifier.padding(NHUSpacing.md)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(NHUSpacing.sm))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(NHUSpacing.md))

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

                Spacer(modifier = Modifier.width(NHUSpacing.xs))

                Text(
                    text = "${event.startDate} - ${event.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(NHUSpacing.xs))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(NHUSpacing.xs))

                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(NHUSpacing.xs))

            Text(
                text = "Registration Deadline: ${event.registrationDeadline}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(NHUSpacing.sm))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(NHUSpacing.sm))

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
                    NHUPrimaryButton(
                        text = "Register",
                        onClick = { onRegisterClick(event.id) },
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
        }
    }
}