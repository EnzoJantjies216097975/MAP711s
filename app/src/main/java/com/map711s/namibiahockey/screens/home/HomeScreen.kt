package com.map711s.namibiahockey.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.screens.events.EventCard
import com.map711s.namibiahockey.screens.newsfeed.NewsCard
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import com.map711s.namibiahockey.viewmodel.EventViewModel
import com.map711s.namibiahockey.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    hockeyType: HockeyType,
    onNavigateToProfile: () -> Unit,
    onNavigateToEventDetails: (String, HockeyType) -> Unit,
    onNavigateToNewsDetails: (String) -> Unit,
    onSwitchHockeyType: (HockeyType) -> Unit,
    onViewAllEvents: () -> Unit = {},
    onViewAllNews: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    newsViewModel: NewsViewModel = hiltViewModel()
) {
    val userProfileState by authViewModel.userProfileState.collectAsState()
    val eventListState by eventViewModel.eventListState.collectAsState()
    val newsListState by newsViewModel.newsListState.collectAsState()
    var selectedHockeyType by remember { mutableStateOf(hockeyType) }

    // Load events and news for the selected hockey type
    LaunchedEffect(selectedHockeyType) {
        eventViewModel.loadEventsByType(selectedHockeyType)
        newsViewModel.loadNewsPiecesByType(selectedHockeyType)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text(text = "Namibia Hockey Union") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            ),
            actions = {
                // Hockey type switcher button
                IconButton(
                    onClick = {
                        val newType = if (selectedHockeyType == HockeyType.OUTDOOR)
                            HockeyType.INDOOR else HockeyType.OUTDOOR
                        onSwitchHockeyType(newType)
                        selectedHockeyType = newType
                    }
                ) {
                    Icon(
                        imageVector = if (selectedHockeyType == HockeyType.OUTDOOR)
                            Icons.Default.Home
                        else
                            Icons.Default.Business,
                        contentDescription = "Switch Hockey Type",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                IconButton(onClick = { /* Notifications */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                IconButton(onClick = onNavigateToProfile) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        )

        // Display hockey type indicator
        HockeyTypeIndicator(hockeyType = selectedHockeyType)

        // Main content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome message
            item {
                if (userProfileState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val userName = userProfileState.user?.name ?: "Hockey Enthusiast"
                    WelcomeSection(userName = userName)
                }
            }

            // Upcoming events section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Events",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = { /* Navigate to events tab */ }) {
                        Text("View All")
                    }
                }
            }

            // Events list
            if (eventListState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (eventListState.events.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No upcoming events for ${selectedHockeyType.name.lowercase()} hockey",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Show recent events (limited to 3)
                items(eventListState.events.take(3)) { event ->
                    EventCard(
                        event = event,
                        onRegisterClick = { eventId ->
                            eventViewModel.registerForEvent(eventId)
                        },
                        onViewDetailsClick = { eventId ->
                            onNavigateToEventDetails(eventId, event.hockeyType)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Latest news section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Latest News",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = { /* Navigate to news tab */ }) {
                        Text("View All")
                    }
                }
            }

            // News list
            if (newsListState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (newsListState.newsPieces.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No news for ${selectedHockeyType.name.lowercase()} hockey",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Show recent news (limited to 3)
                items(newsListState.newsPieces.take(3)) { newsPiece ->
                    NewsCard(
                        news = newsPiece,
                        onNewsClick = { onNavigateToNewsDetails(newsPiece.id) },
                        onBookmarkClick = { newsId, isBookmarked ->
                            newsViewModel.toggleBookmark(newsId, isBookmarked)
                        },
                        onShareClick = { newsId ->
                            // Handle share
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Add some space at the bottom for the bottom navigation
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HockeyTypeIndicator(hockeyType: HockeyType) {
    val backgroundColor = when (hockeyType) {
        HockeyType.OUTDOOR -> MaterialTheme.colorScheme.primaryContainer
        HockeyType.INDOOR -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${hockeyType.name.lowercase().replaceFirstChar { it.uppercase() }} Hockey",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = when (hockeyType) {
                HockeyType.OUTDOOR -> MaterialTheme.colorScheme.onPrimaryContainer
                HockeyType.INDOOR -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onTertiaryContainer
            }
        )
    }
}

@Composable
fun WelcomeSection(userName: String) {
    val firstName = userName.trim().split(" ").firstOrNull() ?: "Hockey Enthusiast"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Current date
        val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        Text(
            text = currentDate,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Welcome message
        Text(
            text = "Welcome, $firstName!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Stay updated with all hockey activities",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}