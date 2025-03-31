package com.map711s.namibiahockey.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Namibia Hockey") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Welcome Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to Namibia Hockey",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your all-in-one app for team management, events, and real-time updates",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Quick Action Buttons
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton(
                    icon = Icons.Filled.Group,
                    title = "Register Team",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onClick = { /* Navigate to team registration */ }
                )

                QuickActionButton(
                    icon = Icons.Filled.Person,
                    title = "Add Player",
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    onClick = { /* Navigate to player registration */ }
                )

                QuickActionButton(
                    icon = Icons.Filled.CalendarMonth,
                    title = "View Events",
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    onClick = { /* Navigate to events screen */ }
                )
            }

            // News Feed
            Text(
                text = "Latest News",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn {
                items(sampleNewsList) { newsItem ->
                    NewsItem(newsItem = newsItem)
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class NewsItem(
    val title: String,
    val date: String,
    val summary: String
)

@Composable
fun NewsItem(newsItem: NewsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = newsItem.title,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = newsItem.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = newsItem.summary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Sample news items for demonstration
val sampleNewsList = listOf(
    NewsItem(
        title = "National Hockey Tournament Announced",
        date = "March 30, 2025",
        summary = "The annual National Hockey Tournament will take place in Windhoek from June 15-20. Team registrations open next week."
    ),
    NewsItem(
        title = "New Training Schedule Released",
        date = "March 25, 2025",
        summary = "The Namibia Hockey Union has released a new training schedule for all registered teams. Check the Events tab for details."
    ),
    NewsItem(
        title = "Player Transfer Window Opens",
        date = "March 20, 2025",
        summary = "The mid-season transfer window is now open. Teams can register new players until April 15."
    ),
    NewsItem(
        title = "Youth Development Program Success",
        date = "March 15, 2025",
        summary = "The youth development program has successfully trained over 100 new players this quarter. Congratulations to all participants!"
    )
)