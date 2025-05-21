// Modify HomeScreen.kt

package com.map711s.namibiahockey.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.map711s.namibiahockey.data.model.EventItem
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.NewsItem
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
    onSwitchHockeyType: (HockeyType) -> Unit,
    onNavigateToTeamRegistration: () -> Unit,
    onNavigateToEventEntries: () -> Unit,
    onNavigateToPlayerManagement: () -> Unit,
    onNavigateToNewsFeed: () -> Unit,
    onNavigateToProfile: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    eventViewModel: EventViewModel = hiltViewModel(),
    newsViewModel: NewsViewModel = hiltViewModel()
) {
    val userProfileState by viewModel.userProfileState.collectAsState()
    var selectedHockeyType by remember { mutableStateOf(HockeyType.BOTH) }
    val eventListState by eventViewModel.eventListState.collectAsState()
    val newsListState by newsViewModel.newsListState.collectAsState()

    // Load events and news for the selected hockey type
    LaunchedEffect(hockeyType) {
        eventViewModel.loadEventsByType(hockeyType)
        newsViewModel.loadNewsPiecesByType(hockeyType)
    }


//    TabRow(
//        selectedTabIndex = if (selectedHockeyType == HockeyType.OUTDOOR) 0 else 1
//    ) {
//        Tab(
//            selected = selectedHockeyType == HockeyType.OUTDOOR,
//            onClick = { selectedHockeyType = HockeyType.OUTDOOR },
//            text = { Text("Outdoor Hockey") }
//        )
//        Tab(
//            selected = selectedHockeyType == HockeyType.INDOOR,
//            onClick = { selectedHockeyType = HockeyType.INDOOR },
//            text = { Text("Indoor Hockey") }
//        )
//    }

        Scaffold(
            topBar = {
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
                                onSwitchHockeyType(
                                    if (hockeyType == HockeyType.OUTDOOR) HockeyType.INDOOR
                                    else HockeyType.OUTDOOR
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (hockeyType == HockeyType.OUTDOOR)
                                    Icons.Default.Home // Replace with appropriate icons
                                else
                                    Icons.Default.Business, // Replace with appropriate icons
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
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Display hockey type indicator
                HockeyTypeIndicator(hockeyType = hockeyType)

                // Rest of HomeScreen content
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
                            WelcomeSection(userName = userName, hockeyType= hockeyType)
                        }
                    }

                    // Feature cards
                    item {
                        Text(
                            text = "Quick Access",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        FeatureCardRow(
                            onTeamRegistrationClick = onNavigateToTeamRegistration,
                            onEventEntriesClick = onNavigateToEventEntries,
                            onPlayerManagmentClick = onNavigateToPlayerManagement,
                            onNewsFeedClick = onNavigateToNewsFeed
                        )
                    }

                    // Upcoming events filtered by hockey type
                    item {
                        Text(
                            text = "Upcoming Events",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        if (eventListState.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (eventListState.events.isEmpty()) {
                            Text(
                                text = "No upcoming events for ${hockeyType.name.lowercase()} hockey",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            // Filter events by hockey type
                            val filteredEvents = eventListState.events.filter { event ->
                                try {
                                    HockeyType.valueOf(event.hockeyType ?: "OUTDOOR") == hockeyType
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            // Convert to EventItem for display
                            val eventItems = filteredEvents.map { event ->
                                EventItem(
                                    id = event.id,
                                    title = event.title,
                                    date = "${event.startDate} - ${event.endDate}",
                                    location = event.location
                                )
                            }

                            if (eventItems.isEmpty()) {
                                Text(
                                    text = "No upcoming events for ${hockeyType.name.lowercase()} hockey",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                eventItems.take(3).forEach { event ->
                                    EventCard(event = event)
                                }

                                if (eventItems.size > 3) {
                                    TextButton(
                                        onClick = onNavigateToEventEntries,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Text("View all events")
                                    }
                                }
                            }
                        }
                    }

                    // Latest news filtered by hockey type
                    item {
                        Text(
                            text = "Latest News",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        if (newsListState.isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (newsListState.newsPieces.isEmpty()) {
                            Text(
                                text = "No news for ${hockeyType.name.lowercase()} hockey",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            // Filter news by hockey type
                            val filteredNews = newsListState.newsPieces.filter { news ->
                                try {
                                    HockeyType.valueOf(news.hockeyType ?: "OUTDOOR") == hockeyType
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            // Convert to NewsItem for display
                            val newsItems = filteredNews.map { news ->
                                NewsItem(
                                    id = news.id,
                                    title = news.title,
                                    summary = news.content.take(100) + "...",
                                    date = news.publishDate
                                )
                            }

                            if (newsItems.isEmpty()) {
                                Text(
                                    text = "No news for ${hockeyType.name.lowercase()} hockey",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                newsItems.take(3).forEach { news ->
                                    NewsCard(news = news, onClick = onNavigateToNewsFeed)
                                }

                                if (newsItems.size > 3) {
                                    TextButton(
                                        onClick = onNavigateToNewsFeed,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Text("View all news")
                                    }
                                }
                            }
                        }
                    }

                    // Add some space at the bottom
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

    @Composable
    fun HockeyTypeHeader(hockeyType: HockeyType) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (hockeyType == HockeyType.OUTDOOR)
                        Icons.Default.Landscape
                    else
                        Icons.Default.Home,
                    contentDescription = null,
                    tint = when (hockeyType) {
                        HockeyType.OUTDOOR -> MaterialTheme.colorScheme.onPrimaryContainer
                        HockeyType.INDOOR -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${hockeyType.name.lowercase().replaceFirstChar { it.uppercase() }} Hockey Events",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (hockeyType) {
                        HockeyType.OUTDOOR -> MaterialTheme.colorScheme.onPrimaryContainer
                        HockeyType.INDOOR -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
                )
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
