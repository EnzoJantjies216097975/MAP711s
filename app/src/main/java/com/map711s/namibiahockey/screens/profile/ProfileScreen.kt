package com.map711s.namibiahockey.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.PlayerProfile
import com.map711s.namibiahockey.data.model.PlayerProfileStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayersScreen(
    hockeyType: HockeyType,
    onNavigateBack: () -> Unit,
    onNavigateToPlayerDetails: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All Players", "National Squad", "Top Performers")
    val snackbarHostState = remember { SnackbarHostState() }

    // Sample players data - in real app, this would come from repository
    val samplePlayers = remember {
        listOf(
            PlayerProfile(
                id = "1",
                name = "Johannes Müller",
                position = "Forward",
                jerseyNumber = 10,
                teamName = "Windhoek Warriors",
                teamId = "team_1",
                hockeyType = HockeyType.OUTDOOR,
                age = 25,
                stats = PlayerProfileStats(
                    gamesPlayed = 24,
                    goalsScored = 18,
                    assists = 12,
                    yellowCards = 3,
                    redCards = 0,
                    averageRating = 8.2,
                    seasonsPlayed = 3
                ),
                isNationalPlayer = true
            ),
            PlayerProfile(
                id = "2",
                name = "Maria Kapenda",
                position = "Midfielder",
                jerseyNumber = 8,
                teamName = "Namibia Women's National Team",
                teamId = "national_outdoor_womens",
                hockeyType = HockeyType.OUTDOOR,
                age = 23,
                stats = PlayerProfileStats(
                    gamesPlayed = 32,
                    goalsScored = 8,
                    assists = 22,
                    yellowCards = 1,
                    redCards = 0,
                    averageRating = 8.7,
                    seasonsPlayed = 4
                ),
                isNationalPlayer = true
            ),
            PlayerProfile(
                id = "3",
                name = "David van der Merwe",
                position = "Goalkeeper",
                jerseyNumber = 1,
                teamName = "Swakopmund Seals",
                teamId = "team_2",
                hockeyType = HockeyType.INDOOR,
                age = 28,
                stats = PlayerProfileStats(
                    gamesPlayed = 20,
                    goalsScored = 0,
                    assists = 2,
                    yellowCards = 0,
                    redCards = 0,
                    averageRating = 7.8,
                    seasonsPlayed = 5
                ),
                isNationalPlayer = false
            ),
            PlayerProfile(
                id = "4",
                name = "Petrina Hamutenya",
                position = "Defender",
                jerseyNumber = 5,
                teamName = "Oshakati Eagles",
                teamId = "team_3",
                hockeyType = HockeyType.OUTDOOR,
                age = 26,
                stats = PlayerProfileStats(
                    gamesPlayed = 28,
                    goalsScored = 3,
                    assists = 8,
                    yellowCards = 5,
                    redCards = 1,
                    averageRating = 7.5,
                    seasonsPlayed = 2
                ),
                isNationalPlayer = false
            )
        )
    }

    // Filter players based on hockey type, search, and tab
    val filteredPlayers = samplePlayers
        .filter { player ->
            player.hockeyType == hockeyType || hockeyType == HockeyType.BOTH
        }
        .filter { player ->
            if (searchQuery.isBlank()) true
            else player.name.contains(searchQuery, ignoreCase = true) ||
                    player.position.contains(searchQuery, ignoreCase = true) ||
                    player.teamName.contains(searchQuery, ignoreCase = true)
        }
        .filter { player ->
            when (selectedTabIndex) {
                0 -> true // All players
                1 -> player.isNationalPlayer // National squad only
                2 -> player.stats.averageRating >= 8.0 // Top performers
                else -> true
            }
        }
        .sortedByDescending { it.stats.totalPoints }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Players") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filter players */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
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
        ) {
            // Hockey type indicator
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = when (hockeyType) {
                    HockeyType.OUTDOOR -> MaterialTheme.colorScheme.primaryContainer
                    HockeyType.INDOOR -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.tertiaryContainer
                }
            ) {
                Text(
                    text = "${hockeyType.name.lowercase().replaceFirstChar { it.uppercase() }} Hockey Players",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = when (hockeyType) {
                        HockeyType.OUTDOOR -> MaterialTheme.colorScheme.onPrimaryContainer
                        HockeyType.INDOOR -> MaterialTheme.colorScheme.onSecondaryContainer
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    }
                )
            }

            // Tabs for filtering players
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
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
                placeholder = { Text("Search players...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(25.dp)
            )

            // Players list
            if (filteredPlayers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedTabIndex) {
                            0 -> "No players found"
                            1 -> "No national players found"
                            2 -> "No top performers found"
                            else -> "No players found"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPlayers) { player ->
                        PlayerProfileCard(
                            player = player,
                            onClick = { onNavigateToPlayerDetails(player.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerProfileCard(
    player: PlayerProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (player.isNationalPlayer)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (player.isNationalPlayer) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "National Player",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Text(
                            text = player.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (player.isNationalPlayer) Color.White
                            else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "#${player.jerseyNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "${player.position} • Age ${player.age}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Text(
                        text = player.teamName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlayerStatItem(
                    label = "Games",
                    value = player.stats.gamesPlayed.toString()
                )
                PlayerStatItem(
                    label = "Goals",
                    value = player.stats.goalsScored.toString()
                )
                PlayerStatItem(
                    label = "Assists",
                    value = player.stats.assists.toString()
                )
                PlayerStatItem(
                    label = "Rating",
                    value = String.format("%.1f", player.stats.averageRating)
                )
            }

            if (player.isNationalPlayer) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "NATIONAL PLAYER",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}