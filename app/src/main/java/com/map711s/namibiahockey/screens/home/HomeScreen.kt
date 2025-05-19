// Modify HomeScreen.kt

package com.map711s.namibiahockey.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.map711s.namibiahockey.R
import com.map711s.namibiahockey.data.model.EventItem
import com.map711s.namibiahockey.data.model.NewsItem
import com.map711s.namibiahockey.screens.events.EventCard
import com.map711s.namibiahockey.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val userProfileState by viewModel.userProfileState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Namibia Hockey Union") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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

            // Notice: We've removed the Quick Access section here

            // Upcoming events
            item {
                Text(
                    text = "Upcoming Events",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Sample events for now
                val events = listOf(
                    EventItem(
                        id = "1",
                        title = "National Hockey Tournament",
                        date = "Apr 15, 2025",
                        location = "Windhoek Stadium"
                    ),
                    EventItem(
                        id = "2",
                        title = "Junior Development Camp",
                        date = "Apr 22, 2025",
                        location = "Swakopmund The Dome"
                    )
                )

                events.forEach{ event ->
                    EventCard(event = event)
                }
            }

            // Latest news
            item {
                Text(
                    text = "Latest News",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                val newsItem = listOf(
                    NewsItem(
                        id = "1",
                        title = "National Team Selection Announced",
                        summary = "The Namibia Hockey Union has announced the selection for the upcoming international tournament.",
                        date = "Mar 28, 2025"
                    ),
                    NewsItem(
                        id = "2",
                        title = "New Training Facilities Unveiled",
                        summary = "State-of-the-art training facilities for hockey players have been unveiled in Windhoek.",
                        date = "Mar 25, 2025"
                    )
                )

                newsItem.forEach { news ->
                    NewsCard(news = news)
                }
            }

            // Add some space at the bottom
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Keep other composables like WelcomeSection, EventCard, and NewsCard the same
// But modify NewsCard to remove the onClick parameter since we'll navigate with bottom bar
@Composable
fun NewsCard(news: NewsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.summary,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = news.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
        }
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